package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.log.enums.EventType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.excel.utils.ExcelUtil;
import com.sinosoft.common.idempotent.annotation.RepeatSubmit;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysPageConfigBo;
import com.sinosoft.system.domain.vo.SysPageConfigVo;
import com.sinosoft.system.service.ISysPageConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 界面配置
 *
 * @author zzf
 * @date 2024-07-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/pageConfig")
public class SysPageConfigController extends BaseController {

    private final ISysPageConfigService sysPageConfigService;

    /**
     * 查询界面配置列表
     */
    @SaCheckPermission("system:pageConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysPageConfigVo> list(SysPageConfigBo bo, PageQuery pageQuery) {
        return sysPageConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出界面配置列表
     */
    @SaCheckPermission("system:pageConfig:export")
    @Log(title = "导出界面配置列表", businessType = BusinessType.EXPORT, eventType = EventType.system)
    @PostMapping("/export")
    public void export(SysPageConfigBo bo, HttpServletResponse response) {
        List<SysPageConfigVo> list = sysPageConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "界面配置", SysPageConfigVo.class, response);
    }

    /**
     * 获取界面配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:pageConfig:query")
    @GetMapping("/{id}")
    public R<SysPageConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        return R.ok(sysPageConfigService.queryById(id));
    }

    /**
     * 新增界面配置
     */
    @SaCheckPermission("system:pageConfig:add")
    @Log(title = "界面配置", businessType = BusinessType.INSERT, eventType = EventType.system)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysPageConfigBo bo) {
        return toAjax(sysPageConfigService.insertByBo(bo));
    }

    /**
     * 修改界面配置
     */
    @SaCheckPermission("system:pageConfig:edit")
    @Log(title = "界面配置", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysPageConfigBo bo) {
        return toAjax(sysPageConfigService.updateByBo(bo));
    }

    /**
     * 删除界面配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:pageConfig:remove")
    @Log(title = "界面配置", businessType = BusinessType.DELETE, eventType = EventType.system)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysPageConfigService.deleteWithValidByIds(List.of(ids), true));
    }
}
