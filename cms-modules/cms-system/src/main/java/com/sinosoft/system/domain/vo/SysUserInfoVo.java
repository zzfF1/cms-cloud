package com.sinosoft.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户信息
 *
 * @author Michelle.Chung
 */
@Data
public class SysUserInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户信息
     */
    private SysUserVo user;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 角色列表
     */
    private List<SysRoleVo> roles;

    /**
     * 岗位ID列表
     */
    private List<Long> postIds;

    /**
     * 岗位列表
     */
    private List<SysPostVo> posts;

}
