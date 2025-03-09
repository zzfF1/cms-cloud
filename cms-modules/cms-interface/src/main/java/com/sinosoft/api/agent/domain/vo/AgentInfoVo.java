package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.agent.domain.Laagent;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 人员基础信息
 *
 * @author: zzf
 * @create: 2025-02-24 11:37
 */
@Data
@AutoMapper(target = Laagent.class)
public class AgentInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 工号
     */
    @AutoMapping(target = "agentcode")
    private String agentCode;
    /**
     * 姓名
     */
    private String name;
}
