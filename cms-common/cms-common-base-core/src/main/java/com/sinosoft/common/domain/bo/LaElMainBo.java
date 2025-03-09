package com.sinosoft.common.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.LaElMain;

import java.math.BigDecimal;

/**
 * 规则配置业务对象 la_el_main
 *
 * @author zzf
 * @date 2023-09-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = LaElMain.class, reverseConvertGenerate = false)
public class LaElMainBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 类型
     */
    @NotNull(message = "类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer type;

    /**
     * 计算代码
     */
    @NotBlank(message = "计算代码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String calCode;

    /**
     * 说明
     */
    @NotBlank(message = "说明不能为空", groups = { AddGroup.class, EditGroup.class })
    private String message;

    /**
     * 版本号
     */
    @NotNull(message = "版本号不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal calVersion;
}
