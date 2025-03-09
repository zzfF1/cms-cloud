package com.sinosoft.common.domain.bo;

import com.sinosoft.common.schema.agent.domain.Laqualification;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;

/**
 * 业务员资质业务对象 la_qualification
 *
 * @author zzf
 * @date 2023-09-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Laqualification.class, reverseConvertGenerate = false)
public class LaQualificationBo extends BaseEntity {

    /**
     * 流水号
     */
    @NotNull(message = "流水号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 业务员编码
     */
    @NotBlank(message = "业务员编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String agentCode;

    /**
     * 资格证书号
     */
    @NotBlank(message = "资格证书号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String qualifNo;

    /**
     * 资格证类型
     */
    @NotBlank(message = "资格证类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String idx;

    /**
     * 批准单位
     */
    @NotBlank(message = "批准单位不能为空", groups = { AddGroup.class, EditGroup.class })
    private String grantUnit;

    /**
     * 发放日期
     */
    @NotNull(message = "发放日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date grantDate;

    /**
     * 失效日期
     */
    @NotNull(message = "失效日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date invalidDate;

    /**
     * 失效原因
     */
    @NotBlank(message = "失效原因不能为空", groups = { AddGroup.class, EditGroup.class })
    private String invalidRsn;

    /**
     * 补发日期
     */
    @NotNull(message = "补发日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date reissueDate;

    /**
     * 补发原因
     */
    @NotBlank(message = "补发原因不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reissueRsn;

    /**
     * 有效日期
     */
    @NotNull(message = "有效日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long validPeriod;

    /**
     * 资格证书状态
     */
    @NotBlank(message = "资格证书状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String state;

    /**
     * 通过考试日期
     */
    @NotNull(message = "通过考试日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date pasExamDate;

    /**
     * 考试年度
     */
    @NotBlank(message = "考试年度不能为空", groups = { AddGroup.class, EditGroup.class })
    private String examYear;

    /**
     * 考试次数
     */
    @NotBlank(message = "考试次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private String examTimes;

    /**
     * 操作人
     */
    @NotBlank(message = "操作人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operator;

    /**
     * 创建日期
     */
    @NotNull(message = "创建日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date makeDate;

    /**
     * 创建时间
     */
    @NotBlank(message = "创建时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String makeTime;

    /**
     * 修改日期
     */
    @NotNull(message = "修改日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date modifyDate;

    /**
     * 修改时间
     */
    @NotBlank(message = "修改时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modifyTime;

    /**
     * 有效起期
     */
    @NotNull(message = "有效起期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date validStart;

    /**
     * 有效止期
     */
    @NotNull(message = "有效止期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date validEnd;

    /**
     * 旧资格证号码
     */
    @NotBlank(message = "旧资格证号码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String oldQualifNo;

    /**
     * 资格证名称
     */
    @NotBlank(message = "资格证名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String qualifName;

    /**
     * 接口标识符
     */
    @NotBlank(message = "接口标识符不能为空", groups = { AddGroup.class, EditGroup.class })
    private String optFlag;


}
