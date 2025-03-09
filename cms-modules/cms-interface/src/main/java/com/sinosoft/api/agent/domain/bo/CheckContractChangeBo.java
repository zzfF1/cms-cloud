package com.sinosoft.api.agent.domain.bo;

import lombok.Data;

/**
 * @program: cms-cloud
 * @description:
 * @author: zzf
 * @create: 2025-02-27 19:47
 */
@Data
public class CheckContractChangeBo {
    /**
     * 团单号
     */
    private String contNo;
    /**
     * 工号
     */
    private String code;
}
