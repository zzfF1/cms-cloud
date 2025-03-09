package com.sinosoft.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @program: cms6
 * @description: excel配置对象
 * @author: zzf
 * @create: 2024-01-06 17:02
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ExcelConfigBase {
    /**
     * 主键
     */
    private Long id;
    /**
     * 代码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 是否为导入
     * true为导入则excel设置文本格式
     */
    private Boolean isImport;
}
