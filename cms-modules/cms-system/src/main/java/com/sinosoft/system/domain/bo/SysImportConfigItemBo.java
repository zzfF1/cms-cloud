package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysImportConfigItem;

import java.math.BigDecimal;
import java.util.Date;

/**
 * excel导入模板配置业务对象 sys_import_config_item
 *
 * @author zzf
 * @date 2024-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysImportConfigItem.class, reverseConvertGenerate = false)
public class SysImportConfigItemBo extends BaseEntity {

    /**
     * 序列号
     */
    private Long id;

    /**
     * 配置id
     */
    private Long configId;

    /**
     * sheet索引
     */
    private Long sheetIndex;

    /**
     * 标题
     */
    private String title;

    /**
     * 字段必录
     */
    private Long fieldRequired;

    /**
     * 字段类型
     */
    private String dataType;

    /**
     * 填写说明
     */
    private String fillSm;

    /**
     * 列宽
     */
    @NotNull(message = "列宽不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer width;

    /**
     * 下拉处理类
     */
    private String downSelHandler;

    /**
     * 参数
     */
    private String parameter;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal sort;

    /**
     * 备注
     */
    private String remark;
}
