package com.sinosoft.system.service;

import com.sinosoft.system.domain.SysUserPwdExpire;
import com.sinosoft.system.domain.vo.SysUserPwdExpireVo;

import java.util.List;

/**
 * 用户密码有效期管理服务接口
 *
 * @author system
 */
public interface ISysUserPwdExpireService {

    /**
     * 获取用户密码有效期信息
     * @param userId 用户ID
     * @return 密码有效期信息
     */
    SysUserPwdExpireVo getUserPwdExpireInfo(Long userId);

    /**
     * 检查用户密码是否过期
     * @param userId 用户ID
     * @return true-已过期 false-未过期
     */
    boolean isPasswordExpired(Long userId);

    /**
     * 检查用户密码是否即将过期
     * @param userId 用户ID
     * @param days 提前天数
     * @return true-即将过期 false-未即将过期
     */
    boolean isPasswordExpireSoon(Long userId, int days);

    /**
     * 重置用户密码有效期并添加历史记录
     * @param userId 用户ID
     * @param oldPassword 旧密码(用于记录历史)
     * @param newPassword 新密码(用于记录历史)
     */
    void resetPasswordExpire(Long userId, String oldPassword, String newPassword);

    /**
     * 批量检查过期密码并更新状态
     * @return 更新数量
     */
    int checkAndUpdateExpiredPasswords();

    /**
     * 发送密码即将过期警告
     * @param days 提前天数
     * @return 发送数量
     */
    int sendPasswordExpireWarning(int days);

    /**
     * 检查密码是否与历史密码重复
     * @param userId 用户ID
     * @param newPassword 新密码(已加密)
     * @return 是否重复
     */
    boolean isPasswordHistoryDuplicate(Long userId, String newPassword);

    /**
     * 添加密码历史记录
     * @param userId 用户ID
     * @param password 密码(已加密)
     */
    void addPasswordHistory(Long userId, String password);

    /**
     * 获取所有过期的用户ID列表
     * @return 用户ID列表
     */
    List<Long> getExpiredUserIdList();

    /**
     * 获取即将过期的用户ID列表
     * @param days 提前天数
     * @return 用户ID列表
     */
    List<Long> getSoonToExpireUserIdList(int days);
}
