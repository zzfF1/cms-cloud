package com.sinosoft.common.enums;

/**
 * 渠道类型
 */
public enum BranchTypeEnum {

	/**
	 * 个险渠道
	 */
	IND("1", "个险"),
	GRP("2", "团险"),
	YB("3", "银保"),
	DS("5", "电商"),
	JD("6", "中介"),
	CX("7", "创新"),
	DEW("8", "达尔文"),
	XQ("9", "续期"),
	UNKNOWN("","无渠道");

	private String code;
	private String name;

	BranchTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}
	public String getName() {
		return this.name;
	}

	/**
	 * 获取渠道
	 *
	 * @param branchType
	 * @return 未找到默认 IND
	 */
	public static BranchTypeEnum getEnumType(String branchType) {
		BranchTypeEnum[] enums = BranchTypeEnum.values();
		for (BranchTypeEnum um : enums) {
			if (um.getCode().equals(branchType)) {
				return um;
			}
		}
		return BranchTypeEnum.IND;
	}
}
