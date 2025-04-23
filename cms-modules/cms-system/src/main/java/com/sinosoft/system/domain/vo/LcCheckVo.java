package com.sinosoft.system.domain.vo;

import com.sinosoft.common.schema.common.domain.LcCheck;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程检查
 *
 * @author: zzf
 * @create: 2025-01-18 14:02
 */
@Data
@AutoMapper(target = LcCheck.class)
public class LcCheckVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer serialno;

    /**
     * 检查说明
     */
    private String name;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时
     */
    private Integer type;

    /**
     * 检查条件
     */
    private String checkTj;

    /**
     * 检查类型
     */
    private Integer checkType;

    /**
     * 检查提示说明
     */
    private String checkMsg;
}
