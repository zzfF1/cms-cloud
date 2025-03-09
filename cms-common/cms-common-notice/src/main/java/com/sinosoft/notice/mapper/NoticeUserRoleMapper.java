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
     * 根据角色ID列表查询用户ID列表
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
     * @return 是否拥有
     */
    boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
