package com.sinosoft.common.core;

/**
 * web接口消息处理器
 */
public interface IWebInterfaceMsgHandler {

    /**
     * 组装消息
     *
     * @param data 数据
     * @return 报文结果
     */
    String assembleMessage(Object data);

    /**
     * 消息回调
     *
     * @param sendResult 发送结果
     * @return 结果
     */
    WebInterfaceResult msgCallback(WebInterfaceResult sendResult);
}
