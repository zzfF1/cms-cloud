package com.sinosoft.common.service;

import com.sinosoft.common.domain.dto.CommissionCalMainDto;
import com.sinosoft.common.domain.dto.WageCalMainDto;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.enums.IndexcalTypeEnum;
import com.sinosoft.common.schema.commission.domain.LaWageIndexInfo;
import com.sinosoft.common.schema.common.domain.BaseLawVersion;
import com.sinosoft.common.schema.common.domain.WageCalElementsConfig;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;

import java.util.List;

/**
 * 佣金Service接口
 */
public interface ICommissionService {
    BaseLawVersion selectByBranchTypeAndCalType(String branchType, String indexCalTYpe);

    BaseLawVersion selectById(Long id);

    /**
     * @param wageCalDelayedDto
     * @return
     */
    boolean clearAndinitCalRunParms(CommissionCalMainDto wageCalDelayedDto);

    /**
     * @param version
     * @param calElement
     * @return
     */
    List<WageCalElementsConfig> getCalElementsList(int version, String calElement);

    /**
     * @param branchTypeEnum
     * @param indexcalTypeEnum
     * @param version
     * @return
     */
    List<WageCalculationDefinition> getOutExcelDefinitionList(BranchTypeEnum branchTypeEnum, IndexcalTypeEnum indexcalTypeEnum, int version);

    /**
     * @param indexcalTypeEnum
     * @param wageNo
     * @param calElements
     * @param agentCodes
     * @return
     */
    List<LaWageIndexInfo> getWageIndexInfoList(IndexcalTypeEnum indexcalTypeEnum, String wageNo, String calElements, List<String> agentCodes);
}
