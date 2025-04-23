package com.sinosoft.system.controller.cmscommonmodule;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.schema.commission.domain.Lmriskapp;
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
 * 险种查询
 *
 * @author: zzf
 * @create: 2025-03-23 19:08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon/riskcode")
public class RiskController extends BaseController {
    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;

    /**
     * 查询管理机构
     *
     * @return 管理机构
     */
    @PostMapping("/queryRiskLabel")
    public R<List<LabelShowVo>> queryComLabel() {
        List<LabelShowVo> dataList = commonService.queryRiskCode(new Lmriskapp());
        return R.ok(dataList);
    }

    /**
     * @return 险种树
     */
    @GetMapping("/riskTree")
    public R<List<Tree<String>>> riskTree() {
        return R.ok(commonService.selectRiskTreeList());
    }
}
