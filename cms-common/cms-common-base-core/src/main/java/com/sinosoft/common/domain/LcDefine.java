package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程定义对象 lc_define
 *
 * @author zzf
 * @date 2023-07-02
 */
@Data
@TableName("lc_define")
public class LcDefine implements Serializable {

    @Serial
    private static final long serialVersionUID = 4330575841734204453L;

    /**
     * 流程id
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 顺序号
     */
    private Integer recno;

    /**
     * 流程类型
     */
    private Integer lcSerialno;

    /**
     * 下一个流程节点
     */
    private Integer nextId;


}
