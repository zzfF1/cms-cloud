package com.sinosoft.common.utils;

import java.util.regex.Pattern;

/**
 * @program: cms6
 * @description: 数字类型校验
 * @author: zzf
 * @create: 2023-07-03 17:42
 */
public class NumUtil {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^(-?\\d+)$");
    private static final Pattern TWO_DECIMAL_PATTERN = Pattern.compile("^(-?([0-9]+)([.]([0-9]{0,2}))?)$");
    private static final Pattern FOUR_DECIMAL_PATTERN = Pattern.compile("^(-?([0-9]+)([.]([0-9]{0,4}))?)$");

    public static boolean isNumber(String str, int nType) {
        try {
            Pattern pattern;
            if (nType == 0) {
                pattern = INTEGER_PATTERN;
            } else if (nType == 1) {
                pattern = TWO_DECIMAL_PATTERN;
            } else if (nType == 2) {
                pattern = FOUR_DECIMAL_PATTERN;
            } else {
                return false; // 无效的 nType 值
            }
            return pattern.matcher(str).matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
