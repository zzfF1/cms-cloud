package com.sinosoft.system.dubbo;

import cn.hutool.core.convert.Convert;
import com.sinosoft.system.domain.SysDept;
import com.sinosoft.system.domain.bo.*;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.api.RemoteTaskAssigneeService;
import com.sinosoft.system.api.domain.bo.RemoteTaskAssigneeBo;
import com.sinosoft.system.api.domain.vo.RemoteTaskAssigneeVo;
import com.sinosoft.system.domain.vo.SysDeptVo;
import com.sinosoft.system.domain.vo.SysPostVo;
import com.sinosoft.system.domain.vo.SysRoleVo;
import com.sinosoft.system.domain.vo.SysUserVo;
import com.sinosoft.system.service.ISysDeptService;
import com.sinosoft.system.service.ISysPostService;
import com.sinosoft.system.service.ISysRoleService;
import com.sinosoft.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 工作流设计器获取任务执行人
 *
 * @author zzf
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteTaskAssigneeServiceImpl implements RemoteTaskAssigneeService {

    // 上级Service注入下级Service 其他Service永远不可能注入当前类 避免循环注入
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysUserService userService;
    private final ISysRoleService roleService;

    /**
     * 查询角色并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public RemoteTaskAssigneeVo selectRolesByTaskAssigneeList(RemoteTaskAssigneeBo taskQuery) {
        PageQuery pageQuery = new PageQuery(taskQuery.getPageSize(), taskQuery.getPageNum());
        SysRoleBo bo = new SysRoleBo();
        bo.setRoleName(taskQuery.getHandlerCode());
        bo.setRoleKey(taskQuery.getHandlerName());
        Map<String, Object> params = bo.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        TableDataInfo<SysRoleVo> page = roleService.selectPageRoleList(bo, pageQuery);
        // 使用封装的字段映射方法进行转换
        List<RemoteTaskAssigneeVo.TaskHandler> handlers = RemoteTaskAssigneeVo.convertToHandlerList(page.getRows(),
            SysRoleVo::getRoleId, SysRoleVo::getRoleKey, SysRoleVo::getRoleName, null, SysRoleVo::getCreateTime);
        return new RemoteTaskAssigneeVo(page.getTotal(), handlers);
    }

    /**
     * 查询岗位并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public RemoteTaskAssigneeVo selectPostsByTaskAssigneeList(RemoteTaskAssigneeBo taskQuery) {
        PageQuery pageQuery = new PageQuery(taskQuery.getPageSize(), taskQuery.getPageNum());
        SysPostBo bo = new SysPostBo();
        bo.setPostCategory(taskQuery.getHandlerCode());
        bo.setPostName(taskQuery.getHandlerName());
        Map<String, Object> params = bo.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        bo.setBelongDeptId(Convert.toLong(taskQuery.getGroupId()));
        TableDataInfo<SysPostVo> page = postService.selectPagePostList(bo, pageQuery);
        // 使用封装的字段映射方法进行转换
        List<RemoteTaskAssigneeVo.TaskHandler> handlers = RemoteTaskAssigneeVo.convertToHandlerList(page.getRows(),
            SysPostVo::getPostId, SysPostVo::getPostCategory, SysPostVo::getPostName, SysPostVo::getDeptId, SysPostVo::getCreateTime);
        return new RemoteTaskAssigneeVo(page.getTotal(), handlers);
    }

    /**
     * 查询部门并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public RemoteTaskAssigneeVo selectDeptsByTaskAssigneeList(RemoteTaskAssigneeBo taskQuery) {
        PageQuery pageQuery = new PageQuery(taskQuery.getPageSize(), taskQuery.getPageNum());
        SysDeptQuery bo= new SysDeptQuery();
        bo.setDeptCategory(taskQuery.getHandlerCode());
        bo.setDeptName(taskQuery.getHandlerName());
        Map<String, Object> params = bo.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        bo.setBelongDeptId(Convert.toLong(taskQuery.getGroupId()));
        TableDataInfo<SysDeptVo> page = deptService.selectPageDeptList(bo, pageQuery);
        // 使用封装的字段映射方法进行转换
        List<RemoteTaskAssigneeVo.TaskHandler> handlers = RemoteTaskAssigneeVo.convertToHandlerList(page.getRows(),
            SysDeptVo::getDeptId, SysDeptVo::getDeptCategory, SysDeptVo::getDeptName, SysDeptVo::getParentId, SysDeptVo::getCreateTime);
        return new RemoteTaskAssigneeVo(page.getTotal(), handlers);
    }


    /**
     * 查询用户并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public RemoteTaskAssigneeVo selectUsersByTaskAssigneeList(RemoteTaskAssigneeBo taskQuery) {
        PageQuery pageQuery = new PageQuery(taskQuery.getPageSize(), taskQuery.getPageNum());
        SysUserBo bo = new SysUserBo();
        bo.setUserName(taskQuery.getHandlerCode());
        bo.setNickName(taskQuery.getHandlerName());
        Map<String, Object> params = bo.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        bo.setDeptId(Convert.toLong(taskQuery.getGroupId()));
//        TableDataInfo<SysUserVo> page = userService.selectPageUserList(bo, pageQuery);
//        // 使用封装的字段映射方法进行转换
//        List<RemoteTaskAssigneeVo.TaskHandler> handlers = RemoteTaskAssigneeVo.convertToHandlerList(page.getRows(),
//            SysUserVo::getUserId, SysUserVo::getUserName, SysUserVo::getNickName, SysUserVo::getDeptId, SysUserVo::getCreateTime);
//        return new RemoteTaskAssigneeVo(page.getTotal(), handlers);
        return new RemoteTaskAssigneeVo();
    }

}
