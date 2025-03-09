package com.sinosoft.common.forest.client;

import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.constant.HeaderConstants;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import org.springframework.stereotype.Component;

/**
 * 客户端地址映射
 *
 * @author: zzf
 * @create: 2025-03-06 13:50
 */
@Component
public class ClientAddressSource implements AddressSource {

    @Override
    public ForestAddress getAddress(ForestRequest req) {
        String clientId = req.getHeaderValue(HeaderConstants.CLIENT_ID);
        if (StringUtils.isBlank(clientId)) {
            throw new ServiceException("客户端ID为空!");
        }
        RemoteClientVo clientVo = RedisUtils.getCacheMapValue(CacheNames.SYS_CLIENT_CONFIG, clientId);
        if (clientVo == null) {
            throw new ServiceException(clientId + "客户端不在存!");
        }
        if (StringUtils.isBlank(clientVo.getClientConfig().getServerPath())) {
            throw new ServiceException("客户端服务地址为空!");
        }
        if (clientVo.getClientConfig().getPort() == null || clientVo.getClientConfig().getPort() == 0) {
            throw new ServiceException("客户端服务端口不正确!");
        }
        return new ForestAddress(clientVo.getClientConfig().getServerPath(), clientVo.getClientConfig().getPort());
    }
}
