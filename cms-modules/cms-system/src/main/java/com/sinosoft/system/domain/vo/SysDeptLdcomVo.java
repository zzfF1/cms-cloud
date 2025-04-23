package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.excel.annotation.ExcelDictFormat;
import com.sinosoft.common.excel.convert.ExcelDictConvert;
import com.sinosoft.system.domain.SysDept;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门视图对象(集成Ldcom) sys_dept
 *
 * @author zzf
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDept.class)
public class SysDeptLdcomVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    private Long deptId;

    /**
     * 管理机构代码
     */
    @ExcelProperty(value = "管理机构代码")
    private String manageCom;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 父部门名称
     */
    private String parentName;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    private String deptName;

    /**
     * 部门类别编码
     */
    @ExcelProperty(value = "部门类别编码")
    private String deptCategory;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 负责人ID
     */
    private Long leader;

    /**
     * 负责人
     */
    @ExcelProperty(value = "负责人")
    private String leaderName;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @ExcelProperty(value = "部门状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 子菜单
     */
    private List<SysDept> children = new ArrayList<>();

    // 以下为Ldcom表对应的字段

    /**
     * 对外公布的机构代码
     */
    @ExcelProperty(value = "对外机构代码")
    private String outcomcode;

    /**
     * 机构名称
     */
    @ExcelProperty(value = "机构名称")
    private String name;

    /**
     * 简称
     */
    @ExcelProperty(value = "机构简称")
    private String shortname;

    /**
     * 机构地址
     */
    @ExcelProperty(value = "机构地址")
    private String address;

    /**
     * 机构邮编
     */
    @ExcelProperty(value = "机构邮编")
    private String zipcode;

    /**
     * 机构电话
     */
    @ExcelProperty(value = "机构电话")
    private String comPhone;

    /**
     * 机构传真
     */
    @ExcelProperty(value = "机构传真")
    private String fax;

    /**
     * 机构级别
     */
    @ExcelProperty(value = "机构级别")
    private String comgrade;

    /**
     * 行政区划代码
     */
    @ExcelProperty(value = "行政区划代码")
    private String regionalismcode;

    /**
     * 上级管理机构代码
     */
    @ExcelProperty(value = "上级管理机构代码")
    private String upcomcode;
}
