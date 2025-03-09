package com.sinosoft.system.dubbo;

import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.json.utils.JsonUtils;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.system.api.domain.vo.ApiPermissionVo;
import com.sinosoft.system.api.domain.vo.ClientConfigVo;
import com.sinosoft.system.domain.bo.SysClientBo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.system.api.RemoteClientService;
import com.sinosoft.system.api.domain.vo.RemoteClientVo;
import com.sinosoft.system.domain.vo.SysClientVo;
import com.sinosoft.system.service.ISysClientService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 客户端服务
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteClientServiceImpl implements RemoteClientService {

    private final ISysClientService sysClientService;

    /**
     * 根据客户端id获取客户端详情
     */
    @Override
    public RemoteClientVo queryByClientId(String clientId) {
        SysClientVo vo = sysClientService.queryByClientId(clientId);
        RemoteClientVo remoteVo = MapstructUtils.convert(vo, RemoteClientVo.class);
        //如果客户端参数不为空
        if (StringUtils.isNotBlank(vo.getConfig())) {
            //将客户端参数转换为对象
            remoteVo.setClientConfig(JsonUtils.parseObject(vo.getConfig(), ClientConfigVo.class));
        }
        return remoteVo;
    }


    /**
     * 初始化客户端redis
     */
    @PostConstruct
    public void initClientRedis() {
        //查询所有客户端信息
        List<SysClientVo> clientVos = sysClientService.queryList(new SysClientBo());
        //循环
        for (SysClientVo clientVo : clientVos) {
            //如果不在存缓存
//            if (!RedisUtils.hasHashKey(CacheNames.SYS_CLIENT_CONFIG, clientVo.getClientId())) {
                RemoteClientVo remoteVo = MapstructUtils.convert(clientVo, RemoteClientVo.class);
                //如果配置不为空
                if (StringUtils.isNotBlank(clientVo.getConfig())) {
                    remoteVo.setClientConfig(JsonUtils.parseObject(clientVo.getConfig(), ClientConfigVo.class));
                }
                RedisUtils.setCacheMapValue(CacheNames.SYS_CLIENT_CONFIG, clientVo.getClientId(), remoteVo);
                //如果api接口权限不为空
                if (StringUtils.isNotBlank(clientVo.getApiPermissions())) {
                    //先删除一下权限
                    RedisUtils.delCacheMapKey(CacheNames.SYS_CLIENT_API_PERMISSION + ":" + clientVo.getClientId());
                    //将权限转换为对象
                    List<ApiPermissionVo> apiPermissionVos = JsonUtils.parseArray(clientVo.getApiPermissions(), ApiPermissionVo.class);
                    //循环权限
                    for (ApiPermissionVo apiPermissionVo : apiPermissionVos) {
                        //缓存api权限
                        RedisUtils.setCacheMapValue(CacheNames.SYS_CLIENT_API_PERMISSION + ":" + clientVo.getClientId(), apiPermissionVo.getApi(), apiPermissionVo);
                    }
                }
//            }
        }
    }

}
