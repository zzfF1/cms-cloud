package com.sinosoft.common.enums;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 范围校验类型枚举
 */
public enum RangeCheckTypeEnum {

    /**
     * 范围校验类型
     */
    TYPE01(1, "含头含尾"),
    TYPE02(2, "含头不含尾"),
    TYPE03(3, "不含头含尾"),
    TYPE04(4, "不含头不含尾");

    public int type;
    public String remark;

    RangeCheckTypeEnum(int type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    /**
     * 校验范围合法性
     *
     * @param basicFieldTypeEnum 字段类型
     * @param range10            当前行范围开始
     * @param range11            当前行范围结束
     * @param range20            下行范围开始
     * @param range21            下行范围结束
     *                           当前行起期 小于等于 下一行止期
     *                           下一行起期 小于等于 当前行止期
     * @return true在范围内 false不在范围内
     */
    public boolean checkVal(BasicFieldTypeEnum basicFieldTypeEnum, Object range10, Object range11, Object range20, Object range21) {
        //如果是整数类型
        if (basicFieldTypeEnum == BasicFieldTypeEnum.INT) {
            int rCurBegin = Integer.parseInt(String.valueOf(range10));
            int rCurEnd = Integer.parseInt(String.valueOf(range11));
            int rNextBegin = Integer.parseInt(String.valueOf(range20));
            int rNextEnd = Integer.parseInt(String.valueOf(range21));
            //如果是头尾都包含
            if (this == RangeCheckTypeEnum.TYPE01) {
                return rCurBegin <= rNextEnd && rNextBegin <= rCurEnd;
            } else {
                //其他都是不包含
                return rCurBegin < rNextEnd && rNextBegin < rCurEnd;
            }
        } else if (basicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE2 || basicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE4) {
            //如果是浮点类型数据
            double rCurBegin = Double.parseDouble(String.valueOf(range10));
            double rCurEnd = Double.parseDouble(String.valueOf(range11));
            double rNextBegin = Double.parseDouble(String.valueOf(range20));
            double rNextEnd = Double.parseDouble(String.valueOf(range21));
            //如果是头尾都包含
            if (this == RangeCheckTypeEnum.TYPE01) {
                return rCurBegin <= rNextEnd && rNextBegin <= rCurEnd;
            } else {
                //其他都是不包含
                return rCurBegin < rNextEnd && rNextBegin < rCurEnd;
            }
        } else if (basicFieldTypeEnum == BasicFieldTypeEnum.DATE1 || basicFieldTypeEnum == BasicFieldTypeEnum.DATE2) {
            //如果是浮点类型数据
            Date rCurBegin = DateUtil.parse(String.valueOf(range10));
            Date rCurEnd = DateUtil.parse(String.valueOf(range11));
            Date rNextBegin = DateUtil.parse(String.valueOf(range20));
            Date rNextEnd = DateUtil.parse(String.valueOf(range21));
            //如果是头尾都包含
            if (this == RangeCheckTypeEnum.TYPE01) {
                return rCurBegin.getTime() <= rNextEnd.getTime() && rNextBegin.getTime() <= rCurEnd.getTime();
            } else {
                //其他都是不包含
                return rCurBegin.getTime() < rNextEnd.getTime() && rNextBegin.getTime() < rCurEnd.getTime();
            }
        }
        return false;
    }

    /**
     * 日期小于校验
     *
     * @param strD1 起期
     * @param strD2 止期
     * @return true小于 false正常
     */
    public static boolean dateLessComparse(String strD1, String strD2) {
        //补日期
        if (strD1.length() <= 6) {
            strD1 += "01";
        }
        if (strD2.length() <= 6) {
            strD2 += "01";
        }
        Date d1 = DateUtil.parse(strD1);
        Date d2 = DateUtil.parse(strD2);
        if (d1.getTime() > d2.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 校验范围合法性
     *
     * @param basicFieldTypeEnum 字段类型
     * @param startField         范围开始
     * @param endField           范围结束
     * @param oBval              范围起结果
     * @param oEval              范围止结果
     * @param queryWrapper       查询条件
     * @return true在范围内 false不在范围内
     */
    public boolean checkValSql(BasicFieldTypeEnum basicFieldTypeEnum, String startField, String endField, Object oBval, Object oEval, QueryWrapper<Object> queryWrapper) {
        Object oV, oEV;
        //如果是整数类型
        if (basicFieldTypeEnum == BasicFieldTypeEnum.INT) {
            oV = Integer.parseInt(String.valueOf(oBval));
            oEV = Integer.parseInt(String.valueOf(oEval));
        } else if (basicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE2 || basicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE4) {
            //如果是浮点类型数据
            oV = new BigDecimal(String.valueOf(oBval));
            oEV = new BigDecimal(String.valueOf(oEval));
        } else if (basicFieldTypeEnum == BasicFieldTypeEnum.DATE1 || basicFieldTypeEnum == BasicFieldTypeEnum.DATE2) {
            oV = DateUtil.parse(String.valueOf(String.valueOf(oBval)));
            oEV = DateUtil.parse(String.valueOf(String.valueOf(oEval)));
        } else if (basicFieldTypeEnum == BasicFieldTypeEnum.DATE3) {
            oV = String.valueOf(oBval);
            oEV = String.valueOf(oEval);
        } else {
            return false;
        }
        //如果是头尾都包含
        if (this == RangeCheckTypeEnum.TYPE01) {
            queryWrapper.and(i -> i.ge(endField, oV).le(startField, oEV));
        } else {
            //头尾都不含
            queryWrapper.and(i -> i.gt(endField, oV).lt(startField, oEV));
        }
        return true;
    }
}
