package com.sinosoft.common.utils.rule;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @program: cms6
 * @description: 规则节点
 * @author: zzf
 * @create: 2023-07-01 13:02
 */
@Data
public class RuleNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 规则id
     */
    private Long id;
    /**
     * 下级规则集合
     */
    private List<RuleNode> children = new ArrayList<>();
    /**
     * 排序号
     */
    private double orderNum = 0;

    /**
     * @param id 规则id
     */
    public RuleNode(Long id, double orderNum) {
        this.id = id;
        this.orderNum = orderNum;
    }

    /**
     * 添加子节点
     *
     * @param child
     */
    public void addChild(RuleNode child) {
        children.add(child);
        //排序
        Collections.sort(children, Comparator.comparingDouble(RuleNode::getOrderNum));
    }
}
