package com.sinosoft.common.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.domain.LcProcesstrack;

import java.util.Date;

/**
 * @program: cms6
 * @description: 流程轨迹显示
 * @author: zzf
 * @create: 2023-10-02 09:57
 */
@Data
@AutoMapper(target = LcProcesstrack.class)
public class LcProcessShowVo {

    /**
     * 最后标志
     */
    private Integer lastFlag;

    /**
     * 操作类型 0保存 1提交 -1退回 -2一键撤回
     */
    private Integer czType;
    /**
     * 流程值
     */
    private String lcName;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date makeDate;

    /**
     * 意见
     */
    private String yj;
}
