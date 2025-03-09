package com.sinosoft.common.service;

import com.sinosoft.common.domain.dto.AssessWhenCalDto;
import com.sinosoft.common.enums.AssessWayEnum;
import com.sinosoft.common.enums.BranchTypeEnum;

/**
 * 考核配置Service接口
 */
public interface IAssessConfigService {

    /**
     * 初始化考核配置
     *
     * @param assessVersionId 考核版本
     * @param branchTypeEnum  渠道
     * @param assessWayEnum   考核方式
     * @param assessDate      考核年月
     */
    AssessWhenCalDto getAssessConfig(Long assessVersionId, BranchTypeEnum branchTypeEnum, AssessWayEnum assessWayEnum, String assessDate);
}
