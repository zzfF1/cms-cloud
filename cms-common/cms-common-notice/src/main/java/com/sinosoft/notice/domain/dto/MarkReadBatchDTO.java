package com.sinosoft.notice.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量标记已读的数据传输对象
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class MarkReadBatchDTO {

    /**
     * 通知ID列表
     */
    @NotEmpty(message = "通知ID列表不能为空")
    private List<Long> notificationIds;
}
