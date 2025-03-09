package com.sinosoft.common.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.schema.common.domain.WebInterfaceConfig;
import com.sinosoft.common.schema.common.mapper.WebInterfaceConfigMapper;
import org.springframework.stereotype.Component;

/**
 * 接口处理基类
 *
 * @author: zzf
 * @create: 2024-12-10 22:22
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class WebInterfaceBase {

    private final WebInterfaceConfigMapper interfaceConfigMapper;

    /**
     * 接口处理
     *
     * @param interfaceCode 接口编码
     * @param data          数据
     */
    public WebInterfaceResult sendMsg(String interfaceCode, Object data) {
        log.info("{}接口处理", interfaceCode);
        // 查询接口配置
        WebInterfaceConfig webInterfaceConfig = interfaceConfigMapper.selectById(interfaceCode);
        if (webInterfaceConfig == null) {
            log.error("接口{}未配置", interfaceCode);
            throw new ServiceException("[" + interfaceCode + "]接口未配置!");
        }
        if (StringUtils.isBlank(webInterfaceConfig.getDataHandlerClass())) {
            log.error("接口{}数据处理类未配置", interfaceCode);
            throw new ServiceException("[" + interfaceCode + "]数据处理类未配置!");
        }
        if (StringUtils.isBlank(webInterfaceConfig.getSendHandlerClass())) {
            log.error("接口{}数据发送类未配置", interfaceCode);
            throw new ServiceException("[" + interfaceCode + "]数据发送类未配置!");
        }
        // 获取数据处理类
        IWebInterfaceMsgHandler msgHandler = SpringUtils.getBean(webInterfaceConfig.getDataHandlerClass());
        if (msgHandler == null) {
            log.error("接口{}数据处理类{}初始化失败", interfaceCode, webInterfaceConfig.getDataHandlerClass());
            throw new ServiceException("[" + interfaceCode + "]数据处理类初始化失败!");
        }
        // 组装消息
        String resultVal = msgHandler.assembleMessage(data);
        if (StringUtils.isBlank(resultVal)) {
            log.error("接口{}数据处理失败", interfaceCode);
            throw new ServiceException("[" + interfaceCode + "]数据处理失败!");
        }
        IWebInterfaceSendHandler sendHandler = SpringUtils.getBean(webInterfaceConfig.getSendHandlerClass());
        if (sendHandler == null) {
            log.error("接口{}数据发送类{}初始化失败", interfaceCode, webInterfaceConfig.getSendHandlerClass());
            throw new ServiceException("[" + interfaceCode + "]数据发送类初始化失败!");
        }
        return msgHandler.msgCallback(sendHandler.sendMsg(resultVal));
    }
}
