package com.sinosoft.gen.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.gen.domain.GenTableColumn;

/**
 * 业务字段 数据层
 *
 * @author zzf
 */
@InterceptorIgnore(dataPermission = "true", tenantLine = "true")
public interface GenTableColumnMapper extends BaseMapperPlus<GenTableColumn, GenTableColumn> {

}
