package com.sinosoft.system.domain.vo;

import com.sinosoft.common.core.constant.Constants;
import com.sinosoft.common.translation.annotation.Translation;
import com.sinosoft.common.translation.constant.TransConstant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 用户信息查询视图对象 sys_user
 * @author
 */
@Data
@AutoMapper(target = SysUserVo.class)
public class SysUserQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 帐号状态
     * @see com.sinosoft.common.core.enums.UserStatus
     */
    private String status;

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
     * 锁定（连续密码错误被锁定）
     */
    private String lock = Constants.NO;

    /**
     * 账户类型
     */
    private Integer accType;

    /**
     * 系统内置 Y-是 N-否
     */
    private String sysType;

    /**
     * 渠道
     */
    private String branchType;
}
