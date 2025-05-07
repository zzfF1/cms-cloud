package com.sinosoft.system.domain.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sinosoft.common.schema.common.domain.SysExportConfig;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * excel导出配置视图对象 sys_export_config
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysExportConfig.class)
public class SysExportConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 代码
     */
    @ExcelProperty(value = "代码")
    private String code;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 文件名称
     */
    @ExcelProperty(value = "文件名称")
    private String filename;

    /**
     * 路径
     */
    @ExcelProperty(value = "路径")
    private String path;

    /**
     * 类型 0-生成方式 1-模板方式
     */
    @ExcelProperty(value = "类型")
    private String type;

    /**
     * 渠道
     */
    @ExcelProperty(value = "渠道")
    private String branchType;


    /**
     * Sheet配置列表
     */
    private List<SysExportConfigSheetVo> sheets;
}
