package com.sinosoft.common.utils.saveframe;

import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.enums.BasicFieldTypeEnum;
import com.sinosoft.common.enums.RangeCheckTypeEnum;

/**
 * @program: cms6
 * @description: 范围校验字段
 * @author: zzf
 * @create: 2023-07-03 17:51
 */
public class RangeCheckField {
    /**
     *
     */
    private RangeCheckTypeEnum rangeCheckTypeEnum;
    /**
     * 范围起期字段
     */
    private String rangeStartField = "";
    /**
     * 范围止期字段
     */
    private String rangeEndField = "";
    /**
     * 字段类型
     */
    private BasicFieldTypeEnum basicFieldTypeEnum;

    public RangeCheckTypeEnum getRangeCheckTypeEnum() {
        return rangeCheckTypeEnum;
    }

    public void setRangeCheckTypeEnum(RangeCheckTypeEnum rangeCheckTypeEnum) {
        this.rangeCheckTypeEnum = rangeCheckTypeEnum;
    }

    public String getRangeStartField() {
        return rangeStartField;
    }

    public void setRangeStartField(String rangeStartField) {
        this.rangeStartField = rangeStartField;
    }

    public String getRangeEndField() {
        return rangeEndField;
    }

    public void setRangeEndField(String rangeEndField) {
        this.rangeEndField = rangeEndField;
    }

    public BasicFieldTypeEnum getBasicFieldTypeEnum() {
        return basicFieldTypeEnum;
    }

    public void setBasicFieldTypeEnum(BasicFieldTypeEnum basicFieldTypeEnum) {
        this.basicFieldTypeEnum = basicFieldTypeEnum;
    }

    /**
     * 校验数据范围合法性
     *
     * @param oCurStartVal 当前行范围开始
     * @param oCurEndVal  当前行范围结束
     * @return true在范围内 false不在范围内
     */
    public boolean checkRange(Object oCurStartVal, Object oCurEndVal) {
        boolean bFlag = true;
        if (oCurStartVal != null && oCurEndVal != null) {
            if (StringUtils.isNotBlank(String.valueOf(oCurStartVal)) && StringUtils.isNotBlank(String.valueOf(oCurEndVal))) {
                switch (this.getBasicFieldTypeEnum()) {
                    case DATE1:
                    case DATE2:
                    case DATE3:
                        if (RangeCheckTypeEnum.dateLessComparse(String.valueOf(oCurStartVal), String.valueOf(oCurEndVal))) {
                            bFlag = false;
                        }
                        break;
                    case INT:
                    case DOUBLE2:
                    case DOUBLE4:
                        if (Double.parseDouble(String.valueOf(oCurEndVal)) < Double.parseDouble(String.valueOf(oCurStartVal))) {
                            bFlag = false;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return bFlag;
    }
}
