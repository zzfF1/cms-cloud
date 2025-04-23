package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.sinosoft.common.core.constant.CacheConstants;
import com.sinosoft.common.core.enums.AccType;
import com.sinosoft.common.core.enums.UserStatus;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.MessageUtils;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.system.api.domain.bo.RemoteUserBo;
import com.sinosoft.system.api.domain.vo.RemoteUserVo;
import com.sinosoft.system.api.model.XcxLoginUser;
import com.sinosoft.system.domain.bo.*;
import com.sinosoft.system.domain.vo.*;
import com.sinosoft.system.dubbo.RemoteUserServiceImpl;
import com.sinosoft.system.service.*;
import com.sinosoft.system.service.impl.SysUserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.constant.SystemConstants;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.utils.StreamUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.encrypt.annotation.ApiEncrypt;
import com.sinosoft.common.excel.core.ExcelResult;
import com.sinosoft.common.excel.utils.ExcelUtil;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.tenant.helper.TenantHelper;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.system.listener.SysUserImportListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户信息
 *
 * @author zzf
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysTenantService tenantService;
    private final ISysConfigService sysConfigService;
    private final RemoteUserServiceImpl remoteUserServiceImpl;
    private final SysUserServiceImpl sysUserServiceImpl;

    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserQueryVo> list(SysUserBo user, PageQuery pageQuery) {
        return userService.selectPageUserList(user, pageQuery);
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT, eventType = EventType.system)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    public void export(SysUserBo user, HttpServletResponse response) {
        List<SysUserExportVo> list = userService.selectUserExportList(user);
        ExcelUtil.exportExcel(list, "用户数据", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT, eventType = EventType.system)
    @SaCheckPermission("system:user:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }

    /**
     * 登录成功后调用获取用户信息
     *
     * @return 用户信息
     */
    @ApiEncrypt(response = true)
    @GetMapping("/getInfo")
    public R<UserInfoVo> getInfo() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }
        SysUserVo user = userService.selectUserById(loginUser.getUserId());
        if (ObjectUtil.isNull(user)) {
            return R.fail("没有权限访问用户数据!");
        }
        if (!LoginHelper.isSuperAdmin()) {
            if (user.getLastPwdUpdateTime() == null || new Date().after(DateUtil.offsetDay(user.getLastPwdUpdateTime(),
                Integer.valueOf(sysConfigService.selectConfigByKey("sys.password.validity.days"))))) {
                userInfoVo.setPwdFlag(true);
            }
            if (user.getAuthTime() == null) {
                userInfoVo.setRealAuth(false);
            }
        }
        user.setRoles(roleService.selectRolesByUserId(user.getUserId()));
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());
        return R.ok(userInfoVo);
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @ApiEncrypt(response = true)
    @GetMapping(value = {"/", "/{userId}"})
    public R<SysUserInfoVo> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        if (ObjectUtil.isNotNull(userId)) {
            userService.checkUserDataScope(userId);
            SysUserVo sysUser = userService.selectUserById(userId);
            userInfoVo.setUser(sysUser);
            userInfoVo.setRoleIds(roleService.selectRoleListByUserId(userId));
            Long deptId = sysUser.getDeptId();
            if (ObjectUtil.isNotNull(deptId)) {
                SysPostBo postBo = new SysPostBo();
                postBo.setDeptId(deptId);
                userInfoVo.setPosts(postService.selectPostList(postBo));
                userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
            }
        }
        SysRoleBo roleBo = new SysRoleBo();
        roleBo.setStatus(SystemConstants.NORMAL);
        List<SysRoleVo> roles = roleService.selectRoleList(roleBo);
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.ok(userInfoVo);
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.ADD_USER, eventType = EventType.system)
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody SysUserBo user) {
        deptService.checkDeptDataScope(user.getDeptId());
        if (user.getPassword().toLowerCase().contains(user.getUserName().toLowerCase())) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，密码不能包含用户名称字符");
        }
        if (!userService.checkUserRoleNums(user.getRoleIds().length)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，只可分配1个角色");
        }
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        } else if (AccType.TEMP.ordinal() == user.getAccType()) {
            //校验临时账户有效期
            if (CollUtil.isEmpty(user.getValidDate()) || user.getValidDate().size() != 2) {
                return R.fail("临时账户需填写账户有效期");
            }
            try {
                DateTime sDate = DateUtil.parseDate(user.getValidDate().get(0));
                DateTime eDate = DateUtil.parseDate(user.getValidDate().get(1));
                if (DateUtil.betweenDay(sDate, eDate, true) < 89) {
                    return R.fail("账户有效期间隔至少90天");
                }
            } catch (Exception e) {
                return R.fail("账户有效期格式应为yyyy-MM-dd");
            }
        } else if (CollUtil.isNotEmpty(user.getAccessTime())) {
            //允许访问时间校验
            if (user.getAccessTime().size() != 2) {
                return R.fail("请输入正确的允许访问时间");
            }
            if (!DateUtils.isAllTime(user.getAccessTime().get(0), user.getAccessTime().get(1))) {
                return R.fail("请输入正确的允许访问时间");
            }
            if (user.getAccessTime().get(0).compareTo(user.getAccessTime().get(1)) >= 0) {
                return R.fail("请输入正确的允许访问时间");
            }
        }
        if (TenantHelper.isEnable()) {
            if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
                return R.fail("当前租户下用户名额不足，请联系管理员");
            }
        }
        //新增用户，状态初始待激活
        user.setStatus(UserStatus.TO_ACTIVATED.getCode());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @SaCheckSafe
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/edit")
    public R<Void> edit(@Validated @RequestBody SysUserBo user) {
        if (!userService.checkUserRoleNums(user.getRoleIds().length)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，只可分配1个角色");
        }
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @param userIds 角色ID串
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE, eventType = EventType.system)
    @PostMapping("/delete/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, LoginHelper.getUserId())) {
            return R.fail("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 根据用户ID串批量获取用户基础信息
     *
     * @param userIds 用户ID串
     * @param deptId  部门ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/optionselect")
    public R<List<SysUserVo>> optionselect(@RequestParam(required = false) Long[] userIds,
                                           @RequestParam(required = false) Long deptId) {
        return R.ok(userService.selectUserByIds(ArrayUtil.isEmpty(userIds) ? null : List.of(userIds), deptId));
    }

    /**
     * 重置密码
     */
    @SaCheckSafe
    @ApiEncrypt
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.RESET_PASSWORD, eventType = EventType.system)
    @PostMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetUserPwd(user.getUserId(), user.getPassword()));
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        userService.updateUserStatus(user.getUserId(), user.getStatus());
        return R.ok();
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:unlock")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/unlock")
    public R<String> unlock(@RequestBody SysUserBo user) {
        RedisUtils.deleteObject(CacheConstants.PWD_ERR_CNT_KEY + user.getUserName());
        return R.ok();
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @Log(title = "用户管理", businessType = BusinessType.OTHER, eventType = EventType.system)
    @GetMapping("/authRole/{userId}")
    public R<SysUserInfoVo> authRole(@PathVariable Long userId) {
        userService.checkUserDataScope(userId);
        SysUserVo user = userService.selectUserById(userId);
        List<SysRoleVo> roles = roleService.selectRolesAuthByUserId(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.ok(userInfoVo);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户Id
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.AUTHORIZE, eventType = EventType.system)
    @PostMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        if (!userService.checkUserRoleNums(roleIds.length)) {

        }
        if (userService.checkInnerUser(userId)) {
            return R.fail("系统内置用户，不可修改");
        }
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptQuery dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }

    /**
     * 获取部门下的所有用户信息
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list/dept/{deptId}")
    public R<List<SysUserVo>> listByDept(@PathVariable @NotNull Long deptId) {
        return R.ok(userService.selectUserListByDept(deptId));
    }

    /**
     * 账户实名认证
     *
     * @param bo
     * @return
     */
    @PostMapping("/doAuthReal")
    public R<Void> doAuthReal(@RequestBody SysUserBo bo) {
        if (StringUtils.isBlank(bo.getRealName()) || StringUtils.isBlank(bo.getIdNo())) {
            return R.fail(MessageUtils.message("account.identity.verification.fail"));
        }
        Long userId = LoginHelper.getUserId();
        sysUserServiceImpl.doAuthReal(userId, bo.getRealName(), bo.getIdNo());
        return R.ok(MessageUtils.message("account.identity.verification.succuss"));
    }


    //======================================以下接口供服务间调用 start======================================================

    /**
     * 通过用户名查询用户信息
     *
     * @param username
     * @param tenantId
     * @return
     */
    @GetMapping(value = {"/info/getUserInfoByName"})
    public R<LoginUser> getUserInfoByName(@RequestParam String username, @RequestParam String tenantId) {
        LoginUser userInfo = remoteUserServiceImpl.getUserInfo(username, tenantId);
        return R.ok(userInfo);
    }

    /**
     * 通过用户id查询用户信息
     *
     * @param userId
     * @param tenantId
     * @return
     */
    @GetMapping(value = {"/info/getUserInfoById"})
    public R<LoginUser> getUserInfoById(@RequestParam Long userId, @RequestParam String tenantId) {
        LoginUser userInfo = remoteUserServiceImpl.getUserInfo(userId, tenantId);
        return R.ok(userInfo);
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param phonenumber
     * @param tenantId
     * @return
     */
    @GetMapping(value = {"/info/getUserInfoByPhonenumber"})
    public R<LoginUser> getUserInfoByPhonenumber(@RequestParam String phonenumber, @RequestParam String tenantId) {
        LoginUser userInfo = remoteUserServiceImpl.getUserInfoByPhonenumber(phonenumber, tenantId);
        return R.ok(userInfo);
    }

    /**
     * 通过邮箱查询用户信息
     *
     * @param email
     * @param tenantId
     * @return
     */
    @GetMapping(value = {"/info/getUserInfoByEmail"})
    public R<LoginUser> getUserInfoByEmail(@RequestParam String email, @RequestParam String tenantId) {
        LoginUser userInfo = remoteUserServiceImpl.getUserInfoByEmail(email, tenantId);
        return R.ok(userInfo);
    }

    /**
     * 通过openid查询用户信息
     *
     * @param openid
     * @return
     */
    @GetMapping(value = {"/info/getUserInfoByOpenid"})
    public R<LoginUser> getUserInfoByOpenid(@RequestParam String openid) {
        XcxLoginUser userInfo = remoteUserServiceImpl.getUserInfoByOpenid(openid);
        return R.ok(userInfo);
    }

    /**
     * 注册用户信息
     *
     * @param remoteUserBo
     * @return
     */
    @PostMapping(value = {"/info/registerUserInfo"})
    public R<Boolean> registerUserInfo(@RequestBody RemoteUserBo remoteUserBo) {
        Boolean re = remoteUserServiceImpl.registerUserInfo(remoteUserBo);
        return R.ok(re);
    }

    /**
     * 通过userId查询用户账户
     *
     * @param userId
     * @return
     */
    @GetMapping(value = {"/info/selectUserNameById"})
    public R<String> selectUserNameById(@RequestParam Long userId) {
        String userName = remoteUserServiceImpl.selectUserNameById(userId);
        return R.ok(userName);
    }

    /**
     * 通过用户ID查询用户昵称
     *
     * @param userId
     * @return
     */
    @GetMapping(value = {"/info/selectNicknameById"})
    public R<String> selectNicknameById(@RequestParam Long userId) {
        String userName = remoteUserServiceImpl.selectNicknameById(userId);
        return R.ok(userName);
    }

    /**
     * 通过用户ID查询用户账户
     *
     * @param userIds 用户ID 多个用逗号隔开
     * @return
     */
    @GetMapping(value = {"/info/selectNicknameByIds"})
    public R<String> selectNicknameByIds(@RequestParam String userIds) {
        String userName = remoteUserServiceImpl.selectNicknameByIds(userIds);
        return R.ok(userName);
    }

    /**
     * 通过用户ID查询用户手机号
     *
     * @param userId
     * @return
     */
    @GetMapping(value = {"/info/selectPhonenumberById"})
    public R<String> selectPhonenumberById(@RequestParam Long userId) {
        String phonenumber = remoteUserServiceImpl.selectPhonenumberById(userId);
        return R.ok(phonenumber);
    }

    /**
     * 通过用户ID查询用户邮箱
     *
     * @param userId
     * @return
     */
    @GetMapping(value = {"/info/selectEmailById"})
    public R<String> selectEmailById(@RequestParam Long userId) {
        String email = remoteUserServiceImpl.selectEmailById(userId);
        return R.ok(email);
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param ip
     * @return
     */
    @PostMapping(value = {"/info/recordLoginInfo"})
    public R recordLoginInfo(@RequestParam Long userId, @RequestParam String ip) {
        remoteUserServiceImpl.recordLoginInfo(userId, ip);
        return R.ok();
    }

    /**
     * 通过用户ID查询用户列表
     *
     * @param userIds
     * @return
     */
    @GetMapping(value = {"/info/selectListByIds"})
    public R<List<RemoteUserVo>> selectListByIds(@RequestParam("userIds") List<Long> userIds) {
        List<RemoteUserVo> remoteUserVos = remoteUserServiceImpl.selectListByIds(userIds);
        return R.ok(remoteUserVos);
    }

    /**
     * 通过角色ID查询用户ID
     *
     * @param roleIds
     * @return
     */
    @GetMapping("/info/selectUserIdsByRoleIds")
    public R<List<Long>> selectUserIdsByRoleIds(@RequestParam("roleIds") List<Long> roleIds) {
        return R.ok(userService.selectUserIdsByRoleIds(roleIds));
    }

    /**
     * 发送动态验证码
     *
     * @param userId       用户ID
     * @param authCodeType 验证码类型
     * @return 结果
     */
    @GetMapping("/info/sendAuthCode")
    public R<Void> sendAuthCode(@RequestParam("userId") Long userId, @RequestParam("authCodeType") Integer authCodeType) {
        boolean flag = remoteUserServiceImpl.sendAuthCode(userId, authCodeType);
        return flag ? R.ok() : R.fail();
    }

    //======================================以上接口供服务间调用 end======================================================
}
