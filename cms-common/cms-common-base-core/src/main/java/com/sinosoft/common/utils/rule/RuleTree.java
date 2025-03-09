package com.sinosoft.common.utils.rule;

import com.sinosoft.common.schema.common.domain.LaElMain;
import com.sinosoft.common.schema.common.domain.LaElRule;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: 规则树对象
 * @author: zzf
 * @create: 2023-07-01 13:23
 */
@Data
public class RuleTree implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 根节点
     */
    private RuleNode root;
    /**
     * 规则主表
     */
    private LaElMain elMain;
    /**
     * 规则节点集合
     */
    private Map<Long, LaElRule> mRuleMap = new HashMap<>();

    /**
     * @param elMain 规则主表
     */
    public RuleTree(LaElMain elMain) {
        //初始化根节点
        root = new RuleNode(0L, 0);
        this.elMain = elMain;
    }

    /**
     * 初始化规则树
     *
     * @param list 规则集合
     */
    public void initTree(List<LaElRule> list) {
        //如果空集合，直接返回
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, RuleNode> nodeMap = new HashMap<>();
        //遍历
        for (LaElRule rule : list) {
            RuleNode node = new RuleNode(rule.getId(), rule.getSortOrder().doubleValue());
            nodeMap.put(rule.getId(), node);
            mRuleMap.put(rule.getId(), rule);
        }
        //循环规则
        for (LaElRule rule : list) {
            Long id = rule.getId();
            Long parentId = rule.getParentId();

            RuleNode node = nodeMap.get(id);
            //为空或为0
            if (parentId == null || parentId == 0L) {
                this.root.addChild(node);
            } else {
                RuleNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    //添加子节点
                    parent.addChild(node);
                }
            }
        }
    }
}
