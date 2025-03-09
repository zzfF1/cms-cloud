package com.sinosoft.common.core;

/**
 * 接口发送处理器
 */
public interface IWebInterfaceSendHandler {

    /**
     * 发送消息
     *
     * @param message 接口消息
     * @return 发送结果
     */
    WebInterfaceResult sendMsg(String message);
}
