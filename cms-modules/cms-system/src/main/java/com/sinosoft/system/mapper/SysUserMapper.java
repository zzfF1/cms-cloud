package com.sinosoft.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.mybatis.annotation.DataColumn;
import com.sinosoft.common.mybatis.annotation.DataPermission;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysUser;
import com.sinosoft.system.domain.vo.SysUserExportVo;
import com.sinosoft.system.domain.vo.SysUserVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author zzf
 */
public interface SysUserMapper extends BaseMapperPlus<SysUser, SysUserVo> {

    @DataPermission({
        @DataColumn(key = "deptName", value = "u.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectPageUserList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "user_id")
    })
    List<SysUserVo> selectUserList(@Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询用户列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    List<SysUserExportVo> selectUserExportList(@Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectAllocatedList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.user_id")
    })
    Page<SysUserVo> selectUnallocatedList(@Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "user_id")
    })
    long countUserById(Long userId);

    @Override
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "user_id")
    })
    int update(@Param(Constants.ENTITY) SysUser user, @Param(Constants.WRAPPER) Wrapper<SysUser> updateWrapper);

    @Override
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "user_id")
    })
    int updateById(@Param(Constants.ENTITY) SysUser user);

    /**
     * 更新用户状态
     * @param userId  用户ID
     * @param status  状态
     * @param current 当前时间
     * @return
     */
    void updateUserStatus(@Param("userId") Long userId,@Param("status") String status,@Param("current") Date current);
}
