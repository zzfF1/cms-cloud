package com.sinosoft.common.core;

/**
 * 业务处理器接口
 */
public interface IBusinessProcessor {

    /**
     * 处理业务
     *
     * @return 处理结果 true:成功 false:失败
     */
    boolean process();
}
