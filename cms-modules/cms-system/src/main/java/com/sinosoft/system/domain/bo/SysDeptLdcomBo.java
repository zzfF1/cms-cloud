package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.system.domain.SysDept;

/**
 * 部门业务对象(集成Ldcom) sys_dept
 *
 * @author zzf
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDept.class, reverseConvertGenerate = false)
public class SysDeptLdcomBo extends BaseEntity {

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 管理机构代码 (对应Ldcom表中的comcode)
     */
    @NotBlank(message = "机构代码不能为空")
    @Size(min = 0, max = 8, message = "机构代码长度不能超过{max}个字符")
    private String manageCom;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过{max}个字符")
    private String deptName;

    /**
     * 部门类别编码
     */
    @Size(min = 0, max = 100, message = "部门类别编码长度不能超过{max}个字符")
    private String deptCategory;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /**
     * 负责人
     */
    private Long leader;

    /**
     * 联系电话
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过{max}个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    private String status;

    /**
     * 归属部门id（部门树）
     */
    private Long belongDeptId;

    // 以下为Ldcom表对应的字段

    /**
     * 对外公布的机构代码
     */
    @Size(min = 0, max = 10, message = "对外机构代码长度不能超过{max}个字符")
    private String outcomcode;

    /**
     * 机构名称
     */
    @Size(min = 0, max = 100, message = "机构名称长度不能超过{max}个字符")
    private String name;

    /**
     * 简称
     */
    @Size(min = 0, max = 50, message = "机构简称长度不能超过{max}个字符")
    private String shortname;

    /**
     * 机构地址
     */
    @Size(min = 0, max = 200, message = "机构地址长度不能超过{max}个字符")
    private String address;

    /**
     * 机构邮编
     */
    @Size(min = 0, max = 10, message = "机构邮编长度不能超过{max}个字符")
    private String zipcode;

    /**
     * 机构电话
     */
    @Size(min = 0, max = 50, message = "机构电话长度不能超过{max}个字符")
    private String comPhone;

    /**
     * 机构传真
     */
    @Size(min = 0, max = 50, message = "机构传真长度不能超过{max}个字符")
    private String fax;

    /**
     * 机构级别
     */
    @Size(min = 0, max = 10, message = "机构级别长度不能超过{max}个字符")
    private String comgrade;

    /**
     * 行政区划代码
     */
    @Size(min = 0, max = 20, message = "行政区划代码长度不能超过{max}个字符")
    private String regionalismcode;

    /**
     * 上级管理机构代码
     */
    @Size(min = 0, max = 10, message = "上级管理机构代码长度不能超过{max}个字符")
    private String upcomcode;
}
