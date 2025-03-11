package com.sinosoft.notice.service.impl;

import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.notice.domain.SysNotificationRule;
import com.sinosoft.notice.domain.SysNotificationTemplate;
import com.sinosoft.notice.domain.dto.TodoResultDTO;
import com.sinosoft.notice.mapper.SysNotificationRuleMapper;
import com.sinosoft.notice.mapper.SysNotificationTemplateMapper;
import com.sinosoft.notice.service.ITodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务待办服务实现
 *
 * @author zzf
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements ITodoService {

    private final SysNotificationRuleMapper ruleMapper;
    private final SysNotificationTemplateMapper templateMapper;

    @Override
    public List<TodoResultDTO> getUserTodoList(Long userId) {
        List<TodoResultDTO> result = new ArrayList<>();

        try {
            // 查询用户有权限的待办规则
            List<SysNotificationRule> rules = ruleMapper.selectTodoRulesByUserId(userId);
            if (rules.isEmpty()) {
                return result;
            }

            log.info("用户[{}]有权限的待办规则数量: {}", userId, rules.size());

            // 提前获取所有相关模板
            List<Long> templateIds = rules.stream()
                .map(SysNotificationRule::getTemplateId)
                .distinct()
                .collect(Collectors.toList());

            List<SysNotificationTemplate> templates = templateMapper.selectBatchIds(templateIds);
            Map<Long, SysNotificationTemplate> templateMap = templates.stream()
                .collect(Collectors.toMap(SysNotificationTemplate::getTemplateId, template -> template));

            // 按模板ID分组，视为同一业务场景
            Map<Long, List<SysNotificationRule>> templateRuleGroups = new HashMap<>();
            for (SysNotificationRule rule : rules) {
                Long templateId = rule.getTemplateId();
                if (!templateRuleGroups.containsKey(templateId)) {
                    templateRuleGroups.put(templateId, new ArrayList<>());
                }
                templateRuleGroups.get(templateId).add(rule);
            }

            // 处理每个模板组
            for (List<SysNotificationRule> templateRules : templateRuleGroups.values()) {
                // 在模板组内按SQL分组
                Map<String, List<SysNotificationRule>> sqlRuleGroups = new HashMap<>();
                for (SysNotificationRule rule : templateRules) {
                    // 只处理SQL类型规则
                    if (!"sql".equals(rule.getRuleType())) {
                        continue;
                    }

                    String sql = rule.getRuleText(); // 使用rule_text字段存储SQL
                    if (StringUtils.isEmpty(sql)) {
                        continue;
                    }

                    // 替换参数
                    sql = sql.replace("#{userId}", userId.toString());

                    // 按SQL分组
                    if (!sqlRuleGroups.containsKey(sql)) {
                        sqlRuleGroups.put(sql, new ArrayList<>());
                    }
                    sqlRuleGroups.get(sql).add(rule);
                }

                // 每个SQL只执行一次查询
                for (Map.Entry<String, List<SysNotificationRule>> entry : sqlRuleGroups.entrySet()) {
                    String sql = entry.getKey();
                    List<SysNotificationRule> ruleGroup = entry.getValue();

                    try {
                        // 执行查询
                        Integer count = ruleMapper.executeDynamicQuery(sql);

                        // 如果有数据，为每个规则创建结果
                        if (count != null && count > 0) {
                            for (SysNotificationRule rule : ruleGroup) {
                                SysNotificationTemplate template = templateMap.get(rule.getTemplateId());
                                if (template == null) {
                                    continue;
                                }

                                TodoResultDTO todoResult = new TodoResultDTO();
                                todoResult.setCount(count);
                                todoResult.setRuleId(rule.getRuleId());
                                todoResult.setRuleName(rule.getRuleName());
                                todoResult.setMenuId(rule.getMenuId());
                                todoResult.setMenuUrl(rule.getMenuUrl());
                                todoResult.setTemplateId(template.getTemplateId());
                                todoResult.setTemplateCode(template.getTemplateCode());
                                todoResult.setTemplateName(template.getTemplateName());
                                todoResult.setCreateTime(rule.getCreateTime());
                                todoResult.setPriority(template.getPriority());

                                result.add(todoResult);
                            }
                        }
                    } catch (Exception e) {
                        log.error("执行批量待办查询异常: {}", e.getMessage());
                    }
                }
            }

            // 结果排序：先按优先级，再按创建时间倒序
            result.sort((a, b) -> {
                // 优先级比较：high > medium > low
                int priorityCompare = comparePriority(a.getPriority(), b.getPriority());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }

                // 创建时间比较：降序
                if (a.getCreateTime() == null && b.getCreateTime() == null) {
                    return 0;
                } else if (a.getCreateTime() == null) {
                    return 1;
                } else if (b.getCreateTime() == null) {
                    return -1;
                } else {
                    return b.getCreateTime().compareTo(a.getCreateTime());
                }
            });

        } catch (Exception e) {
            log.error("获取用户待办列表异常", e);
        }

        return result;
    }

    /**
     * 比较优先级
     *
     * @return 负数表示a优先级高于b，正数表示a优先级低于b，0表示相等
     */
    private int comparePriority(String a, String b) {
        // 转换为数值方便比较
        int aValue = getPriorityValue(a);
        int bValue = getPriorityValue(b);
        return aValue - bValue; // 数值越小，优先级越高
    }

    /**
     * 获取优先级数值
     */
    private int getPriorityValue(String priority) {
        if ("high".equals(priority)) {
            return 1;
        } else if ("medium".equals(priority)) {
            return 2;
        } else if ("low".equals(priority)) {
            return 3;
        } else {
            return 4; // 未知优先级，最低
        }
    }
}
