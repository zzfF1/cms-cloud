package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.system.domain.vo.SysImportConfigItemVo;
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
import com.sinosoft.system.domain.bo.SysImportConfigBo;
import com.sinosoft.system.domain.vo.SysImportConfigVo;
import com.sinosoft.system.service.ISysImportConfigService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel导入模板配置
 *
 * @author zzf
 * @date 2024-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/importConfig")
public class SysImportConfigController extends BaseController {

    private final ISysImportConfigService sysImportConfigService;

    /**
     * 查询导入模板列表
     */
    @SaCheckPermission("system:importConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysImportConfigVo> list(SysImportConfigBo bo, PageQuery pageQuery) {
        return sysImportConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出导入模板列表
     */
    @SaCheckPermission("system:importConfig:export")
    @Log(title = "导出导入模板列表", businessType = BusinessType.EXPORT, eventType = EventType.system)
    @PostMapping("/export")
    public void export(SysImportConfigBo bo, HttpServletResponse response) {
        List<SysImportConfigVo> list = sysImportConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "导入模板", SysImportConfigVo.class, response);
    }

    /**
     * 获取导入模板详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:importConfig:query")
    @GetMapping("/{id}")
    public R<SysImportConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                        @PathVariable Long id) {
        return R.ok(sysImportConfigService.queryById(id));
    }

    /**
     * 获取导入模板配置详情（针对字段配置）
     *
     * @param id 主键
     */
    @SaCheckPermission("system:importConfig:query")
    @GetMapping("/detail/{id}")
    public R<List<Map<String, Object>>> getConfigDetail(@NotNull(message = "主键不能为空")
                                                        @PathVariable Long id) {
        SysImportConfigVo config = sysImportConfigService.queryById(id);
        if (config == null || config.getConfigItems() == null) {
            return R.ok(new ArrayList<>());
        }

        // 按sheet索引分组
        Map<Long, List<Map<String, Object>>> sheetMap = new HashMap<>();
        config.getConfigItems().forEach(item -> {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("title", item.getTitle());
            fieldMap.put("fieldRequired", item.getFieldRequired());
            fieldMap.put("dataType", item.getDataType());
            fieldMap.put("fillSm", item.getFillSm());
            fieldMap.put("width", item.getWidth());
            fieldMap.put("sort", item.getSort());
            fieldMap.put("id", item.getId());

            Long sheetIndex = item.getSheetIndex();
            if (!sheetMap.containsKey(sheetIndex)) {
                sheetMap.put(sheetIndex, new ArrayList<>());
            }
            sheetMap.get(sheetIndex).add(fieldMap);
        });

        // 构建前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        String[] sheetNames = config.getSheetNames() != null ?
            config.getSheetNames().split(",") : new String[]{};

        for (Map.Entry<Long, List<Map<String, Object>>> entry : sheetMap.entrySet()) {
            Long sheetIndex = entry.getKey();
            List<Map<String, Object>> fields = entry.getValue();

            Map<String, Object> sheetConfig = new HashMap<>();
            // 获取Sheet名称，如果没有对应索引则使用默认名称
            String sheetName = sheetIndex < sheetNames.length ?
                sheetNames[sheetIndex.intValue()] : "Sheet" + (sheetIndex + 1);

            sheetConfig.put("name", sheetName);
            sheetConfig.put("fields", fields);
            result.add(sheetConfig);
        }

        return R.ok(result);
    }

    /**
     * 新增导入模板
     */
    @SaCheckPermission("system:importConfig:add")
    @Log(title = "导入模板", businessType = BusinessType.INSERT, eventType = EventType.system)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysImportConfigBo bo) {
        return toAjax(sysImportConfigService.insertByBo(bo));
    }

    /**
     * 修改导入模板
     */
    @SaCheckPermission("system:importConfig:edit")
    @Log(title = "导入模板", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysImportConfigBo bo) {
        return toAjax(sysImportConfigService.updateByBo(bo));
    }

    /**
     * 保存导入模板配置项
     */
    @SaCheckPermission("system:importConfig:edit")
    @Log(title = "导入模板配置项", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @RepeatSubmit()
    @PostMapping("/saveItems/{id}")
    public R<Void> saveConfigItems(
        @NotNull(message = "主键不能为空") @PathVariable Long id,
        @RequestBody List<SysImportConfigBo> sheetConfigs) {
        return toAjax(sysImportConfigService.saveConfigItems(id, sheetConfigs));
    }

    /**
     * 删除导入模板
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:importConfig:remove")
    @Log(title = "导入模板", businessType = BusinessType.DELETE, eventType = EventType.system)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysImportConfigService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 下载导入模板
     */
    @SaCheckPermission("system:importConfig:download")
    @GetMapping("/download/{id}")
    public void downloadTemplate(
        @NotNull(message = "主键不能为空") @PathVariable Long id,
        HttpServletResponse response) throws IOException {
        SysImportConfigVo config = sysImportConfigService.queryById(id);
        if (config == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] excelBytes = sysImportConfigService.generateExcelTemplate(id);

        String fileName = URLEncoder.encode(config.getName() + ".xlsx", StandardCharsets.UTF_8);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentLength(excelBytes.length);

        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }

    /**
     * 获取完整的模板信息（包含基本信息和Sheet配置）
     *
     * @param id 主键
     */
    @SaCheckPermission("system:importConfig:query")
    @GetMapping("/full/{id}")
    public R<Map<String, Object>> getFullInfo(@NotNull(message = "主键不能为空")
                                              @PathVariable Long id) {
        // 获取模板基本信息
        SysImportConfigVo configVo = sysImportConfigService.queryById(id);
        // 处理Sheet配置信息
        List<Map<String, Object>> sheetList = new ArrayList<>();
        if (configVo != null && configVo.getConfigItems() != null) {
            // 按sheet索引分组
            Map<Long, List<SysImportConfigItemVo>> sheetMap = configVo.getConfigItems().stream()
                .collect(Collectors.groupingBy(SysImportConfigItemVo::getSheetIndex));

            // 获取Sheet名称数组
            String[] sheetNames = configVo.getSheetNames() != null ?
                configVo.getSheetNames().split(",") : new String[]{};

            // 构建前端需要的格式
            for (Map.Entry<Long, List<SysImportConfigItemVo>> entry : sheetMap.entrySet()) {
                Long sheetIndex = entry.getKey();
                List<SysImportConfigItemVo> fields = entry.getValue();

                Map<String, Object> sheetConfig = new HashMap<>();
                // 获取Sheet名称，如果没有对应索引则使用默认名称
                String sheetName = sheetIndex < sheetNames.length ?
                    sheetNames[sheetIndex.intValue()] : "Sheet" + (sheetIndex + 1);

                sheetConfig.put("name", sheetName);
                sheetConfig.put("fields", fields);
                sheetList.add(sheetConfig);
            }
        }
        // 将模板基本信息和Sheet配置信息一起返回
        Map<String, Object> result = new HashMap<>();
        result.put("basicInfo", configVo);
        result.put("sheetInfo", sheetList);
        return R.ok(result);
    }
}
