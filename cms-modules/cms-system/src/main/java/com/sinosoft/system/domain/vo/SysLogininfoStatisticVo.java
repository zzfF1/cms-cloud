package com.sinosoft.system.domain.vo;

import com.sinosoft.common.translation.annotation.Translation;
import com.sinosoft.common.translation.constant.TransConstant;
import lombok.Data;

@Data
public class SysLogininfoStatisticVo {
    //操作类型编码
    private String code;
    //操作类型名称
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "code",other = "sys_sign_status")
    private String name;
    //不同类型的数量
    private Long value;
}
