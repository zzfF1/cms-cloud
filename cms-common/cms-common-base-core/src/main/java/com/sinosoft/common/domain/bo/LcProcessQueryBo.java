package com.sinosoft.common.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.domain.LcProcesstrack;

/**
 * @program: cms6
 * @description: 流程规则查询
 * @author: zzf
 * @create: 2023-10-02 09:52
 */
@Data
@AutoMapper(target = LcProcesstrack.class, reverseConvertGenerate = false)
public class LcProcessQueryBo {
    /**
     * 流程类型
     */
    private Integer lcSerialNo;
    /**
     * 最后标志
     */
    private Integer lastFlag;

    /**
     * 操作类型 0保存 1提交 -1退回
     */
    private Integer czType;

    /**
     * 业务数据ID
     */
    private String dataId;
}
