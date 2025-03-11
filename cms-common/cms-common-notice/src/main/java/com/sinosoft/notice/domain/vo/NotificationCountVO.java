package com.sinosoft.notice.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 未读通知计数视图对象
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class NotificationCountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 未读通知总数
     */
    private Integer total;

    /**
     * 未读待办通知数量
     */
    private Integer todo;

    /**
     * 未读预警通知数量
     */
    private Integer alert;

    /**
     * 未读消息通知数量
     */
    private Integer message;

    /**
     * 未读公告通知数量
     */
    private Integer announcement;
}
