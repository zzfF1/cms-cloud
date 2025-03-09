package com.sinosoft.common.mybatis.core.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * Entity基类
 *
 * @author zzf
 */

@Data
public class BackEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 备份类型
     */
    private String edortype;
    /**
    /**
     * 备份人
     */
    private String lastoperator;
    /**
     * 备份时间
     */
    private Date lastmakedatetime;
}
