package com.sinosoft.common.event;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @program: cms6
 * @description: 流程轨迹事件
 * @author: zzf
 * @create: 2023-07-04 16:53
 */
@Data
public class LcProcTrackEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 流程类型
     */
    private Integer lcSerialNo;
    /**
     * 流程主键
     */
    private Integer lcId;
    /**
     * 操作类型 0保存 1提交 -1退回
     */
    private Integer czType;
    /**
     * 说明
     */
    private String sm;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 业务数据ID
     */
    private List<String> idsList;
}
