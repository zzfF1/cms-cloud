package com.sinosoft.common.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 手续费计算对象
 * @author: zzf
 * @create: 2023-07-16 13:19
 */
@Data
public class ChargeCalDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 8434550280904958236L;

    /**
     * 计算队列主键
     */
    private Long id;
    /**
     * 类型
     */
    private String indexCalType;
    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 中介机构
     */
    private String agentCom;
    /**
     * 手续费类型
     */
    private String chargeType;
    /**
     * 计算方式
     */
    private Long calType;
    /**
     * 激励id
     */
    private Long incentiveId;
    /**
     * 渠道 branchtypeenum
     */
    private String branchType;
    /**
     * 操作人
     */
    private Long operator;
    /**
     * 手续费实现类
     */
    private String handlerClass;
}
