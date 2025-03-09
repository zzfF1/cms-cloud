package com.sinosoft.system.dubbo;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.system.api.RemoteTenantService;
import com.sinosoft.system.api.domain.vo.RemoteTenantVo;
import com.sinosoft.system.domain.bo.SysTenantBo;
import com.sinosoft.system.domain.vo.SysTenantVo;
import com.sinosoft.system.service.ISysTenantService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhujie
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteTenantServiceImpl implements RemoteTenantService {

    private final ISysTenantService tenantService;

    /**
     * 根据租户id获取租户详情
     */
    @Override
    public RemoteTenantVo queryByTenantId(String tenantId) {
        SysTenantVo vo = tenantService.queryByTenantId(tenantId);
        return MapstructUtils.convert(vo, RemoteTenantVo.class);
    }

    /**
     * 获取租户列表
     */
    @Override
    public List<RemoteTenantVo> queryList() {
        List<SysTenantVo> list = tenantService.queryList(new SysTenantBo());
        return MapstructUtils.convert(list, RemoteTenantVo.class);
    }

}
