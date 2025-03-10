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

    /**
     * 查询所有模板的权限处理器信息
     */
    List<Map<String, Object>> selectTemplatePermissionHandlers();

    /**
     * 根据业务类型查询模板
     */
    List<SysNotificationTemplate> selectByBusinessType(@Param("businessType") String businessType);
}
