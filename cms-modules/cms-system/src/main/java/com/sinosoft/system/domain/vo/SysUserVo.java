package com.sinosoft.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinosoft.common.sensitive.annotation.Sensitive;
import com.sinosoft.common.sensitive.core.SensitiveStrategy;
import com.sinosoft.common.translation.annotation.Translation;
import com.sinosoft.common.translation.constant.TransConstant;
import com.sinosoft.system.domain.SysUser;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 用户信息视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysUser.class)
public class SysUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL, perms = "system:user:edit")
    private String email;

    /**
     * 手机号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE, perms = "system:user:edit")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 头像地址
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private Long avatar;

    /**
     * 密码
     */
    @JsonIgnore
    @JsonProperty
    private String password;

    /**
     * 帐号状态
     * @see com.sinosoft.common.core.enums.UserStatus
     */
    private String status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 部门名
     */
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;

    /**
     * 角色对象
     */
    private List<SysRoleVo> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;


    /**
     * 最后一次密码修改时间
     */
    private Date lastPwdUpdateTime;

    /**
     * 账户类型
     */
    private Integer accType;

    /**
     * 账号绑定IP
     */
    private String bindIp;

    /**
     * 系统内置 Y-是 N-否
     */
    private String sysType;

    private String accessStartTime;

    private String accessEndTime;

    private String validStartDate;

    private String validEndDate;

    /**
     * 允许访问时间 24hi:mm:ss
     */
//    @AutoMappings({
//        @AutoMapping(target = "accessStartTime",source = "accessTime", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getAccessTime(),0))"),
//        @AutoMapping(target = "accessEndTime",source = "accessTime", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getAccessTime(),1))")
//    })
    private List<String> accessTime;

    /**
     * 账户有效期  yyyy-MM-dd
     */
    private List<String> validDate;

    /**
     * 姓名（实名制）
     */
    private String realName;
    /**
     * 实名完成时间
     */
    private Date authTime;
}
