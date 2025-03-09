package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationMergeConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 通知合并配置Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationMergeConfigMapper extends BaseMapperPlus<SysNotificationMergeConfig, SysNotificationMergeConfig> {
    /**
     * 根据模板代码查询合并配置
     *
     * @param templateCode 模板代码
     * @return 合并配置对象
     */
    SysNotificationMergeConfig selectByTemplateCode(
            @Param("templateCode") String templateCode);
}
