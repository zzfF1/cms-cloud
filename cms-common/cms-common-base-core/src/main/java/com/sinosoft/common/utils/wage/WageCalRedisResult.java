package com.sinosoft.common.utils.wage;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 佣金计算redis结果缓存
 * @author: zzf
 * @create: 2023-07-18 12:09
 */
@Data
public class WageCalRedisResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -2329242764340329811L;
    /**
     * 算法代码
     */
    private String wageCode;
    /**
     * 结果类型
     * 0 字符串 1浮点数字 2比例 3整数
     */
    private String dataType;
    /**
     * 结果
     */
    private Object resultVal;
    /**
     * 字段
     */
    private String tableColName;
}
