package com.sinosoft.common.domain.dto;

import com.sinosoft.common.schema.common.domain.LaAssessCalIndexInfo;
import lombok.Data;
import com.sinosoft.common.schema.common.domain.LaAgentAssess;
import com.sinosoft.common.schema.common.domain.LaAssessCalIndexConfig;
import com.sinosoft.common.schema.common.domain.LaAssessConfig;

/**
 * @program: cms6
 * @description: 考核计算对象
 * @author: zzf
 * @create: 2024-01-24 16:48
 */
@Data
public class AssCalDto {
    /**
     * 考核人员
     */
    private LaAgentAssess agentAssess;
    /**
     * 考核配置
     */
    private LaAssessConfig assessConfig;
    /**
     * 指标配置对象
     */
    private LaAssessCalIndexConfig calIndexConfig;
    /**
     * 计算对象
     */
    private LaAssessCalIndexInfo calIndexInfo;
}
