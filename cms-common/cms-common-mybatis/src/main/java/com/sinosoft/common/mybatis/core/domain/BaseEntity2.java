package com.sinosoft.common.mybatis.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: cms6
 * @description: 基础字段
 * @author: zzf
 * @create: 2023-06-30 12:04
 */
@Data
public class BaseEntity2  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 操作者
     */
    private String operator;
    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date makedate;
    /**
     * 创建日期
     */
    private String maketime;
    /**
     * 修改操作者
     */
    private String modifyoperator;
    /**
     * 修改日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date modifydate;
    /**
     * 修改时间
     */
    private String modifytime;
}
