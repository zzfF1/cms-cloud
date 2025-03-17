package com.sinosoft.api.agent.domain.bo;

import lombok.Data;

@Data
public class CheckOrphanSingleBo {

    /**
     * 合同号
     */
    private String conNo;
    /**
     * 业务员名称
     */
    private String name;
    /**
     * 业务员编码
     */
    private String code;
    /**
     * 业务员名称
     */
    private String changeName;
    /**
     * 业务员编码
     */
    private String changeCode;
    /**
     * 变更时间
     */
    private String changeDate;




}
