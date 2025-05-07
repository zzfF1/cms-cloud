package com.sinosoft.system.controller.cmscommonmodule;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.domain.bo.LaComQueryBo;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.service.ICmsCommonService;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.service.ICommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代理机构查询管理
 *
 * @author: zzf
 * @create: 2025-03-23 19:08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon/intermediary")
public class IntermediaryController extends BaseController {
    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;

    /**
     * 查询代理机构标签
     *
     * @param bo 查询条件
     * @return 代理机构标签
     */
    @PostMapping("/queryLaComLabel")
    public R<List<LabelShowVo>> queryLaComLabel(@RequestBody LaComQueryBo bo) {
        //设置当前用户管理机构
        List<LabelShowVo> dataList = commonService.queryLaComLabel(bo);
        return R.ok(dataList);
    }

    /**
     * 查询代理机构树
     *
     * @param queryBo 查询条件
     * @return 代理机构树
     */
    @PostMapping("/comTree")
    public R<List<Tree<String>>> comTree(@RequestBody LaComQueryBo queryBo) {
        return R.ok(commonService.selectComTreeList(queryBo));
    }

    /**
     * 查询所选管理机构的上级管理机构
     *
     * @param bo 查询条件
     * @return 管理机构
     */
    @GetMapping("/queryUpLaComLabel")
    public R<List<LabelShowVo>> queryUpLaComLabel(@RequestBody LaComQueryBo bo) {
        List<LabelShowVo> dataList = commonService.queryUpLaComLabel(bo);
        return R.ok(dataList);
    }
}
