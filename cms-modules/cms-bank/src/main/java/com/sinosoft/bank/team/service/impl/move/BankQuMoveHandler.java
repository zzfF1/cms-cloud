package com.sinosoft.bank.team.service.impl.move;

import com.sinosoft.common.assess.*;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.enums.BranchLevelEnum;
import com.sinosoft.bank.common.core.BankBranchHandler;
import org.springframework.stereotype.Service;

/**
 * 区层级异动
 *
 * @author: zzf
 * @create: 2023-11-30 16:13
 */
@Slf4j
@Service
public class BankQuMoveHandler implements IAssessConfirmHandler {

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
        BranchTreeNode targetNode = branchTree.getRootNode();
        //机构异动
        BankBranchHandler.nodeMove(branchTree, confirmAgent, BranchLevelEnum.YB03, BranchLevelEnum.YB03, targetNode, curNode);
        curNode.clearAllZxManager();
    }

}
