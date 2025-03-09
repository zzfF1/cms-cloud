package com.sinosoft.common.translation.core.impl;

import com.sinosoft.common.translation.annotation.TranslationType;
import com.sinosoft.common.translation.constant.TransConstant;
import com.sinosoft.common.translation.core.TranslationInterface;
import com.sinosoft.resource.api.RemoteFileService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * OSS翻译实现
 *
 * @author zzf
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

    @DubboReference(mock = "true")
    private RemoteFileService remoteFileService;

    @Override
    public String translation(Object key, String other) {
        return remoteFileService.selectUrlByIds(key.toString());
    }
}
