package com.sinosoft.notice.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 通知查询条件DTO
 */
@Data
public class NotificationQueryDTO {
    /** 用户ID */
    private Long userId;

    /** 通知类型 */
    private String type;

    /** 是否已读 */
    private Boolean isRead;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 通知标题关键字 */
    private String keyword;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页记录数 */
    private Integer pageSize = 10;
}
