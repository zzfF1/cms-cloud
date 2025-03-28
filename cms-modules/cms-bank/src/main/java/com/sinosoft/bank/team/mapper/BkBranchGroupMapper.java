package com.sinosoft.bank.team.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.schema.commission.domain.vo.LaBranchGroupVo;
import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupFormVo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupShowVo;
import com.sinosoft.common.schema.team.domain.Labranchgroup;


public interface BkBranchGroupMapper extends BaseMapperPlus<Labranchgroup, LaBranchGroupVo> {

    /**
     * 分页查询
     *
     * @param page
     * @param wrapper
     * @return
     */
    Page<BkBranchGroupShowVo> selectBkPageShowList(@Param("page") Page<Labranchgroup> page, @Param("ew") Wrapper<Labranchgroup> wrapper);

    /**
     * 获取最大编号
     *
     * @param branchType    渠道标志
     * @param branchLevel   级别
     * @param curBranchAttr 当前机构属性
     * @return 当前最大编号
     */
    String getMaxBranchAttr(@Param("branchType") String branchType, @Param("branchLevel") String branchLevel, @Param("curBranchAttr") String curBranchAttr);

    /**
     * 查询销售机构表单展示信息
     *
     * @param agentGroup 销售机构主键
     * @return 展示信息
     */
    BkBranchGroupFormVo selectFromById(@Param("agentGroup") String agentGroup);

}
