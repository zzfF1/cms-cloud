package com.sinosoft.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.system.domain.vo.SysOperLogStatisticVo;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.common.excel.utils.ExcelUtil;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysOperLogBo;
import com.sinosoft.system.domain.vo.SysOperLogVo;
import com.sinosoft.system.service.ISysOperLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author zzf
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController {

    private final ISysOperLogService operLogService;

    /**
     * 获取操作日志记录列表
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    public TableDataInfo<SysOperLogVo> list(SysOperLogBo operLog, PageQuery pageQuery) {
        return operLogService.selectPageOperLogList(operLog, pageQuery);
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT ,eventType = EventType.system)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    public void export(SysOperLogBo operLog, HttpServletResponse response) {
        List<SysOperLogVo> list = operLogService.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo.class, response);
    }

    /**
     * 批量删除操作日志记录
     * @param operIds 日志ids
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE ,eventType = EventType.system)
    @SaCheckPermission("monitor:operlog:remove")
    @PostMapping("/delete/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * 清理操作日志记录
     */
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN ,eventType = EventType.system)
    @PostMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }

    /**
     * 操作日志统计
     */
    @SaCheckPermission("monitor:operlog:list")
    @PostMapping("/statistics")
    public R<List<SysOperLogStatisticVo>> statistics(@RequestBody SysOperLogBo bo) {
        if(bo.getParams().get("beginTime")==null || bo.getParams().get("endTime")==null){
            return R.fail("请选择操作时间");
        }
        return R.ok(operLogService.operLogStatistic(bo));
    }
}
