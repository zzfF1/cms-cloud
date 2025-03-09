package com.sinosoft.common.utils.wage;

import lombok.Data;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: cms6
 * @description: 计算参数
 * @author: zzf
 * @create: 2023-07-19 15:57
 */
@Data
public class CalParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -2438604581774401593L;

    /**
     * 算法定义
     */
    WageCalculationDefinition calculationDefinition;
    /**
     * 人员信息
     */
    Laagent agent;
    /**
     * 职级信息
     */
    Latree tree;
    /**
     * 佣金缓存对象
     */
    WageRedisUtil wageRedisUtil;
    /**
     * 过程集合
     */
    List<WageCalElements> calElementsList = new ArrayList<>();
}
