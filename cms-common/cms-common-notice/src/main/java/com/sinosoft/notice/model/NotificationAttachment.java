package com.sinosoft.notice.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知附件
 *
 * @author zzf
 */
@Data
@Accessors(chain = true)
public class NotificationAttachment implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 附件ID */
    private String id;

    /** 附件名称 */
    private String name;

    /** 附件类型 */
    private String type;

    /** 附件URL */
    private String url;

    /** 附件大小 */
    private Long size;

    /** 附件扩展信息 */
    private Map<String, Object> extension = new HashMap<>();
}
