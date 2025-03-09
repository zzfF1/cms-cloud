package com.sinosoft.notice.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知操作
 *
 * @author zzf
 */
@Data
@Accessors(chain = true)
public class NotificationAction implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 操作ID
     */
    private String id;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作URL
     */
    private String url;

    /**
     * 操作方法（GET/POST）
     */
    private String method = "GET";

    /**
     * 操作参数
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 确认信息
     */
    private String confirmMessage;

    /**
     * 操作标识（primary/danger/warning/success/info）
     */
    private String style = "primary";

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer order = 0;
}
