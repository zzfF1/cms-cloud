package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知规则Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationRuleMapper extends BaseMapperPlus<SysNotificationRule, SysNotificationRule> {

    /**
     * 查询所有启用的规则
     *
     * @return 规则列表
     */
    List<SysNotificationRule> selectActiveRules();

    /**
     * 根据事件类型查询规则
     *
     * @param eventType 事件类型
     * @return 规则列表
     */
    List<SysNotificationRule> selectByEventType(@Param("eventType") String eventType);

}
