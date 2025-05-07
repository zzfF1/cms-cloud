package com.sinosoft.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.system.domain.vo.SysLogininfoStatisticVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.constant.CacheConstants;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.excel.utils.ExcelUtil;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysLogininforBo;
import com.sinosoft.system.domain.vo.SysLogininforVo;
import com.sinosoft.system.service.ISysLogininforService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统访问记录
 *
 * @author zzf
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    /**
     * 获取系统访问记录列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforBo logininfor, PageQuery pageQuery) {
        return logininforService.selectPageLogininforList(logininfor, pageQuery);
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "系统访问记录", businessType = BusinessType.EXPORT ,eventType = EventType.system)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLogininforBo logininfor, HttpServletResponse response) {
        List<SysLogininforVo> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
    }

    /**
     * 批量删除登录日志
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "系统访问记录", businessType = BusinessType.DELETE ,eventType = EventType.system)
    @PostMapping("/remove/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "系统访问记录", businessType = BusinessType.CLEAN ,eventType = EventType.system)
    @PostMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "系统访问记录", businessType = BusinessType.OTHER ,eventType = EventType.system)
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@PathVariable("userName") String userName) {
        String loginName = CacheConstants.PWD_ERR_CNT_KEY + userName;
        if (RedisUtils.hasKey(loginName)) {
            RedisUtils.deleteObject(loginName);
        }
        return R.ok();
    }

    /**
     * 统计图表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @PostMapping("/statistics")
    public R<List<SysLogininfoStatisticVo>> statistics(@RequestBody SysLogininforBo logininfor) {
        return R.ok(logininforService.logininfoStatistic(logininfor));
    }

}
