package com.sinosoft.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.sinosoft.common.core.constant.SystemConstants;
import com.sinosoft.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 用户对象 sys_user
 *
 * @author zzf
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends TenantEntity {

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

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
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private Long avatar;

    /**
     * 密码
     */
    @TableField(
        insertStrategy = FieldStrategy.NOT_EMPTY,
        updateStrategy = FieldStrategy.NOT_EMPTY,
        whereStrategy = FieldStrategy.NOT_EMPTY
    )
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

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
     * 最后一次密码修改时间
     */
    private Date lastPwdUpdateTime;

    /**
     * 证件号码（实名制使用）
     */
    private String idNo;
    /**
     * 允许访问时间开始 24hi:mm:ss
     */
    private String accessStartTime;

    /**
     * 账号绑定IP
     */
    private String bindIp;

    /**
     * 允许访问时间截止 24hi:mm:ss
     */
    private String accessEndTime;

    /**
     * 账户类型
     */
    private Integer accType;

    /**
     * 账户有效起期  yyyy-MM-dd
     */
    private String validStartDate;

    /**
     * 账户有效止期  yyyy-MM-dd
     */
    private String validEndDate;

    /**
     * 账户激活时间
     */
    private Date activeTime;

    /**
     * 账户锁定时间
     */
    private Date lockTime;

    /**
     * 账户休眠时间
     */
    private Date dormancyTime;

    /**
     * 账户注销时间
     */
    private Date logoutTime;

    /**
     * 系统内置 Y-是 N-否
     */
    private String sysType;

    /**
     * 姓名（实名制）
     */
    private String realName;
    /**
     * 实名完成时间
     */
    private Date authTime;


    public SysUser(Long userId) {
        this.userId = userId;
    }

    public boolean isSuperAdmin() {
        return SystemConstants.SUPER_ADMIN_ID.equals(this.userId);
    }

}
