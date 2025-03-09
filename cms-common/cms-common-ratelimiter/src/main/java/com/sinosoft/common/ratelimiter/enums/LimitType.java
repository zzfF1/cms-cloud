package com.sinosoft.common.ratelimiter.enums;

/**
 * 限流类型
 *
 * @author zzf
 */

public enum LimitType {
    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 实例限流(集群多后端实例)
     */
    CLUSTER
}
