package com.sinosoft.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户密码有效期视图对象 sys_user_pwd_expire
 *
 * @author system
 */
@Data
@NoArgsConstructor
public class SysUserPwdExpireVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 密码设置时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 密码过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date warningTime;

    /**
     * 状态(0正常 1已过期)
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 剩余有效天数（计算字段）
     */
    private Long remainingDays;

    /**
     * 当前密码是否即将过期
     */
    private Boolean isExpiringSoon;

    /**
     * 当前密码是否已过期
     */
    private Boolean isExpired;
}
