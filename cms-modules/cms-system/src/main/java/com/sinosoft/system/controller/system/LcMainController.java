package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.schema.common.domain.LcMain;
import com.sinosoft.common.idempotent.annotation.RepeatSubmit;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.LcMainBo;
import com.sinosoft.system.service.ILcMainSetService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程定义
 *
 * @author zzf
 * @date 2023-11-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/lcconfig/type")
public class LcMainController extends BaseController {
    private final ILcMainSetService lcMainService;

    /**
     * 查询流程定义列表
     */
    @SaCheckPermission("lc:lcdy:list")
    @GetMapping("/list")
    public R<List<LcMain>> list(LcMainBo bo) {
        return R.ok(lcMainService.queryList(bo));
    }

    /**
     * 获取流程定义详细信息
     *
     * @param serialno 主键
     */
    @SaCheckPermission("lc:lcdy:query")
    @GetMapping("/{serialno}")
    public R<LcMain> getInfo(@NotNull(message = "主键不能为空") @PathVariable Integer serialno) {
        return R.ok(lcMainService.queryById(serialno));
    }

    /**
     * 新增流程定义
     */
    @SaCheckPermission("lc:lcdy:add")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LcMainBo bo) {
        return toAjax(lcMainService.insertByBo(bo));
    }

    /**
     * 修改流程定义
     */
    @SaCheckPermission("lc:lcdy:edit")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LcMainBo bo) {
        return toAjax(lcMainService.updateByBo(bo));
    }

    /**
     * 删除流程定义
     *
     * @param serialnos 主键串
     */
    @SaCheckPermission("lc:lcdy:remove")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{serialnos}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Integer[] serialnos) {
        return toAjax(lcMainService.deleteWithValidByIds(List.of(serialnos), true));
    }
}
