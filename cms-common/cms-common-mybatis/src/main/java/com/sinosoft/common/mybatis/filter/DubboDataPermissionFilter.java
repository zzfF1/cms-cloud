package com.sinosoft.common.mybatis.filter;

import com.sinosoft.common.mybatis.helper.DataPermissionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;

/**
 * dubbo 数据权限参数传递
 *
 * @author zzf
 */
@Slf4j
@Activate(group = {CommonConstants.CONSUMER})
public class DubboDataPermissionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcServiceContext context = RpcContext.getServiceContext();
        Map<String, Object> dataPermissionContext = DataPermissionHelper.getContext();
        context.setObjectAttachment(DataPermissionHelper.DATA_PERMISSION_KEY, dataPermissionContext);
        return invoker.invoke(invocation);
    }

}
