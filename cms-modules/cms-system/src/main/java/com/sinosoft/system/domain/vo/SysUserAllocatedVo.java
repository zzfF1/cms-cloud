package com.sinosoft.system.domain.vo;

import com.sinosoft.common.sensitive.annotation.Sensitive;
import com.sinosoft.common.sensitive.core.SensitiveStrategy;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 用户信息视图对象 sys_user
 *
 * @author zzf
 */
@Data
@AutoMapper(target = SysUserVo.class)
public class SysUserAllocatedVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    private String email;

    /**
     * 手机号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phonenumber;

    /**
     * 帐号状态
     *
     * @see com.sinosoft.common.core.enums.UserStatus
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

}
