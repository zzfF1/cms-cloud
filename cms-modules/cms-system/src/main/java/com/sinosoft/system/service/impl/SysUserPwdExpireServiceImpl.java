package com.sinosoft.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.sinosoft.common.core.constant.Constants;
import com.sinosoft.common.core.constant.PasswordConstants;
import com.sinosoft.common.core.enums.FormatsType;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.system.domain.SysUserPwdExpire;
import com.sinosoft.system.domain.SysUserPwdHistory;
import com.sinosoft.system.domain.vo.SysUserPwdExpireVo;
import com.sinosoft.system.mapper.SysUserPwdExpireMapper;
import com.sinosoft.system.mapper.SysUserPwdHistoryMapper;
import com.sinosoft.system.service.ISysConfigService;
import com.sinosoft.system.service.ISysUserPwdExpireService;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户密码有效期管理服务实现
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserPwdExpireServiceImpl implements ISysUserPwdExpireService {

    private final SysUserPwdExpireMapper userPwdExpireMapper;
    private final SysUserPwdHistoryMapper userPwdHistoryMapper;
    private final ISysConfigService configService;
    private final Converter converter;

    /**
     * 获取用户密码有效期信息
     *
     * @param userId 用户ID
     * @return 密码有效期信息
     */
    @Override
    public SysUserPwdExpireVo getUserPwdExpireInfo(Long userId) {
        SysUserPwdExpire entity = userPwdExpireMapper.selectById(userId);
        if (entity == null) {
            return null;
        }
        return converter.convert(entity, SysUserPwdExpireVo.class);
    }

    /**
     * 检查用户密码是否过期
     *
     * @param userId 用户ID
     * @return true-已过期 false-未过期
     */
    @Override
    public boolean isPasswordExpired(Long userId) {
        SysUserPwdExpire entity = userPwdExpireMapper.selectById(userId);
        if (entity == null) {
            // 如果没有记录，默认为不过期
            return false;
        }

        // 如果状态为已过期，直接返回true
        if (Constants.YES.equals(entity.getStatus())) {
            return true;
        }

        // 如果过期时间为null，表示永不过期
        if (entity.getExpireTime() == null) {
            return false;
        }

        // 检查当前时间是否超过过期时间
        return new Date().after(entity.getExpireTime());
    }

    /**
     * 检查用户密码是否即将过期
     *
     * @param userId 用户ID
     * @param days   提前天数
     * @return true-即将过期 false-未即将过期
     */
    @Override
    public boolean isPasswordExpireSoon(Long userId, int days) {
        // 如果未指定天数，使用系统默认值
        if (days <= 0) {
            days = getPasswordWarningDays();
        }

        SysUserPwdExpire entity = userPwdExpireMapper.selectById(userId);
        if (entity == null || entity.getExpireTime() == null) {
            // 如果没有记录或过期时间为null，表示永不过期
            return false;
        }

        // 已经过期了
        if (Constants.YES.equals(entity.getStatus()) || new Date().after(entity.getExpireTime())) {
            return false;
        }

        // 计算警告时间
        Calendar warningCal = Calendar.getInstance();
        warningCal.setTime(new Date());
        warningCal.add(Calendar.DAY_OF_MONTH, days);
        Date warningDate = warningCal.getTime();

        // 过期时间是否在警告时间之前
        return entity.getExpireTime().before(warningDate);
    }

    /**
     * 重置用户密码有效期并添加历史记录
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码(用于记录历史)
     * @param newPassword 新密码(用于记录历史)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordExpire(Long userId, String oldPassword, String newPassword) {
        // 获取密码有效期天数
        int validityDays = getPasswordValidityDays();

        // 构建密码有效期记录
        SysUserPwdExpire pwdExpire = userPwdExpireMapper.selectById(userId);
        Date now = new Date();

        if (pwdExpire == null) {
            // 新增记录
            pwdExpire = new SysUserPwdExpire();
            pwdExpire.setUserId(userId);
            pwdExpire.setStartTime(now);
            pwdExpire.setUpdateTimes(1);
            pwdExpire.setStatus("0");
            pwdExpire.setWarningFlag("0");

            if (validityDays > 0) {
                // 计算过期时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, validityDays);
                pwdExpire.setExpireTime(calendar.getTime());
            }

            userPwdExpireMapper.insert(pwdExpire);
        } else {
            // 更新记录
            pwdExpire.setStartTime(now);
            pwdExpire.setUpdateTimes(pwdExpire.getUpdateTimes() + 1);
            pwdExpire.setStatus("0");
            pwdExpire.setWarningFlag("0");
            pwdExpire.setWarningTime(null);

            if (validityDays > 0) {
                // 计算过期时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, validityDays);
                pwdExpire.setExpireTime(calendar.getTime());
            } else {
                pwdExpire.setExpireTime(null);
            }

            userPwdExpireMapper.updateById(pwdExpire);
        }

        // 如果有旧密码，添加到历史记录
        if (StringUtils.isNotBlank(oldPassword)) {
            addPasswordHistory(userId, oldPassword);
        }

        // 添加新密码到历史记录
        if (StringUtils.isNotBlank(newPassword)) {
            addPasswordHistory(userId, newPassword);
        }
    }

    /**
     * 批量检查过期密码并更新状态
     *
     * @return 更新数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int checkAndUpdateExpiredPasswords() {
        List<Long> expiredUserIds = getExpiredUserIdList();
        if (CollUtil.isEmpty(expiredUserIds)) {
            return 0;
        }

        Date now = new Date();
        userPwdExpireMapper.batchUpdateExpiredStatus(expiredUserIds, now);

        return expiredUserIds.size();
    }

    /**
     * 发送密码即将过期警告
     *
     * @param days 提前天数
     * @return 发送数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sendPasswordExpireWarning(int days) {
        // 如果未指定天数，使用系统默认值
        if (days <= 0) {
            days = getPasswordWarningDays();
        }

        List<Long> soonToExpireUserIds = getSoonToExpireUserIdList(days);
        if (CollUtil.isEmpty(soonToExpireUserIds)) {
            return 0;
        }

        // 发送消息通知
        for (Long userId : soonToExpireUserIds) {
            SysUserPwdExpire entity = userPwdExpireMapper.selectById(userId);
            if (entity != null && entity.getExpireTime() != null) {
                String expireDate = DateUtils.parseDateToStr(FormatsType.YYYY_MM_DD, entity.getExpireTime());
                String message = "您的密码将于" + expireDate + "过期，请及时修改密码。";
            }
        }

        // 更新警告标志
        Date now = new Date();
        userPwdExpireMapper.batchUpdateWarningFlag(soonToExpireUserIds, now, now);

        return soonToExpireUserIds.size();
    }

    /**
     * 检查密码是否与历史密码重复
     *
     * @param userId      用户ID
     * @param newPassword 新密码(已加密)
     * @return 是否重复
     */
    @Override
    public boolean isPasswordHistoryDuplicate(Long userId, String newPassword) {
        // 检查是否启用历史密码检查
        if (!isPasswordHistoryCheckEnabled()) {
            return false;
        }

        // 获取历史密码检查数量
        int historySize = getPasswordHistorySize();

        // 如果配置为0，则不检查历史密码
        if (historySize <= 0) {
            return false;
        }

        // 查询最近N次的密码历史
        List<SysUserPwdHistory> historyList = userPwdHistoryMapper.selectRecentPasswordHistory(userId, historySize);

        // 检查是否有重复
        for (SysUserPwdHistory history : historyList) {
            if (BCrypt.checkpw(newPassword, history.getPassword())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 添加密码历史记录
     *
     * @param userId   用户ID
     * @param password 密码(已加密)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPasswordHistory(Long userId, String password) {
        SysUserPwdHistory history = new SysUserPwdHistory();
        history.setUserId(userId);
        history.setPassword(password);
        history.setCreateTime(new Date());

        // 尝试获取当前登录用户ID，如果无法获取则使用0
        try {
            history.setCreateBy(LoginHelper.getUserId());
            history.setTenantId(LoginHelper.getTenantId());
        } catch (Exception e) {
            history.setCreateBy(0L);
            history.setTenantId("000000");
        }

        userPwdHistoryMapper.insert(history);

        // 保持历史记录数量在配置范围内，删除多余的历史记录
        int historySize = getPasswordHistorySize();
        if (historySize > 0) {
            userPwdHistoryMapper.deleteExcessHistory(userId, historySize);
        }
    }

    /**
     * 获取所有过期的用户ID列表
     *
     * @return 用户ID列表
     */
    @Override
    public List<Long> getExpiredUserIdList() {
        Date now = new Date();
        List<SysUserPwdExpire> expiredList = userPwdExpireMapper.selectExpiredPasswords(now);
        if (CollUtil.isEmpty(expiredList)) {
            return new ArrayList<>();
        }

        return expiredList.stream()
            .map(SysUserPwdExpire::getUserId)
            .collect(Collectors.toList());
    }

    /**
     * 获取即将过期的用户ID列表
     *
     * @param days 提前天数
     * @return 用户ID列表
     */
    @Override
    public List<Long> getSoonToExpireUserIdList(int days) {
        // 如果未指定天数，使用系统默认值
        if (days <= 0) {
            days = getPasswordWarningDays();
        }

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date warningTime = calendar.getTime();

        List<SysUserPwdExpire> soonToExpireList = userPwdExpireMapper.selectSoonToExpirePasswords(now, warningTime);
        if (CollUtil.isEmpty(soonToExpireList)) {
            return new ArrayList<>();
        }

        return soonToExpireList.stream()
            .map(SysUserPwdExpire::getUserId)
            .collect(Collectors.toList());
    }

    /**
     * 获取密码有效期天数
     * 首先尝试从配置服务获取，如果获取失败则使用默认值
     *
     * @return 密码有效期天数
     */
    private int getPasswordValidityDays() {
        String configValue = configService.selectConfigByKey(PasswordConstants.CONFIG_KEY_VALIDITY_DAYS);
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                return Integer.parseInt(configValue);
            } catch (NumberFormatException e) {
                log.error("密码有效期配置值格式错误: {}", configValue, e);
            }
        }
        return PasswordConstants.PASSWORD_VALIDITY_DAYS;
    }

    /**
     * 获取密码过期前提醒天数
     * 首先尝试从配置服务获取，如果获取失败则使用默认值
     *
     * @return 提醒天数
     */
    private int getPasswordWarningDays() {
        String configValue = configService.selectConfigByKey(PasswordConstants.CONFIG_KEY_WARNING_DAYS);
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                return Integer.parseInt(configValue);
            } catch (NumberFormatException e) {
                log.error("密码提醒天数配置值格式错误: {}", configValue, e);
            }
        }
        return PasswordConstants.PASSWORD_WARNING_DAYS;
    }

    /**
     * 获取历史密码检查数量
     * 首先尝试从配置服务获取，如果获取失败则使用默认值
     *
     * @return 历史密码检查数量
     */
    private int getPasswordHistorySize() {
        String configValue = configService.selectConfigByKey(PasswordConstants.CONFIG_KEY_HISTORY_SIZE);
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                return Integer.parseInt(configValue);
            } catch (NumberFormatException e) {
                log.error("历史密码检查数量配置值格式错误: {}", configValue, e);
            }
        }
        return PasswordConstants.PASSWORD_HISTORY_SIZE;
    }

    /**
     * 检查是否启用历史密码检查
     * 首先尝试从配置服务获取，如果获取失败则使用默认值
     *
     * @return 是否启用
     */
    private boolean isPasswordHistoryCheckEnabled() {
        String configValue = configService.selectConfigByKey(PasswordConstants.CONFIG_KEY_HISTORY_CHECK);
        if (StringUtils.isNotEmpty(configValue)) {
            return "Y".equalsIgnoreCase(configValue);
        }
        return "Y".equalsIgnoreCase(PasswordConstants.PASSWORD_HISTORY_CHECK);
    }
}
