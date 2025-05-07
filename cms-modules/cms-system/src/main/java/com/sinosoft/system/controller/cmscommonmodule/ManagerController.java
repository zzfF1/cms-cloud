package com.sinosoft.system.controller.cmscommonmodule;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.satoken.utils.LoginHelper;
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
 * 管理机构相关查询管理
 *
 * @author: zzf
 * @create: 2025-03-23 19:08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon/manage")
public class ManagerController extends BaseController {
    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;

    /**
     * 查询管理机构
     *
     * @param bo 管理机构查询条件
     * @return 管理机构
     */
    @PostMapping("/queryComLabel")
    public R<List<LabelShowVo>> queryComLabel(@RequestBody LdComQueryBo bo) {
        //设置当前用户管理机构
        bo.setCurUserManageCom(LoginHelper.getDeptId() + "");
        List<LabelShowVo> dataList = commonService.queryComLabel(bo);
        return R.ok(dataList);
    }

    /**
     * @param queryBo 查询条件
     * @return 管理机构树
     */
    @GetMapping("/manageTree")
    public R<List<Tree<String>>> manageTree(@RequestBody LdComQueryBo queryBo) {
        return R.ok(commonService.selectManageTreeList(queryBo));
    }
}
