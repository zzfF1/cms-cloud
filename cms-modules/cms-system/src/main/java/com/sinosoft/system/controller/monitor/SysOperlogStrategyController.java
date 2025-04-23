package com.sinosoft.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysOperlogStrategyBo;
import com.sinosoft.system.dubbo.RemoteLogServiceImpl;
import com.sinosoft.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 日志策略配置
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/logStrategy")
public class SysOperlogStrategyController extends BaseController {

    private final ISysOperLogService operLogService;
    private final RemoteLogServiceImpl remoteLogServiceImpl;

    /**
     * 获取操作日志记录列表
     */
    @SaCheckPermission("monitor:logStrategy:getConfig")
    @GetMapping("/getConfig")
    public R getConfig() {
        return R.ok(operLogService.obtainStrategy());
    }

    /**
     * 编辑日志策略
     * @param bo
     */
    @Log(title = "日志策略", businessType = BusinessType.UPDATE ,eventType = EventType.system)
    @SaCheckPermission("monitor:logStrategy:edit")
    @PostMapping("edit")
    public R<Void> edit(@RequestBody SysOperlogStrategyBo bo) {
        operLogService.editStrategy(bo);
        return R.ok();
    }


}
