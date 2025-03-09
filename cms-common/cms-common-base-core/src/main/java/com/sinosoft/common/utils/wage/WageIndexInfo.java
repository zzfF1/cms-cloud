package com.sinosoft.common.utils.wage;

import cn.hutool.core.util.IdUtil;
import com.sinosoft.common.schema.commission.domain.LaWageIndexInfo;
import lombok.Data;
import com.sinosoft.common.core.utils.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 佣金计算过程
 * @author: zzf
 * @create: 2023-07-19 13:45
 */
@Data
public class WageIndexInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7120158986419817832L;

    /**
     * 结果最大列数
     */
    public static final int COLUMN_MAX = 5;

    /**
     * 业务员
     */
    String agentCode;
    /**
     * 佣金年月
     */
    String wageNo;
    /**
     * 计算元素值 WageCalElementsEnum枚举
     */
    String calElements;
    /**
     * 流水ID
     */
    String key;
    /**
     * 索引位
     */
    int index;
    /**
     * 结果一
     */
    String n1;
    /**
     * 结果一
     */
    String n2;
    /**
     * 结果一
     */
    String n3;
    /**
     * 结果一
     */
    String n4;
    /**
     * 结果一
     */
    String n5;

    /**
     * 批量设置结果
     *
     * @param strVals 结果数组
     */
    public void setValues(String[] strVals) {
        if (strVals != null && strVals.length > 0) {
            this.setN1(setV(strVals[0]));
            if (strVals.length >= 2) {
                this.setN2(setV(strVals[1]));
            }
            if (strVals.length >= 3) {
                this.setN3(setV(strVals[2]));
            }
            if (strVals.length >= 4) {
                this.setN4(setV(strVals[3]));
            }
            if (strVals.length >= 5) {
                this.setN5(setV(strVals[4]));
            }
        }
    }

    /**
     * 转换String
     *
     * @param strVal
     * @return
     */
    public String setV(String strVal) {
        if (StringUtils.isBlank(strVal)) {
            return "";
        }
        return strVal;
    }

    /**
     * 计算过程结果获取结果
     *
     * @param infoSchema
     * @return
     */
    public static String[] getLawageInfoValues(LaWageIndexInfo infoSchema) {
        String[] strValues = new String[5];
        for (int i = 0; i < strValues.length; i++) {
            String strV = null;
            switch (i) {
                case 0:
                    strV = infoSchema.getN1();
                    break;
                case 1:
                    strV = infoSchema.getN2();
                    break;
                case 2:
                    strV = infoSchema.getN3();
                    break;
                case 3:
                    strV = infoSchema.getN4();
                    break;
                case 4:
                    strV = infoSchema.getN5();
                    break;
                default:
                    break;
            }
            strValues[i] = strV;
        }
        return strValues;
    }

    /**
     * 转换为计算过程结果
     *
     * @param indexCalType 计算类型
     * @return 对象
     */
    public LaWageIndexInfo convertToLaWageIndexInfo(String indexCalType) {
        LaWageIndexInfo infoSchema = new LaWageIndexInfo();
        infoSchema.setAgentCode(this.getAgentCode());
        infoSchema.setIndexCalNo(this.getWageNo());
        infoSchema.setCalElements(this.getCalElements());
        infoSchema.setIndexCalType(indexCalType);
        infoSchema.setCalId(Long.parseLong(this.getKey()));
        infoSchema.setRowIndex(this.getIndex());
        infoSchema.setN1(this.getN1());
        infoSchema.setN2(this.getN2());
        infoSchema.setN3(this.getN3());
        infoSchema.setN4(this.getN4());
        infoSchema.setN5(this.getN5());
        return infoSchema;
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.fastSimpleUUID());
    }
}
