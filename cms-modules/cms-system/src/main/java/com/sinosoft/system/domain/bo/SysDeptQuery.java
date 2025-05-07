package com.sinosoft.system.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.mybatis.core.page.PageQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * 部门查询对象(集成Ldcom)
 *
 * @author zzf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptQuery extends PageQuery {

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 管理机构代码
     */
    private String manageCom;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门类别编码
     */
    private String deptCategory;

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
    private String outcomcode;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 简称
     */
    private String shortname;

    /**
     * 机构级别
     */
    private String comgrade;

    /**
     * 行政区划代码
     */
    private String regionalismcode;


    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params = new HashMap<>();
}
