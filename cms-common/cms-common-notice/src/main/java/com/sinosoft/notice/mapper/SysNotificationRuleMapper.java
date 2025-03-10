package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationRule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 通知规则Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationRuleMapper extends BaseMapperPlus<SysNotificationRule, SysNotificationRule> {

    /**
     * 执行动态SQL查询
     * @param sql 动态SQL语句
     * @return 查询结果
     */
    @Select("${sql}")
    Integer executeDynamicQuery(@Param("sql") String sql);

    /**
     * 查询所有启用的待办规则
     */
    List<SysNotificationRule> selectTodoRulesByUserId(@Param("userId") Long userId);
}
