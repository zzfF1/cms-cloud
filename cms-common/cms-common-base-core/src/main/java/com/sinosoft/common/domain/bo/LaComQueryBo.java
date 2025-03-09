package com.sinosoft.common.domain.bo;

import com.sinosoft.common.schema.inermediary.domain.Lacom;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 中介机构管理业务对象 la_com
 *
 * @author LXY
 * @date 2023-11-13
 */
@Data
@AutoMapper(target = Lacom.class, reverseConvertGenerate = false)
public class LaComQueryBo{

    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 管理机构
     */
    private String agentCom;
    /**
     * 渠道
     */
    private String branchType;
    /**
     * 类型
     */
    private String agentType;
    /**
     * 上级机构
     */
    private String upAgentCom;
}
