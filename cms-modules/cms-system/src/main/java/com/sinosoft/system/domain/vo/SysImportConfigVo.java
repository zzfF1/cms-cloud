package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.schema.common.domain.SysImportConfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * excel导入模板视图对象 sys_import_config
 *
 * @author zzf
 * @date 2024-01-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysImportConfig.class)
public class SysImportConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 序列号
     */
    @ExcelProperty(value = "序列号")
    private Long id;

    /**
     * 代码
     */
    @ExcelProperty(value = "代码")
    private String code;

    /**
     * 模板名称
     */
    @ExcelProperty(value = "模板名称")
    private String name;

    /**
     * 标题名称 多个sheet标题使用,分割
     */
    @ExcelProperty(value = "标题名称")
    private String titleNames;

    /**
     * sheet名称 多个sheet标题使用,分割
     */
    @ExcelProperty(value = "sheet名称")
    private String sheetNames;

    /**
     * 渠道
     */
    @ExcelProperty(value = "渠道")
    private String branchType;

    /**
     * 说明
     */
    @ExcelProperty(value = "说明")
    private String remark;

    /**
     * 创建人
     */
    private String operator;

    /**
     * 创建日期
     */
    private String makedate;

    /**
     * 创建时间
     */
    private String maketime;

    /**
     * 修改人
     */
    private String modifyoperator;

    /**
     * 修改日期
     */
    private String modifydate;

    /**
     * 修改时间
     */
    private String modifytime;

    /**
     * 配置项列表
     */
    private List<SysImportConfigItemVo> configItems;
}
