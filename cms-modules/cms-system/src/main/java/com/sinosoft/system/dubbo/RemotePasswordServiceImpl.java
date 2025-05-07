// 在system服务中
package com.sinosoft.system.dubbo;

import com.sinosoft.system.api.RemotePasswordService;
import com.sinosoft.system.service.IPasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 密码服务远程实现
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemotePasswordServiceImpl implements RemotePasswordService {

    private final IPasswordService passwordService;

    @Override
    public void checkPasswordComplexity(String password, String userName) {
        passwordService.checkPasswordComplexity(password, userName);
    }

    @Override
    public void checkPasswordHistory(Long userId, String newPassword) {
        passwordService.checkPasswordHistory(userId, newPassword);
    }

    @Override
    public void updatePassword(Long userId, String newPassword, String encryptedPassword) {
        passwordService.updatePassword(userId, newPassword, encryptedPassword);
    }

    @Override
    public int getPasswordLockDuration() {
        return passwordService.getPasswordLockDuration();
    }

    @Override
    public int getPasswordMaxRetryCount() {
        return passwordService.getPasswordMaxRetryCount();
    }
}
