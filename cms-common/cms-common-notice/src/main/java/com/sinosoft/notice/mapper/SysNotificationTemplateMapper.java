package com.sinosoft.notice.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 通知模板Mapper接口
 */
public interface SysNotificationTemplateMapper extends BaseMapperPlus<SysNotificationTemplate, SysNotificationTemplate> {

    /**
     * 根据代码查询模板
     */
    SysNotificationTemplate selectByCode(@Param("templateCode") String templateCode);
}
