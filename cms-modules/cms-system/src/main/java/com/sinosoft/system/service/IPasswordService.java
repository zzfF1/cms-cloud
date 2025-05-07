package com.sinosoft.system.service;

/**
 * 密码服务 - 负责密码验证、复杂度检查、历史管理和有效期管理
 */
public interface IPasswordService {
    /**
     * 检查密码复杂度
     *
     * @param password 密码
     * @param userName 用户名
     */
    void checkPasswordComplexity(String password, String userName);

    /**
     * 检查密码历史
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    void checkPasswordHistory(Long userId, String newPassword);

    /**
     * 更新用户密码并记录历史
     *
     * @param userId      用户ID
     * @param newPassword 新密码(未加密)
     * @param encryptedPassword 加密后的新密码
     */
    void updatePassword(Long userId, String newPassword, String encryptedPassword);

    /**
     * 检查密码是否已过期
     *
     * @param userId 用户ID
     * @return 是否过期
     */
    boolean isPasswordExpired(Long userId);

    /**
     * 检查密码是否即将过期
     *
     * @param userId 用户ID
     * @return 是否即将过期
     */
    boolean isPasswordSoonToExpire(Long userId);

    /**
     * 获取密码锁定时间（分钟）
     */
    int getPasswordLockDuration();

    /**
     * 获取密码最大重试次数
     */
    int getPasswordMaxRetryCount();
}
