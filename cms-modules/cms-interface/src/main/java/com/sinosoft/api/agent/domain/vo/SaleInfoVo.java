package com.sinosoft.api.agent.domain.vo;

import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.broker.domain.Lasaleagent;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 第三方人员基础信息
 *
 * @author: zzf
 * @create: 2025-02-24 11:37
 */
@Data
@AutoMapper(target = Lasaleagent.class)
public class SaleInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 中介机构编码
     */
    @AutoMapping(target = "agentcom")
    private String agentCom;
    /**
     * 中介机构名称
     */
    private String name;
    /**
     * 工号
     */
    @AutoMapping(target = "salecode")
    private String saleCode;
    /**
     * 姓名
     */
    private String saleName;
}
