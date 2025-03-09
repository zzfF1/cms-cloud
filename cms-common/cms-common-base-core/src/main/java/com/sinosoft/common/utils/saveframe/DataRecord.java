package com.sinosoft.common.utils.saveframe;

import com.sinosoft.common.core.exception.base.CErrors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: cms6
 * @description: 数据记录
 * @author: zzf
 * @create: 2023-07-03 17:49
 */
public class DataRecord {
    /**
     * 行号
     */
    public int nRindex;
    /**
     * 错误类
     */
    public CErrors errorMsg = new CErrors();
    /**
     * 数据
     */
    public Map<String, String> dataMap = new LinkedHashMap<>();
}
