package com.sinosoft.common.enums;

import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.utils.NumUtil;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 基础字段类型枚举
 */
public enum BasicFieldTypeEnum {
    /**
     * 字符类型枚举
     */
    INT("整数类型", "100") {
        @Override
        public boolean checkVal(String strVal) {
            return NumUtil.isNumber(strVal, 0);
        }

        @Override
        public Object convertVal(Object oVal) {
            String resVal = String.valueOf(oVal);
            try {
                int nVal = Integer.parseInt(resVal);
                resVal = String.valueOf((int) nVal);
            } catch (NumberFormatException e) {
                //分割获取小数
                String[] num = resVal.split("\\.");
                //等于2
                if (num.length == 2) {
                    //获取小数位结果
                    BigDecimal bVal = new BigDecimal("0." + num[1]);
                    //如果小于就只取整数部分 否则返回原结果代表有小数结果
                    if (bVal.doubleValue() < 0.000001) {
                        resVal = num[0];
                    }
                }
            }
            return resVal;
        }
    },
    DOUBLE2("浮点类型(2位小数)", "1.00") {
        @Override
        public boolean checkVal(String strVal) {
            return NumUtil.isNumber(strVal, 1);
        }

        @Override
        public Object convertVal(Object oVal) {
            if (StringUtils.isBlank(String.valueOf(oVal)) || "null".equals(String.valueOf(oVal))) {
                return "0.00";
            } else {
                String resVal = String.valueOf(oVal);
                BigDecimal bVal = new BigDecimal(resVal);
                //分割获取小数
                String[] num = resVal.split("\\.");
                //等于2
                if (num.length == 2) {
                    BigDecimal bDecimal = new BigDecimal("0." + num[1]);
                    //大于千分位
                    if (bDecimal.doubleValue() >= 0.001) {
                        return resVal;
                    }
                }
                return bVal.setScale(2, BigDecimal.ROUND_DOWN);
            }
        }
    },
    DOUBLE4("浮点类型(4位小数)", "1.0000") {
        @Override
        public boolean checkVal(String strVal) {
            return NumUtil.isNumber(strVal, 2);
        }

        @Override
        public Object convertVal(Object oVal) {
            if (StringUtils.isBlank(String.valueOf(oVal)) || "null".equals(String.valueOf(oVal))) {
                return "0.0000";
            } else {
                String resVal = String.valueOf(oVal);
                BigDecimal bVal = new BigDecimal(resVal);
                //分割获取小数
                String[] num = resVal.split("\\.");
                //等于2
                if (num.length == 2) {
                    BigDecimal bDecimal = new BigDecimal("0." + num[1]);
                    //如果大于返回原值
                    if (bDecimal.doubleValue() >= 0.00001) {
                        return resVal;
                    }
                }
                //保留4位小数 截取
                return bVal.setScale(4, BigDecimal.ROUND_DOWN);
            }
        }
    },
    STRING("字符串", "") {
        @Override
        public Object convertVal(Object oVal) {
            if (StringUtils.isBlank(String.valueOf(oVal)) || "null".equals(String.valueOf(oVal))) {
                return "";
            }
            return oVal;
        }
    },
    DATE1("日期类型(YYYY-MM-DD)", "2020-01-01") {
        @Override
        public boolean checkVal(String strVal) {
            return isDate(strVal, "yyyy-MM-dd");
        }
    },
    DATE2("日期类型(YYYYMMDD)", "20200101") {
        @Override
        public boolean checkVal(String strVal) {
            return isDate(strVal, "yyyyMMdd");
        }
    },
    DATE3("日期类型(YYYYMM)", "202001") {
        @Override
        public boolean checkVal(String strVal) {
            String regex = "^\\d{4}(0[1-9]{1}|1[0-2]{1})$";
            Pattern pattern = Pattern.compile(regex);
            boolean isMatch = pattern.matcher(strVal).matches();
            return isMatch;
        }
    },
    TIME1("时间类型(HH:mm:ss)", "18:30:00") {
        @Override
        public boolean checkVal(String strVal) {
            return isDate(strVal, "HH:mm:ss");
        }
    };

    public static void main(String[] args) {
        boolean b = BasicFieldTypeEnum.DATE1.checkVal("2022-07-1");
        System.out.println(b);
    }

    /**
     * 结果检查
     *
     * @return
     */
    public boolean checkVal(String strVal) {
        return true;
    }

    /**
     * 转换结果
     *
     * @param oVal
     * @return
     */
    public Object convertVal(Object oVal) {
        return oVal;
    }

    /**
     * 获取备注
     *
     * @return
     */
    public String getCorrect() {
        return this.correct;
    }

    /**
     * 类型说明
     */
    String remark;
    /**
     * 正确例子
     */
    String correct;


    BasicFieldTypeEnum(String remark, String correct) {
        this.remark = remark;
        this.correct = correct;
    }

    /**
     * 校验是否为日期
     *
     * @param value
     * @param format
     * @return
     */
    public static boolean isDate(String value, String format) {
        if (Objects.isNull(value) || Objects.isNull(format) || value.isEmpty() || format.isEmpty()) {
            return false;
        }
        if (!Objects.equals(format.length(), value.length())) {
            return false;
        }
        try {
            DateUtils.parseDate(value, format);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static BasicFieldTypeEnum getEnum(String code) {
        if ("1".equals(code)) {
            return BasicFieldTypeEnum.INT;
        } else if ("2".equals(code)) {
            return BasicFieldTypeEnum.DOUBLE2;
        } else if ("3".equals(code)) {
            return BasicFieldTypeEnum.DOUBLE4;
        } else if ("4".equals(code)) {
            return BasicFieldTypeEnum.DATE1;
        }
        return BasicFieldTypeEnum.STRING;
    }

}
