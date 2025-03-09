package com.sinosoft.system.domain.convert;

import io.github.linpeilie.BaseMapper;
import com.sinosoft.system.api.domain.bo.RemoteLogininforBo;
import com.sinosoft.system.domain.bo.SysLogininforBo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * 登录日志转换器
 * @author zhujie
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysLogininforBoConvert extends BaseMapper<RemoteLogininforBo, SysLogininforBo> {

}
