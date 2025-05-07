package com.sinosoft.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sinosoft.common.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户密码有效期表 sys_user_pwd_expire
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_pwd_expire")
public class SysUserPwdExpire extends TenantEntity {

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 密码设置时间
     */
    private Date startTime;

    /**
     * 密码过期时间
     */
    private Date expireTime;

    /**
     * 密码修改次数
     */
    private Integer updateTimes;

    /**
     * 是否已发送过期警告(0未发送 1已发送)
     */
    private String warningFlag;

    /**
     * 警告发送时间
     */
    private Date warningTime;

    /**
     * 状态(0正常 1已过期)
     */
    private String status;
}
