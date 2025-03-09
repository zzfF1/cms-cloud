package com.sinosoft.common.utils.saveframe;

import com.sinosoft.common.enums.BasicFieldTypeEnum;
import com.sinosoft.common.enums.RangeCheckTypeEnum;

import java.util.*;

/**
 * @program: cms6
 * @description:
 * @author: zzf
 * @create: 2023-07-03 17:53
 */
public class DuplicateCheck {
    /**
     * 要校验的字段
     */
    private Map<String, String> mFieldMap = new LinkedHashMap<>();
    /**
     * 范围校验
     */
    private Map<String, RangeCheckField> m_RangeFieldMap = new HashMap<>();

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
     * 添加范围
     *
     * @param rangeCheckTypeEnum 范围类型
     * @param fieldTypeEnum      字段类型
     * @param beginField         开始字段
     * @param endField           结束字段
     */
    public void addRangeRule(RangeCheckTypeEnum rangeCheckTypeEnum, BasicFieldTypeEnum fieldTypeEnum, String beginField, String endField) {
        //范围字段
        RangeCheckField rangeCheckField = new RangeCheckField();
        rangeCheckField.setRangeStartField(beginField);
        rangeCheckField.setRangeEndField(endField);
        rangeCheckField.setRangeCheckTypeEnum(rangeCheckTypeEnum);
        rangeCheckField.setBasicFieldTypeEnum(fieldTypeEnum);
        m_RangeFieldMap.put(beginField + "#" + endField, rangeCheckField);
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

    /**
     * 是否有范围校验
     *
     * @return
     */
    public boolean isHaveRangeField() {
        return m_RangeFieldMap.size() > 0;
    }

    /**
     * 获取范围字段
     *
     * @return
     */
    public Map<String, RangeCheckField> getRangeFieldMap() {
        return this.m_RangeFieldMap;
    }
}
