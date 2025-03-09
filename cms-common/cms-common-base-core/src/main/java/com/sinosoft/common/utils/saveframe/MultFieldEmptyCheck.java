package com.sinosoft.common.utils.saveframe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: 多字段空校验
 * @author: zzf
 * @create: 2023-07-03 17:54
 */
public class MultFieldEmptyCheck {
    /**
     * 要校验的字段
     */
    private Map<String, String> mFieldMap = new LinkedHashMap<>();

    /**
     * 添加校验字段
     *
     * @param fields 字段
     */
    public void addCheckField(String... fields) {
        for (String field : fields) {
            mFieldMap.put(field, "");
        }
    }

    /**
     * 获取要校验的字段
     *
     * @return 字段列表
     */
    public List<String> getCheckField() {
        List<String> fieldList = new ArrayList<>();
        for (String field : mFieldMap.keySet()) {
            fieldList.add(field);
        }
        return fieldList;
    }
}
