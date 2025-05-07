package com.sinosoft.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.system.domain.SysUserPwdExpire;
import com.sinosoft.system.domain.SysUserPwdHistory;
import com.sinosoft.system.mapper.SysUserPwdExpireMapper;
import com.sinosoft.system.mapper.SysUserPwdHistoryMapper;
import com.sinosoft.system.service.IPasswordService;
import com.sinosoft.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 密码服务 - 负责密码验证、复杂度检查、历史管理和有效期管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements IPasswordService {

    private final SysUserPwdHistoryMapper pwdHistoryMapper;
    private final SysUserPwdExpireMapper pwdExpireMapper;
    private final ISysConfigService configService;

    /**
     * 检查密码复杂度
     *
     * @param password 密码
     * @param userName 用户名
     */
    @Override
    public void checkPasswordComplexity(String password, String userName) {
        // 从配置获取密码最小长度
        int minPasswordLength = Convert.toInt(configService.selectConfigByKey("sys.password.min.length"), 8);
        // 从配置获取需要的字符类型数
        int requiredTypeCount = Convert.toInt(configService.selectConfigByKey("sys.password.min.type.count"), 3);

        // 1. 检查长度
        if (password.length() < minPasswordLength) {
            throw new ServiceException("密码长度不能小于" + minPasswordLength + "位");
        }

        // 2. 检查密码是否包含用户名
        if (userName != null && !userName.isEmpty() &&
            password.toLowerCase().contains(userName.toLowerCase())) {
            throw new ServiceException("密码不能包含用户名");
        }

        // 3. 检查字符类型
        int typeCount = 0;
        if (Pattern.compile("[A-Z]").matcher(password).find()) typeCount++;  // 大写字母
        if (Pattern.compile("[a-z]").matcher(password).find()) typeCount++;  // 小写字母
        if (Pattern.compile("[0-9]").matcher(password).find()) typeCount++;  // 数字
        if (Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) typeCount++;  // 特殊字符

        if (typeCount < requiredTypeCount) {
            throw new ServiceException("密码必须包含大写字母、小写字母、数字、特殊字符中的至少" + requiredTypeCount + "种");
        }
    }

    /**
     * 检查密码历史
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    @Override
    public void checkPasswordHistory(Long userId, String newPassword) {
        // 从配置获取密码历史保留数量
        int passwordHistoryCount = Convert.toInt(configService.selectConfigByKey("sys.password.history.count"), 5);

        List<SysUserPwdHistory> historyList = pwdHistoryMapper.selectRecentPasswordHistory(userId, passwordHistoryCount);

        for (SysUserPwdHistory history : historyList) {
            if (BCrypt.checkpw(newPassword, history.getPassword())) {
                throw new ServiceException("不能重复使用最近" + passwordHistoryCount + "次使用过的密码");
            }
        }
    }

    /**
     * 更新用户密码并记录历史
     *
     * @param userId      用户ID
     * @param newPassword 新密码(未加密)
     * @param encryptedPassword 加密后的新密码
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(Long userId, String newPassword, String encryptedPassword) {
        // 从配置获取密码有效期天数
        int passwordValidityDays = Convert.toInt(configService.selectConfigByKey("sys.password.validity.days"), 90);
        // 从配置获取密码历史保留数量
        int passwordHistoryCount = Convert.toInt(configService.selectConfigByKey("sys.password.history.count"), 5);

        // 记录密码历史
        SysUserPwdHistory history = new SysUserPwdHistory();
        history.setUserId(userId);
        history.setPassword(encryptedPassword);
        history.setCreateTime(new Date());
        history.setCreateBy(userId);
        pwdHistoryMapper.insert(history);

        // 删除过多的历史记录，只保留最近N次
        pwdHistoryMapper.deleteExcessHistory(userId, passwordHistoryCount);

        // 更新或创建密码有效期记录
        Date now = new Date();
        SysUserPwdExpire pwdExpire = pwdExpireMapper.selectById(userId);

        Date expireTime = DateUtil.offsetDay(now, passwordValidityDays);

        if (pwdExpire == null) {
            // 创建新记录
            pwdExpire = new SysUserPwdExpire();
            pwdExpire.setUserId(userId);
            pwdExpire.setTenantId("000000"); // 默认租户，实际应从上下文获取
            pwdExpire.setStartTime(now);
            pwdExpire.setExpireTime(expireTime);
            pwdExpire.setUpdateTimes(1);
            pwdExpire.setWarningFlag("0");
            pwdExpire.setStatus("0");
            pwdExpire.setCreateTime(now);
            pwdExpire.setUpdateTime(now);
            pwdExpireMapper.insert(pwdExpire);
        } else {
            // 更新现有记录
            pwdExpire.setStartTime(now);
            pwdExpire.setExpireTime(expireTime);
            pwdExpire.setUpdateTimes(pwdExpire.getUpdateTimes() + 1);
            pwdExpire.setWarningFlag("0");
            pwdExpire.setStatus("0");
            pwdExpire.setUpdateTime(now);
            pwdExpireMapper.updateById(pwdExpire);
        }

        log.info("用户ID:{} 密码已更新，下次过期时间:{}", userId, DateUtil.formatDateTime(expireTime));
    }

    /**
     * 检查密码是否已过期
     *
     * @param userId 用户ID
     * @return 是否过期
     */
    @Override
    public boolean isPasswordExpired(Long userId) {
        SysUserPwdExpire pwdExpire = pwdExpireMapper.selectById(userId);
        return pwdExpire != null && "1".equals(pwdExpire.getStatus());
    }

    /**
     * 检查密码是否即将过期
     *
     * @param userId 用户ID
     * @return 是否即将过期
     */
    @Override
    public boolean isPasswordSoonToExpire(Long userId) {
        SysUserPwdExpire pwdExpire = pwdExpireMapper.selectById(userId);
        return pwdExpire != null && "1".equals(pwdExpire.getWarningFlag());
    }

    /**
     * 获取密码锁定时间（分钟）
     */
    @Override
    public int getPasswordLockDuration() {
        return Convert.toInt(configService.selectConfigByKey("sys.password.lock.duration"), 20);
    }

    /**
     * 获取密码最大重试次数
     */
    @Override
    public int getPasswordMaxRetryCount() {
        return Convert.toInt(configService.selectConfigByKey("sys.password.retry.count"), 5);
    }
}
