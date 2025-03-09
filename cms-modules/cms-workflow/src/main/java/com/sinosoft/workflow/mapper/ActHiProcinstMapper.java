package com.sinosoft.workflow.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.workflow.domain.ActHiProcinst;

/**
 * 流程实例Mapper接口
 *
 * @author may
 * @date 2023-07-22
 */
@InterceptorIgnore(tenantLine = "true")
public interface ActHiProcinstMapper extends BaseMapperPlus<ActHiProcinst, ActHiProcinst> {

}
