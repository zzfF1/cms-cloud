package com.sinosoft.common.utils.rule;

import lombok.Data;

/**
 * @program: cms6
 * @description: 规则计算结果
 * @author: zzf
 * @create: 2023-07-02 09:18
 */
@Data
public class RuleExecutionRecord {
    /**
     * 规则
     */
    private Long ruleid;
    /**
     * 计算结果
     */
    private boolean executionResult;
    /**
     * 计算次数
     */
    private int executionCount;

    public RuleExecutionRecord(Long id) {
        this.ruleid = id;
        this.executionResult = false;
        this.executionCount = 0;
    }

    /**
     * 增加计算次数
     */
    public void incrementExecutionCount() {
        this.executionCount++;
    }

}
