package com.sinosoft.workflow.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.sinosoft.workflow.domain.ActHiTaskinst;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 流程历史任务Mapper接口
 *
 * @author may
 * @date 2024-03-02
 */
@InterceptorIgnore(tenantLine = "true")
public interface ActHiTaskinstMapper extends BaseMapperPlus<ActHiTaskinst, ActHiTaskinst> {

}
