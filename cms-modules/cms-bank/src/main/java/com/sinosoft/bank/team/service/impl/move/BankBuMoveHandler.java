package com.sinosoft.bank.team.service.impl.move;

import com.sinosoft.common.assess.*;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.enums.BranchLevelEnum;
import com.sinosoft.bank.common.core.BankBranchHandler;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Latree;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 部层级异动
 *
 * @author: zzf
 * @create: 2023-11-30 16:13
 */
@Slf4j
@Service
public class BankBuMoveHandler implements IAssessConfirmHandler {

    @Override
    public void confirm(GraderConfirmAgent confirmAgent, AdjustContext adjustContext) throws Exception {
        //获取机构树
        BranchTree branchTree = adjustContext.getBranchTree();
        //获取当前销售机构
        BranchTreeNode curNode = branchTree.getBranchTreeNodeByBranchId(confirmAgent.getCurBranchGroup().getAgentgroup());
        if (curNode == null) {
            log.error("{}异动销售机构不存在!", confirmAgent.getCurBranchGroup().getAgentgroup());
            throw new ServiceException("异动销售机构不存在!");
        }
        //目标节点
        BranchTreeNode targetNode = branchTree.getBranchTreeNodeByBranchId(confirmAgent.getDestAgentGroup());
        if (targetNode == null) {
            log.error("{}目标销售机构不存在!", confirmAgent.getDestAgentGroup());
            throw new ServiceException("目标销售机构不存在!");
        }
        if (!BranchLevelEnum.YB03.getCode().equals(targetNode.getLevel())) {
            log.error("{}目标销售机构不是区!", confirmAgent.getDestAgentGroup());
            throw new ServiceException("目标销售机构不是区!");
        }
        //获取人员
        Map<String, String> teamAgents = new HashMap<>(curNode.getAgents());
        //如果管理机构不为空
        if (StringUtils.isNotBlank(confirmAgent.getManageCom())) {
            //循环人员
            for (String agentCode : teamAgents.keySet()) {
                Laagent agentSchema = branchTree.getAgentSchema(agentCode);
                Latree treeSchema = branchTree.getTreeSchema(agentCode);
                if (agentSchema != null) {
                    agentSchema.setManagecom(confirmAgent.getManageCom());
                }
                if (treeSchema != null) {
                    treeSchema.setManagecom(confirmAgent.getManageCom());
                }
            }
        }
        BranchTreeNode buTreeNode = BankBranchHandler.convertNewNodeByAgent(confirmAgent.getDestBranchName(), confirmAgent, BranchLevelEnum.YB02, branchTree, curNode, targetNode, false);
        //异动人员
        branchTree.agentMoveTreeNode(teamAgents, null, confirmAgent.getConfirmDate(), buTreeNode, curNode);
        //重置主管
        branchTree.resetTreeNodeUpdate(buTreeNode, confirmAgent);
        //清空管理机构
        curNode.clearManager();
    }

}
