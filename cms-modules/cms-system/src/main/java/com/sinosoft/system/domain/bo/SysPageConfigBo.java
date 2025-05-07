package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.schema.common.domain.SysPageConfig;

import java.math.BigDecimal;
import java.util.List;

/**
 * 界面配置业务对象 sys_page_config
 *
 * @author zzf
 * @date 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPageConfig.class, reverseConvertGenerate = false)
public class SysPageConfigBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 代码
     */
    @NotBlank(message = "代码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空", groups = { AddGroup.class, EditGroup.class })
    private String branchType;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal sort;

    /**
     * 说明
     */
    private String remark;

    /**
     * 涉及数据表
     */
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
    private List<SysPageConfigQueryBo> queryList;

    /**
     * 表格列表
     */
    private List<SysPageConfigTabBo> tableList;
}
