package com.sinosoft.system.domain.vo;

import com.sinosoft.common.schema.common.domain.LcTz;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程跳转
 *
 * @author: zzf
 * @create: 2023-11-16 11:41
 */
@Data
@AutoMapper(target = LcTz.class)
public class LcTzVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer serialno;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 下一个流程节点
     */
    private Integer lcNextId;

    /**
     * 跳转条件
     */
    private String tzTj;

    /**
     * 跳转说明
     */
    private String sm;
}
