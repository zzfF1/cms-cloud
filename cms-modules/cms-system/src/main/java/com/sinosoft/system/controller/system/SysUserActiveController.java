package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.enums.UserStatus;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysUserBo;
import com.sinosoft.system.domain.vo.SysUserQueryVo;
import com.sinosoft.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/active")
public class SysUserActiveController extends BaseController {

    private final ISysUserService userService;

    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:active:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserQueryVo> list(SysUserBo user, PageQuery pageQuery) {
        return userService.selectPageToActiveUserList(user, pageQuery);
    }

    /**
     * 激活用户
     */
    @Log(title = "用户激活", businessType = BusinessType.UPDATE,eventType = EventType.system)
    @SaCheckPermission("system:active:do")
    @PostMapping("/do")
    public R<Void> doActive(@RequestBody SysUserBo user) {
        userService.updateUserStatus(user.getUserId(),UserStatus.OK.getCode());
        return R.ok();
    }

}
