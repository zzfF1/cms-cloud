package com.sinosoft.api.agent.mapper;


import com.sinosoft.api.agent.domain.vo.SaleInfoVo;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.common.schema.broker.domain.Lasaleagent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销管中介人员信息Mapper接口
 *
 * @author zzf
 * @date 2024-09-03
 */
public interface GrpLasaleagentMapper extends BaseMapperPlus<Lasaleagent, Lasaleagent> {

    /**
     * 查询中介机构中介人员信息
     *
     * @param manageCom 管理机构
     * @return 第三方人员信息
     */
    List<SaleInfoVo> selectSaleAgentInfo(@Param("manageCom") String manageCom);
}
