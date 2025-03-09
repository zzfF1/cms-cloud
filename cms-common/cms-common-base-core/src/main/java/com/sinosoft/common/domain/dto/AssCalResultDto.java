package com.sinosoft.common.domain.dto;

import lombok.Data;
import com.sinosoft.common.schema.common.domain.LaAssessCalIndexInfo;
import com.sinosoft.common.schema.common.domain.LaAssessCalResult;

/**
 * @program: cms6
 * @description: 考核计算结果对象
 * @author: zzf
 * @create: 2024-01-25 14:24
 */
@Data
public class AssCalResultDto {
    /**
     * 考核计算结果
     */
    LaAssessCalResult calResult;
    /**
     * 考核明细结果
     */
    LaAssessCalIndexInfo calIndexInfo;
}
