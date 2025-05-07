package com.sinosoft.system.domain.vo;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysOperlogStrategyVo {
    private List<String> date = new ArrayList<>();
    private List<String> types = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
}
