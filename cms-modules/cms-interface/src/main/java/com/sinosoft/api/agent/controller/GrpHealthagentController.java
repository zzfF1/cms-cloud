package com.sinosoft.api.agent.controller;

import com.sinosoft.api.agent.domain.ApiManageQueryBo;
import com.sinosoft.api.agent.domain.bo.CheckContractChangeBo;
import com.sinosoft.api.agent.domain.vo.AgentInfoVo;
import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.api.agent.service.ILaComSaleService;
import com.sinosoft.api.agent.service.ILahealthagentService;
import com.sinosoft.common.core.domain.GlobalResponse;
import com.sinosoft.common.core.enums.ErrorCodeEnum;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.schema.broker.domain.Lasaleagent;
import com.sinosoft.common.web.core.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/grpagent/healthagent")
public class GrpHealthagentController extends BaseController {

    private final ILahealthagentService lahealthinsauthinfoService;
    private final ILaComSaleService isocomSaleService;

    /**
     * 查询健康险人员授权信息列表
     */
    @PostMapping("/list")
    public GlobalResponse<List<AgentInfoVo>> list(@RequestBody ApiManageQueryBo queryBo) {
        if (StringUtils.isBlank(queryBo.getManageCom())) {
            return GlobalResponse.fail(ErrorCodeEnum.PARAM_MISSING.getCode(), "参数缺失", "管理机构为空");
        }
        return GlobalResponse.ok(lahealthinsauthinfoService.queryHealthToAgentList(queryBo.getManageCom()));
    }
    /**
     * 查询中介机构中介人员信息
     */
    @PostMapping("/listLasaleagent")
    public GlobalResponse<List<SaleInfoVo>> listLasaleagent(@RequestBody ApiManageQueryBo queryBo) {
        if (StringUtils.isBlank(queryBo.getManageCom())) {
            return GlobalResponse.fail(ErrorCodeEnum.PARAM_MISSING.getCode(), "参数缺失", "管理机构为空");
        }
        return GlobalResponse.ok(isocomSaleService.selectSaleAgent(queryBo.getManageCom()));
    }

    /**
     * 查询中介机构中介人员信息
     */
    @PostMapping("/checkContractChange")
    public GlobalResponse<List<SaleInfoVo>> checkContractChange(@RequestBody List<CheckContractChangeBo> queryBo) {
        List<SaleInfoVo> saleInfoVos = new ArrayList<>();
        SaleInfoVo vo1= new SaleInfoVo();
        vo1.setAgentCom("a");
        vo1.setName("b");
        vo1.setSaleCode("c");
        vo1.setSaleName("d");
        saleInfoVos.add(vo1);
        return GlobalResponse.ok(saleInfoVos);
    }

    /**
     * 孤儿单分配接口 - 仅用于测试
     */
    @PostMapping("/assignOrphanPolicy")
    public Map<String, Object> assignOrphanPolicy(@RequestBody Map<String, Object> requestMap) {
        log.info("收到分配请求: {}", requestMap);
        // 从请求中获取客户端信息
        Map<String, Object> clientInfo = (Map<String, Object>) requestMap.get("clientInfo");
        Map<String, Object> inputData = (Map<String, Object>) requestMap.get("inputData");
        // 模拟业务处理逻辑
        log.info("处理同步信息, 数量: {}", inputData.size());
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        // 复制请求中的客户端信息到响应中
        response.put("clientInfo", clientInfo);
        // 创建结果对象
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "Y"); // 模拟成功
        // 设置响应体
        response.put("response", resultMap);
        log.info("返回孤儿单分配响应: {}", response);
        return response;
    }
}
