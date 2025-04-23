package com.sinosoft.system.domain.bo;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SysOperlogStrategyBo {
    private List<String> types;
    private List<String> roles;
    private Map<String,Object> params;
}
