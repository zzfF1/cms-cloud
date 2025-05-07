package com.sinosoft.system.domain.bo;


import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysExportConfigItem;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * excel导出配置明细业务对象 sys_export_config_item
 *
 * @author demo
 * @date 2024-04-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysExportConfigItem.class, reverseConvertGenerate = false)
public class SysExportConfigItemBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * sheet索引
     */
    private Long sheetId;

    /**
     * 配置id
     */
    private Long configId;

    /**
     * 字段
     */
    @NotBlank(message = "字段不能为空", groups = { AddGroup.class, EditGroup.class })
    private String field;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号
     */
    @NotNull(message = "字段类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer type;

    /**
     * 显示长度
     */
    @NotNull(message = "显示长度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer dispLength;

    /**
     * 格式
     */
    private String format;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal sort;

}
