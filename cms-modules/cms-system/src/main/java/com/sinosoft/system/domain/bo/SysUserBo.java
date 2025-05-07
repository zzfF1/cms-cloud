package com.sinosoft.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.AutoMappings;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.sinosoft.common.core.constant.SystemConstants;
import com.sinosoft.common.core.xss.Xss;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.system.domain.SysUser;

import java.util.List;

/**
 * 用户信息业务对象 sys_user
 *
 * @author Michelle.Chung
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysUser.class, reverseConvertGenerate = false)
public class SysUserBo extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过{max}个字符")
    @Pattern(regexp = "(?i)(?!^admin$|^guest$|^superadmin$)^.*$",message = "用户账号不能为admin、guest、superadmin")
    private String userName;

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @NotBlank(message = "用户昵称不能为空")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
    @Size(min = 1, max = 13, message = "邮箱长度不能超过{max}个字符")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 角色组
     */
    @Size(min = 1, message = "用户角色不能为空")
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @Size(max = 1, message = "岗位只能分配1个")
    private Long[] postIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

    /**
     * 用户ID
     */
    private String userIds;

    /**
     * 排除不查询的用户(工作流用)
     */
    private String excludeUserIds;

    /**
     * 管理机构 (自动从部门获取)
     */
    @Size(max = 8, message = "管理机构长度不能超过{max}个字符")
    private String manageCom;

    /**
     * 渠道类型
     */
    @NotBlank(message = "渠道类型不能为空")
    @Pattern(regexp = "^([0-9]|10)$", message = "渠道类型格式不正确")
    private String branchType;

    /**
     * 证件号码（实名制使用）
     */
    private String idNo;

    /**
     * 账号绑定IP
     */
    @Pattern(regexp ="^$|((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$",message = "绑定IP格式错误")
    private String bindIp;

    /**
     * 允许访问时间(开始、截止) 24hi:mm:ss
     */
    @AutoMappings({
        @AutoMapping(target = "accessStartTime", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getAccessTime(),0))"),
        @AutoMapping(target = "accessEndTime", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getAccessTime(),1))")
    })
    private List<String> accessTime;

    /**
     * 账户类型
     */
    @NotNull(message = "账户类型不能为空")
    private Integer accType;

    /**
     * 系统内置 Y-是 N-否
     */
    @NotNull(message = "系统内置不能为空")
    private String sysType;

    /**
     * 姓名（实名制）
     */
    private String realName;

    /**
     * 账户有效期
     */
    @AutoMappings({
        @AutoMapping(target = "validStartDate", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getValidDate(),0))"),
        @AutoMapping(target = "validEndDate", expression = "java(cn.hutool.core.collection.CollUtil.get(source.getValidDate(),1))")
    })
    private List<String> validDate;

    public SysUserBo(Long userId) {
        this.userId = userId;
    }

    public boolean isSuperAdmin() {
        return SystemConstants.SUPER_ADMIN_ID.equals(this.userId);
    }

}
