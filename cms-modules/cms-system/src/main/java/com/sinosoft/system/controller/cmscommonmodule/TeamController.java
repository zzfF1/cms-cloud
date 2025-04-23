package com.sinosoft.system.controller.cmscommonmodule;

import com.sinosoft.common.domain.bo.BranchGroupQueryBo;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.team.domain.vo.BranchGroupShowVo;
import com.sinosoft.common.service.ICmsCommonService;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.service.ICommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 团队查询管理
 *
 * @author: zzf
 * @create: 2025-03-23 19:08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon/team")
public class TeamController extends BaseController {
    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;

    /**
     * 销售机构查询
     *
     * @param bo        查询对象
     * @param pageQuery 流程类型
     * @return 流程轨迹
     */
    @GetMapping("/branchlist")
    public TableDataInfo<BranchGroupShowVo> branchlist(BranchGroupQueryBo bo, PageQuery pageQuery) {
        return commonService.queryBranchPageList(bo, pageQuery);
    }
}
