package com.sinosoft.common.translation.core.impl;

import com.sinosoft.common.translation.annotation.TranslationType;
import com.sinosoft.common.translation.constant.TransConstant;
import com.sinosoft.common.translation.core.TranslationInterface;
import com.sinosoft.system.api.RemoteUserService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 用户名翻译实现
 *
 * @author zzf
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    @DubboReference
    private RemoteUserService remoteUserService;

    @Override
    public String translation(Object key, String other) {
        return remoteUserService.selectUserNameById((Long) key);
    }
}
