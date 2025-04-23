package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.excel.annotation.ExcelDictFormat;
import com.sinosoft.common.excel.convert.ExcelDictConvert;
import com.sinosoft.common.schema.common.domain.SysPageConfig;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 界面配置视图对象 sys_page_config
 *
 * @author zzf
 * @date 2024-07-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPageConfig.class)
public class SysPageConfigVo implements Serializable {

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
     * 渠道
     */
    @ExcelProperty(value = "渠道")
    private String branchType;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private BigDecimal sort;

    /**
     * 说明
     */
    @ExcelProperty(value = "说明")
    private String remark;

    /**
     * 涉及数据表
     */
    @ExcelProperty(value = "涉及数据表")
    private String involveTab;

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
     * 查询条件列表
     */
    private List<SysPageConfigQueryVo> queryList;

    /**
     * 表格列表
     */
    private List<SysPageConfigTabVo> tableList;
}
