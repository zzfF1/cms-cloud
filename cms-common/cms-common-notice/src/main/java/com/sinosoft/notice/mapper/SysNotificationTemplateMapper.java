package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.apache.ibatis.annotations.Param;

/**
 * 通知模板Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationTemplateMapper extends BaseMapperPlus<SysNotificationTemplate, SysNotificationTemplate> {

    /**
     * 根据代码查询模板
     *
     * @param templateCode 模板代码
     * @return 模板对象
     */
    SysNotificationTemplate selectByCode(
            @Param("templateCode") String templateCode);

}

