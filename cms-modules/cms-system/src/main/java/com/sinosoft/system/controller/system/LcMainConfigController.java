package com.sinosoft.system.controller.system;

import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.validate.AddGroup;
import com.sinosoft.common.core.validate.EditGroup;
import com.sinosoft.common.idempotent.annotation.RepeatSubmit;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.LcDefineBo;
import com.sinosoft.system.domain.vo.LcDefineVo;
import com.sinosoft.system.service.ILcMainConfigService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程配置
 *
 * @author zzf
 * @date 2023-11-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/lcconfig/node")
public class LcMainConfigController extends BaseController {
    private final ILcMainConfigService lcMainService;

    /**
     * 查询流程明细列表
     */
    @GetMapping("/list/{lcSerialNo}")
    public R<List<LcDefineVo>> list(@PathVariable Integer lcSerialNo) {
        return R.ok(lcMainService.queryList(lcSerialNo));
    }

    /**
     * 获取流程明细
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<LcDefineVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Integer id) {
        return R.ok(lcMainService.queryById(id));
    }

    /**
     * 新增流程配置
     */
    @Log(title = "流程配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LcDefineBo bo) {
        return toAjax(lcMainService.insertByBo(bo));
    }

    /**
     * 修改流程定义
     */
    @Log(title = "流程配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LcDefineBo bo) {
        return toAjax(lcMainService.updateByBo(bo));
    }

    /**
     * 删除流程定义
     *
     * @param ids 主键串
     */
    @Log(title = "流程配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Integer[] ids) {
        return toAjax(lcMainService.deleteWithValidByIds(List.of(ids), true));
    }
}
