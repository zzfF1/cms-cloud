package com.sinosoft.common.utils.wage;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 佣金过程对象
 * @author: zzf
 * @create: 2023-07-19 15:47
 */
@Data
public class WageCalElements implements Serializable {
    @Serial
    private static final long serialVersionUID = 5950035510818663879L;
    /**
     * 过程ID
     */
    String calElementsCode;
    /**
     * 过程元素个数
     */
    int elementCount;
}
