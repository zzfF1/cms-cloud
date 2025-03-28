package com.sinosoft.bank.team.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.bank.team.domain.vo.BkBranchMoveVo;
import com.sinosoft.bank.team.domain.vo.BkShowMoveBranchVo;
import com.sinosoft.common.schema.team.domain.Labranchchange;

/**
 * 团队异动Mapper接口
 */
public interface BkBranchMoveMapper extends BaseMapperPlus<Labranchchange, Labranchchange> {

    /**
     * 查询团队异动
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 人员异动集合
     */
    Page<BkBranchMoveVo> selectPageList(Page<Labranchchange> page, @Param("ew") Wrapper<Labranchchange> queryWrapper);

    /**
     * 查询可异动机构列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 机构列表
     */
    Page<BkShowMoveBranchVo> selectShowMoveBranchPageList(Page<Labranchchange> page, @Param("ew") Wrapper<Labranchchange> queryWrapper);
}
