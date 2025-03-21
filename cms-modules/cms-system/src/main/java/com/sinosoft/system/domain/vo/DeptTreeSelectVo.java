package com.sinosoft.system.domain.vo;

import cn.hutool.core.lang.tree.Tree;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色部门列表树信息
 *
 * @author Michelle.Chung
 */
@Data
public class DeptTreeSelectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 选中部门列表
     */
    private List<Long> checkedKeys;

    /**
     * 下拉树结构列表
     */
    private List<Tree<Long>> depts;

}
