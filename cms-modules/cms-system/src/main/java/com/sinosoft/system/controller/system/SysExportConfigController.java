package com.sinosoft.system.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.excel.utils.ExcelUtil;
import com.sinosoft.common.idempotent.annotation.RepeatSubmit;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysExportConfigBo;
import com.sinosoft.system.domain.vo.SysExportConfigSheetVo;
import com.sinosoft.system.domain.vo.SysExportConfigVo;
import com.sinosoft.system.service.ISysExportConfigService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导出配置
 *
 * @author demo
 * @date 2024-04-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/exportConfig")
public class SysExportConfigController extends BaseController {

    private final ISysExportConfigService sysExportConfigService;

    /**
     * 查询Excel导出配置列表
     */
    @SaCheckPermission("system:exportConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysExportConfigVo> list(SysExportConfigBo bo, PageQuery pageQuery) {
        return sysExportConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出Excel导出配置列表
     */
    @SaCheckPermission("system:exportConfig:export")
    @Log(title = "Excel导出配置", businessType = BusinessType.EXPORT, eventType = EventType.system)
    @PostMapping("/export")
    public void export(SysExportConfigBo bo, HttpServletResponse response) {
        List<SysExportConfigVo> list = sysExportConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "Excel导出配置", SysExportConfigVo.class, response);
    }

    /**
     * 获取Excel导出配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:exportConfig:query")
    @GetMapping("/{id}")
    public R<SysExportConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                        @PathVariable Long id) {
        return R.ok(sysExportConfigService.queryById(id));
    }

    /**
     * 新增Excel导出配置
     */
    @SaCheckPermission("system:exportConfig:add")
    @Log(title = "Excel导出配置", businessType = BusinessType.INSERT, eventType = EventType.system)
    @RepeatSubmit()
    @PostMapping
    public R<Long> add(@Validated(AddGroup.class) @RequestBody SysExportConfigBo bo) {
        Long id = sysExportConfigService.insertByBo(bo);
        return R.ok(id);
    }

    /**
     * 修改Excel导出配置
     */
    @SaCheckPermission("system:exportConfig:edit")
    @Log(title = "Excel导出配置", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysExportConfigBo bo) {
        return toAjax(sysExportConfigService.updateByBo(bo));
    }

    /**
     * 删除Excel导出配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:exportConfig:remove")
    @Log(title = "Excel导出配置", businessType = BusinessType.DELETE, eventType = EventType.system)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysExportConfigService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 下载Excel模板
     *
     * @param id 配置ID
     */
    @SaCheckPermission("system:exportConfig:download")
    @GetMapping("/download/{id}")
    public void download(@NotNull(message = "配置ID不能为空")
                         @PathVariable Long id,
                         HttpServletResponse response) throws IOException {
        // 查询配置信息
        SysExportConfigVo config = sysExportConfigService.queryById(id);
        if (config == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 生成Excel文件
        Map<String, Object> params = new HashMap<>();
        // TODO: 可从请求参数中获取查询条件
        String filePath = sysExportConfigService.generateExcel(id, params);

        // 设置响应头
        File file = new File(filePath);
        String fileName = URLEncoder.encode(config.getName() + ".xlsx", StandardCharsets.UTF_8);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentLength((int) file.length());

        // 写入响应
        Files.copy(Paths.get(filePath), response.getOutputStream());
        response.getOutputStream().flush();

        // 删除临时文件
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (Exception e) {
            // 忽略删除失败的异常
        }
    }

    /**
     * 上传模板文件
     */
    @SaCheckPermission("system:exportConfig:upload")
    @Log(title = "上传模板文件", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("请选择文件");
        }
        // 上传文件并获取路径
        String path = sysExportConfigService.uploadTemplate(file.getOriginalFilename(), file.getBytes());
        return R.ok(path);
    }

    /**
     * 一次性获取完整的导出配置信息（包含基本信息和Sheet配置）
     *
     * @param id 主键
     */
    @SaCheckPermission("system:exportConfig:query")
    @GetMapping("/full/{id}")
    public R<Map<String, Object>> getFullExportConfig(@NotNull(message = "主键不能为空")
                                                      @PathVariable Long id) {
        // 获取配置基本信息
        SysExportConfigVo basicInfo = sysExportConfigService.queryById(id);
        if (basicInfo == null) {
            return R.fail("配置不存在");
        }

        // 获取Sheet配置信息
        List<SysExportConfigSheetVo> sheetInfo = sysExportConfigService.querySheetsByConfigId(id);

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("basicInfo", basicInfo);
        result.put("sheetInfo", sheetInfo);

        return R.ok(result);
    }

}
