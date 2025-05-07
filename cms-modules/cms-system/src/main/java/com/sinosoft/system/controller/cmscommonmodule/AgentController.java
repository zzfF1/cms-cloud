package com.sinosoft.system.controller.cmscommonmodule;

import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.schema.agent.domain.bo.LaAgentGradeBo;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.service.ICmsCommonService;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.LaGradeQueryBo;
import com.sinosoft.system.service.ICommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 人员相关查询管理
 * -----代理人相关查询
 * -----第三方人员相关查询
 *
 * @author: zzf
 * @create: 2025-03-23 19:08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon/agent")
public class AgentController extends BaseController {
    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;

    @PostMapping("/queryGradeLabel")
    public R<List<LabelShowVo>> queryGradeLabel(@Validated @RequestBody LaGradeQueryBo queryBo) {
        LaAgentGradeBo bo = new LaAgentGradeBo();
        //设置当前用户管理机构() 团险新客户数配置职级下拉
        bo.setBranchType(queryBo.getBranchType());
        List<LabelShowVo> dataList = commonService.queryLaAgentGradeLabel();
        return R.ok(dataList);
    }
}
