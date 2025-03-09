package com.sinosoft.common.domain.dto;

import lombok.Data;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;

import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: 要计算的人员对象
 * @author: zzf
 * @create: 2023-07-19 23:39
 */
@Data
public class CalPerson {

    /**
     * 要计算的人员
     */
    List<WageCalAgentDto> wageCalAgentDtoList;
    /**
     * 人员信息
     */
    Map<String, Laagent> laAgentMap;
    /**
     * 人员职级信息
     */
    Map<String, Latree> laTreeMap;
}
