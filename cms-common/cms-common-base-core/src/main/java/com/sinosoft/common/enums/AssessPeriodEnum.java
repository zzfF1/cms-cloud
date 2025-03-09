package com.sinosoft.common.enums;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.schema.common.domain.LaAgentAssess;
import com.sinosoft.common.schema.common.domain.LaAssessConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 考核周期定义枚举
 * laassessconfig.AssessPeriod字段
 */
public enum AssessPeriodEnum {
    /**
     * TYPE01,TYPE03,TYPE06,TYPE012 次月考核期
     * TYPE13,TYPE16,TYPE112 本月考核期
     */
    TYPE01("1", "月度", (new HashMap<String, String>() {{
        put("1", "");
    }})),
    TYPE03("3", "季度", (new HashMap<String, String>() {{
        put("1", "");
        put("4", "");
        put("7", "");
        put("10", "");
    }})),
    TYPE06("6", "半年", (new HashMap<String, String>() {{
        put("1", "");
        put("7", "");
    }})),
    TYPE012("12", "年度", (new HashMap<String, String>() {{
        put("1", "");
    }})),
    /**
     * 当月考核月
     */
    TYPE13("02", "季度", (new HashMap<String, String>() {{
        put("3", "");
        put("6", "");
        put("9", "");
        put("12", "");
    }})),
    TYPE16("05", "半年", (new HashMap<String, String>() {{
        put("6", "");
        put("12", "");
    }})),
    TYPE112("011", "年度", (new HashMap<String, String>() {{
        put("12", "");
    }})),
    TYPE020("20", "特殊考核周期", null), UNKNOWN("unknown", "没有匹配", null);

    private String code;
    private String name;
    /**
     * 考核周期
     */
    private Map<String, String> mapAssessMonth = null;

    AssessPeriodEnum(String code, String name, Map<String, String> assessMonth) {
        this.code = code;
        this.name = name;
        this.mapAssessMonth = assessMonth;
    }

    public String getCode() {
        return code;
    }

    /**
     * @param month
     * @return true计算 false不计算
     */
    public boolean getMonthCal(String month) {
        //对象不为空
        if (mapAssessMonth != null) {
            //存在
            if (mapAssessMonth.containsKey(month)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据type匹配枚举
     *
     * @param type
     * @return
     */
    public static AssessPeriodEnum getEnumType(String type) {
        AssessPeriodEnum[] assessPeriod = AssessPeriodEnum.values();
        for (AssessPeriodEnum assessPer : assessPeriod) {
            if (assessPer.getCode().equals(type)) {
                return assessPer;
            }
        }
        return AssessPeriodEnum.UNKNOWN;
    }

    /**
     * 判断考核年月是否计算
     *
     * @param periodEnum 考核周期枚举
     * @param assessDate 考核年月
     * @param strParm    考核参数
     * @return true计算 false不计算
     */
    public static boolean periodIsCal(AssessPeriodEnum periodEnum, String assessDate, String strParm) {
        //如果是没有匹配数据
        if (AssessPeriodEnum.UNKNOWN == periodEnum) {
            return false;
        }
        //检查考核周期是否为空
        if (StringUtils.isBlank(assessDate)) {
            return false;
        }
        //判断考核日期是否合法
        if (assessDate.length() < 6) {
            return false;
        }
        //如果是月度考核
        if (AssessPeriodEnum.TYPE01 == periodEnum) {
            return true;
        }
        boolean bCal = false;
        //获取月份
        String month = assessDate.substring(4, 6);
        int nMonth = Integer.parseInt(month);
        //转换为字符串
        month = String.valueOf(nMonth);
        //如果是特殊周期
        if (AssessPeriodEnum.TYPE020 == periodEnum) {
            //参数不为空
            if (!StringUtils.isBlank(strParm)) {
                String parms[] = strParm.split(",");
                for (int i = 0; i < parms.length; i++) {
                    if (month.equals(parms[i])) {
                        bCal = true;
                        break;
                    }
                }
            }
        }
        //如果是正常考核
        switch (periodEnum) {
            case TYPE03:
            case TYPE06:
            case TYPE012:
                bCal = periodEnum.getMonthCal(month);
                break;
            default:
                break;
        }
        return bCal;
    }

    /**
     * 计算考核起期
     *
     * @param laAgentAssessSchema 考核人对员对象
     * @param configSchema        考核配置对象
     * @param endDate             止期
     * @return 考核数据的起期
     */
    public static String calAssessStart(LaAgentAssess laAgentAssessSchema, LaAssessConfig configSchema, String endDate) {
        //获取数据统计周期
        AssessDataPeriodEnum assessDataPeriodEnum = AssessDataPeriodEnum.getEnumType(configSchema.getDataPeriod());
        //如果没有找到或者是特殊考核周期就返回当前assessstart
        if (assessDataPeriodEnum == AssessDataPeriodEnum.UNKNOWN) {
            return laAgentAssessSchema.getAssessStart();
        }
        //参数
        String perParm = configSchema.getPeriodParm();
        String calType = "";
        //判断是否指定了生效时间模式
        if (perParm.indexOf("A") > -1) {
            //职级生效时间
            calType = "A";
        } else if (perParm.indexOf("B") > -1) {
            //职级序列生效
            calType = "B";
        }
        //考核周起止期
        String strStartDate = "";
        //获取月初
        String strDate = DateUtils.formatDate(DateUtil.beginOfMonth(DateUtils.parseDate(endDate)));
        //当前时间
        DateTime dTime = DateUtil.parse(endDate);
        //计算开始月份
        int nCalStartMonth = 1;
        //获取前推日期
        nCalStartMonth = assessDataPeriodEnum.getDiff();
        switch (assessDataPeriodEnum) {
            case QUARTER_NORMAL: {
                //正常季度
                //季度开始日期
                DateTime formatDate = DateUtil.beginOfQuarter(dTime);
                //相差月份
                long lDiff = DateUtil.betweenMonth(formatDate, dTime, false) + 1;
                nCalStartMonth = (int) lDiff;
                break;
            }
            case HALF_YEAR_NORMAL: {
                //判断是在那个渠道
                int quarter = DateUtil.quarter(dTime);
                //获取年初
                DateTime formatDate = DateUtil.beginOfYear(dTime);
                //下半年
                if (quarter > 2) {
                    //年初偏移6个月
                    formatDate = DateUtil.offsetMonth(formatDate, 6);
                }
                //相差月份
                long lDiff = DateUtil.betweenMonth(formatDate, dTime, false) + 1;
                nCalStartMonth = (int) lDiff;
                break;
            }
            case YEAR_NORMAL: {
                //年度开始日期
                DateTime formatDate = DateUtil.beginOfYear(dTime);
                //相差月份
                long lDiff = DateUtil.betweenMonth(formatDate, dTime, false) + 1;
                nCalStartMonth = (int) lDiff;
                break;
            }
            default:
                break;
        }
        nCalStartMonth--;
        strStartDate = DateUtils.formatDate(DateUtil.offsetMonth(DateUtils.parseDate(strDate), -nCalStartMonth));
        if ("A".equals(calType)) {

        } else if ("B".equals(calType)) {

        }
        //如果入司日期 小于 入司日期
        if (laAgentAssessSchema.getEmployDate().compareTo(DateUtils.parseDate(strStartDate)) > 0) {
            strStartDate = DateUtils.formatDate(laAgentAssessSchema.getEmployDate());
        }
        return strStartDate;
    }

    /**
     * 计算考核止期
     *
     * @param laAgentAssessSchema 考核人员
     * @param configSchema        考核配置
     * @return 考核止期
     */
    public static String calAssessEnd(LaAgentAssess laAgentAssessSchema, LaAssessConfig configSchema) {
        AssessPeriodEnum assessPeriodEnum = AssessPeriodEnum.getEnumType(configSchema.getAssessPeriod());
        AssessDataPeriodEnum assessDataPeriodEnum = AssessDataPeriodEnum.getEnumType(configSchema.getDataPeriod());
        // 如果没有找到或者是特殊考核周期就返回当前assessstart
        if (assessPeriodEnum == AssessPeriodEnum.UNKNOWN || assessDataPeriodEnum == AssessDataPeriodEnum.UNKNOWN) {
            return laAgentAssessSchema.getAssessStart();
        }
        Date lastDate = DateUtil.offsetMonth(DateUtils.parseDate(laAgentAssessSchema.getAssessDate() + "01"), -1);
        String startD = DateUtils.formatDate(DateUtil.beginOfMonth(lastDate));
        String endD;

        switch (assessDataPeriodEnum) {
            case QUARTER_ALL:
                endD = DateUtils.formatDate(DateUtil.endOfQuarter(DateUtils.parseDate(startD)));
                break;
            case HALF_YEAR_ALL:
                int month = DateUtil.month(DateUtils.parseDate(startD)) + 1;
                int offsetMonths = (month <= 6) ? (6 - month) : (12 - month);
                endD = DateUtils.formatDate(DateUtil.endOfMonth(DateUtil.offsetMonth(DateUtils.parseDate(startD), offsetMonths)));
                break;
            case YEAR_ALL:
                endD = DateUtils.formatDate(DateUtil.endOfYear(DateUtils.parseDate(startD)));
                break;
            default:
                endD = DateUtils.formatDate(DateUtil.endOfMonth(DateUtils.parseDate(startD)));
                break;
        }
        return DateUtils.formatDate(DateUtil.endOfMonth(DateUtils.parseDate(endD)));

    }

    public static void main(String[] args) {
        DateTime d1 = DateUtil.parse("2022-04-01");
        DateTime d2 = DateUtil.parse("2022-04-02");
        System.out.println(DateUtil.betweenMonth(d1, d2, false));
        System.out.println(DateUtil.betweenMonth(d1, d2, true));
        //LAAgentAssessDB laAgentAssessDB = new LAAgentAssessDB();
        //laAgentAssessDB.setAgentCode("G0000571");
        //laAgentAssessDB.setAssessDate("202201");
        //laAgentAssessDB.setAssessType("10");
        //laAgentAssessDB.getInfo();
        //
        //LAAssessConfigDB laAssessConfigDB = new LAAssessConfigDB();
        //laAssessConfigDB.setAssessCode("G100012");
        //laAssessConfigDB.getInfo();
        //
        //LAAgentAssessSchema laAgentAssessSchema = laAgentAssessDB.getSchema();
        //LAAssessConfigSchema laAssessConfigSchema = laAssessConfigDB.getSchema();
        //
        //System.out.println(AssessPeriodEnum.calAssessStart(laAgentAssessSchema, laAssessConfigSchema));
        //System.out.println(AssessPeriodEnum.calAssessEnd(laAgentAssessSchema, laAssessConfigSchema));
    }
}
