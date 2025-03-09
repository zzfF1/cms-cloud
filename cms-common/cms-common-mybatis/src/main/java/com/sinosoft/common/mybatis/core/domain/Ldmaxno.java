package com.sinosoft.common.mybatis.core.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: ncms
 * @description: 流水号表
 * @author: zzf
 * @create: 2023-06-14 21:25
 */
@Data
@NoArgsConstructor
@TableName("ldmaxno")
public class Ldmaxno {
    private static final long serialVersionUID = 1L;
    @TableId
    private String notype;
    private String nolimit;
    private int maxno;
}
