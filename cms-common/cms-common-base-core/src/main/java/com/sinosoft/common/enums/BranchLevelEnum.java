package com.sinosoft.common.enums;

/**
 * 销售机构级别
 */
public enum BranchLevelEnum {

	/**
	 * 个险渠道 组
	 */
	IND01(BranchTypeEnum.IND, "01", "销售组", 3, "%s组", 12) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.IND02;
		}
	},
	/**
	 * 个险渠道 部
	 */
	IND02(BranchTypeEnum.IND, "02", "销售部", 2, "%s部", 8) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.IND01;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.IND03;
		}
	},
	/**
	 * 个险渠道 区
	 */
	IND03(BranchTypeEnum.IND, "03", "销售区", 1, "%s区", 4) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.IND02;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	},
	/**
	 * 银保渠道 部
	 */
	YB02(BranchTypeEnum.YB, "02", "销售部", 2, "%s部", 8) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.YB03;
		}
	},
	/**
	 * 银保渠道区
	 */
	YB03(BranchTypeEnum.YB, "03", "销售区", 1, "%s区", 4) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.YB02;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	},
	/**
	 * 续期渠道区
	 */
	XQ03(BranchTypeEnum.XQ, "03", "销售区", 1, "%s区", 4) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	},
	/**
	 * 团险渠道 部
	 */
	GRP02(BranchTypeEnum.GRP, "12", "销售部", 1, "%s部", 4) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	},
	/**
	 * 中介渠道 部
	 */
	JD02(BranchTypeEnum.JD, "02", "销售部", 2, "%s部", 8) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.JD03;
		}
	},
	/**
	 * 中介渠道 区
	 */
	JD03(BranchTypeEnum.JD, "03", "销售区", 1, "%s区", 4) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.JD02;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	},
	UNKNOWN(BranchTypeEnum.UNKNOWN, "", "未匹配到", 1, "", 0) {
		@Override
		public BranchLevelEnum getLowerLevel() {
			return BranchLevelEnum.UNKNOWN;
		}

		@Override
		public BranchLevelEnum getSuperiorLevel() {
			return BranchLevelEnum.UNKNOWN;
		}
	};

	/**
	 * 渠道标识
	 */
	private BranchTypeEnum branchType;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 说明
	 */
	private String remark;
	/**
	 * 是否根节点
	 */
	private int level;
	/**
	 * 机构名称格式
	 */
	private String branchNameFormat;
	/**
	 * 机构外部编码长度
	 */
	private int nNum;

	/**
	 * 下一个级别
	 *
	 * @return
	 */
	public abstract BranchLevelEnum getLowerLevel();

	/**
	 * 获取上一个级别
	 *
	 * @return
	 */
	public abstract BranchLevelEnum getSuperiorLevel();

	/**
	 * 销售机构级别枚举
	 *
	 * @param code             编号
	 * @param remark           说明
	 * @param level            级别
	 * @param branchNameFormat 机构名称格式化方式
	 * @param nNum             机构外部编码长度
	 */
	BranchLevelEnum(BranchTypeEnum branchTypeEnum, String code, String remark, int level, String branchNameFormat, int nNum) {
		this.branchType = branchTypeEnum;
		this.code = code;
		this.remark = remark;
		this.level = level;
		this.branchNameFormat = branchNameFormat;
		this.nNum = nNum;
	}

	public String getCode() {
		return this.code;
	}

	/**
	 * 获取是否根节点
	 *
	 * @return true根节点 false否
	 */
	public boolean isRoot() {
		return this.level == 1;
	}

	/**
	 * 获取级别
	 *
	 * @return
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * 获取渠道标志
	 *
	 * @return
	 */
	public BranchTypeEnum getBranchType() {
		return this.branchType;
	}

	/**
	 * 返回长度
	 *
	 * @return
	 */
	public int getNum() {
		return this.nNum;
	}

	/**
	 * 获取名称格式化参数
	 *
	 * @return
	 */
	public String getBranchNameFormat() {
		return this.branchNameFormat;
	}

	/**
	 * 机构说明
	 *
	 * @return
	 */
	public String getRemark() {
		return this.remark;
	}

	/**
	 * 初始化编号
	 *
	 * @param upBH 上级编号
	 * @return 当前编号
	 */
	public String initBh(String upBH) {
		return (upBH + String.format("%0" + (this.getNum() - upBH.length()) + "d", 0));
	}

	/**
	 * 获取枚举对象
	 *
	 * @param branchType  渠道
	 * @param branchLevel 级别
	 * @return
	 */
	public static BranchLevelEnum getEnumType(String branchType, String branchLevel) {
		BranchLevelEnum[] branchLevelEnums = BranchLevelEnum.values();
		//循环
		for (BranchLevelEnum um : branchLevelEnums) {
			if (um.branchType.getCode().equals(branchType)) {
				if (um.getCode().equals(branchLevel)) {
					return um;
				}
			}
		}
		return BranchLevelEnum.UNKNOWN;
	}
}
