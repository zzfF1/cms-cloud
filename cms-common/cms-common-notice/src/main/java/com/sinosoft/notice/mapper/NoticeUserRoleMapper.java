package com.sinosoft.notice.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationDelivery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知模块用户角色关联Mapper接口
 */
public interface NoticeUserRoleMapper extends BaseMapperPlus<Object, Object> {


    /**
     * 根据角色ID列表查询用户ID
     *
     * @param roleIds 角色ID列表
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 判断用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    Boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据权限标识列表查询用户ID
     *
     * @param perms 权限标识列表
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByPerms(@Param("perms") List<String> perms);

    /**
     * 根据用户名查询用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    Long selectUserIdByUsername(@Param("username") String username);

    /**
     * 查询节点角色配置
     *
     * @param lcId 流程节点ID
     * @return 角色配置
     */
    String selectNodeRoles(@Param("lcId") Integer lcId);

    /**
     * 查询节点权限配置
     *
     * @param lcId 流程节点ID
     * @return 权限配置
     */
    String selectNodePerms(@Param("lcId") Integer lcId);

    /**
     * 查询节点处理人配置
     *
     * @param lcId 流程节点ID
     * @return 处理人配置
     */
    String selectNodeHandlers(@Param("lcId") Integer lcId);

    /**
     * 根据部门ID列表查询用户ID
     *
     * @param deptIds 部门ID列表
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByDeptIds(@Param("deptIds") List<Long> deptIds);
}
