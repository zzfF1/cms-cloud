package com.sinosoft.auth.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.apache.dubbo.config.annotation.DubboReference;
import com.sinosoft.auth.form.RegisterBody;
import com.sinosoft.auth.properties.CaptchaProperties;
import com.sinosoft.auth.properties.UserPasswordProperties;
import com.sinosoft.common.core.constant.CacheConstants;
import com.sinosoft.common.core.constant.Constants;
import com.sinosoft.common.core.constant.GlobalConstants;
import com.sinosoft.common.core.constant.TenantConstants;
import com.sinosoft.common.core.enums.LoginType;
import com.sinosoft.common.core.enums.TenantStatus;
import com.sinosoft.common.core.enums.UserType;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.exception.user.CaptchaException;
import com.sinosoft.common.core.exception.user.CaptchaExpireException;
import com.sinosoft.common.core.exception.user.UserException;
import com.sinosoft.common.core.utils.MessageUtils;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.log.event.LogininforEvent;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.tenant.exception.TenantException;
import com.sinosoft.common.tenant.helper.TenantHelper;
import com.sinosoft.system.api.RemoteSocialService;
import com.sinosoft.system.api.RemoteTenantService;
import com.sinosoft.system.api.RemoteUserService;
import com.sinosoft.system.api.domain.bo.RemoteSocialBo;
import com.sinosoft.system.api.domain.bo.RemoteUserBo;
import com.sinosoft.system.api.domain.vo.RemoteSocialVo;
import com.sinosoft.system.api.domain.vo.RemoteTenantVo;
import com.sinosoft.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 登录校验方法
 *
 * @author cms
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysLoginService {

    @DubboReference
    private RemoteUserService remoteUserService;
    @DubboReference
    private RemoteTenantService remoteTenantService;
    @DubboReference
    private RemoteSocialService remoteSocialService;

    @Autowired
    private UserPasswordProperties userPasswordProperties;
    @Autowired
    private final CaptchaProperties captchaProperties;

    /**
     * 绑定第三方用户
     *
     * @param authUserData 授权响应实体
     */
    @Lock4j
    public void socialRegister(AuthUser authUserData) {
        String authId = authUserData.getSource() + authUserData.getUuid();
        // 第三方用户信息
        RemoteSocialBo bo = BeanUtil.toBean(authUserData, RemoteSocialBo.class);
        BeanUtil.copyProperties(authUserData.getToken(), bo);
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        bo.setAuthId(authId);
        bo.setOpenId(authUserData.getUuid());
        bo.setUserName(authUserData.getUsername());
        bo.setNickName(authUserData.getNickname());
        List<RemoteSocialVo> checkList = remoteSocialService.selectByAuthId(authId);
        if (CollUtil.isNotEmpty(checkList)) {
            throw new ServiceException("此三方账号已经被绑定!");
        }
        // 查询是否已经绑定用户
        RemoteSocialBo params = new RemoteSocialBo();
        params.setUserId(userId);
        params.setSource(bo.getSource());
        List<RemoteSocialVo> list = remoteSocialService.queryList(params);
        if (CollUtil.isEmpty(list)) {
            // 没有绑定用户, 新增用户信息
            remoteSocialService.insertByBo(bo);
        } else {
            // 更新用户信息
            bo.setId(list.get(0).getId());
            remoteSocialService.updateByBo(bo);
            // 如果要绑定的平台账号已经被绑定过了 是否抛异常自行决断
            // throw new ServiceException("此平台账号已经被绑定!");
        }
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
                // 超级管理员 登出清除动态租户
                TenantHelper.clearDynamic();
            }
            recordLogininfor(loginUser.getTenantId(), loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        String tenantId = registerBody.getTenantId();
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        // 校验用户类型是否存在
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        boolean captchaEnabled = captchaProperties.getEnabled();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, registerBody.getCode(), registerBody.getUuid());
        }

        // 注册用户信息
        RemoteUserBo remoteUserBo = new RemoteUserBo();
        remoteUserBo.setTenantId(tenantId);
        remoteUserBo.setUserName(username);
        remoteUserBo.setNickName(username);
        remoteUserBo.setPassword(BCrypt.hashpw(password));
        remoteUserBo.setUserType(userType);

        boolean regFlag = remoteUserService.registerUserInfo(remoteUserBo);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }
        recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String tenantId, String username, String status, String message) {
        // 封装对象
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        SpringUtils.context().publishEvent(logininforEvent);
    }

    /**
     * 登录校验
     */
    public void checkLogin(LoginType loginType, String tenantId, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;
        Integer maxRetryCount = userPasswordProperties.getMaxRetryCount();
        Integer lockTime = userPasswordProperties.getLockTime();

        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        int errorNumber = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0);
        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            // 错误次数递增
            errorNumber++;
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数
                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    public void checkTenant(String tenantId) {
        if (!TenantHelper.isEnable()) {
            return;
        }
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return;
        }
        if (StringUtils.isBlank(tenantId)) {
            throw new TenantException("tenant.number.not.blank");
        }
        RemoteTenantVo tenant = remoteTenantService.queryByTenantId(tenantId);
        if (ObjectUtil.isNull(tenant)) {
            log.info("登录租户：{} 不存在.", tenantId);
            throw new TenantException("tenant.not.exists");
        } else if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus())) {
            log.info("登录租户：{} 已被停用.", tenantId);
            throw new TenantException("tenant.blocked");
        } else if (ObjectUtil.isNotNull(tenant.getExpireTime())
            && new Date().after(tenant.getExpireTime())) {
            log.info("登录租户：{} 已超过有效期.", tenantId);
            throw new TenantException("tenant.expired");
        }
    }
}
