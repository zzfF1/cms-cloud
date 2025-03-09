package com.sinosoft.common.enums;

/**
 * 考核数据范围枚举
 * laassessconfig.DataPeriod字段
 */
public enum AssessDataPeriodEnum {

	TYP01("1", "月度", 1),
	/**
	 * 例
	 * 3月 1,2,3
	 * 4月 2,3,4
	 */
	QUARTER_ROLL("2", "季度(向前滚动)", 3),
	/**
	 * 例
	 * 3月 1,2,3
	 * 4月 4
	 * 5月 4,5
	 */
	QUARTER_NORMAL("3", "季度(截至当前月季度)", 3),
	/**
	 * 1月 1,2,3
	 * 2月 1,2,3
	 */
	QUARTER_ALL("4", "季度(整个季度)", 3),

	/**
	 * 6月 1,2,3,4,5,6
	 * 7月 2,3,4,5,6,7
	 */
	HALF_YEAR_ROOL("A2", "半年(向前滚动)", 6),
	/**
	 * 6月 1,2,3,4,5,6
	 * 7月 7
	 * 8月 7,8
	 */
	HALF_YEAR_NORMAL("A3", "半年(截至当前月半年)", 6),
	/**
	 * 6月 1,2,3,4,5,6
	 * 7月 7,8,9,10,11,12
	 * 8月 7,8,9,10,11,12
	 */
	HALF_YEAR_ALL("A4", "半年(整个半年)", 6),

	/**
	 * 12月 1,2,3,4,5,6,7,8,9,10,11,12
	 * 1月 2,3,4,5,6,7,8,9,10,11,12,1
	 */
	YEAR_ROLL("B2", "年度(向前滚动)", 12),
	/**
	 * 6月 1,2,3,4,5,6
	 * 7月 1,2,3,4,5,6,7
	 */
	YEAR_NORMAL("B3", "年度(截至当前月年度)", 12),
	/**
	 * 6月 1,2,3,4,5,6,7,8,9,10,11,12
	 * 7月 1,2,3,4,5,6,7,8,9,10,11,12
	 */
	YEAR_ALL("B4", "年度(整个年度)", 12),

	UNKNOWN("unknown", "没有匹配", 1);


	/**
	 * 代码
	 */
	private String code;
	/**
	 * 名称
	 */
	private String name;
	/**
	 *
	 */
	private int diff;

	AssessDataPeriodEnum(String code, String name, int nDiff) {
		this.code = code;
		this.name = name;
		this.diff = nDiff;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 获取考核周期
	 *
	 * @return
	 */
	public int getDiff() {
		return this.diff;
	}

	/**
	 * 根据type匹配枚举
	 *
	 * @param type
	 * @return
	 */
	public static AssessDataPeriodEnum getEnumType(String type) {
		AssessDataPeriodEnum[] assessPeriod = AssessDataPeriodEnum.values();
		for (AssessDataPeriodEnum assessPer : assessPeriod) {
			if (assessPer.getCode().equals(type)) {
				return assessPer;
			}
		}
		return AssessDataPeriodEnum.UNKNOWN;
	}
}
