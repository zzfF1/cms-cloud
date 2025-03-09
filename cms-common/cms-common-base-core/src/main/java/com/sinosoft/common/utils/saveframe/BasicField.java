package com.sinosoft.common.utils.saveframe;

import com.sinosoft.common.enums.BasicFieldTypeEnum;

/**
 * @program: cms6
 * @description: 基础字段
 * @author: zzf
 * @create: 2023-07-03 17:45
 */
public class BasicField {
    /**
     * 列索引 默认-1 不读取excel结果
     * Excel校验使用
     */
    public int mColIndex = -1;
    /**
     * 字段代码
     * 实体类字段(忽略大小写)
     */
    public String mField = "";
    /**
     * 数据库字段
     */
    public String mSqlField = "";
    /**
     * 字段说明
     */
    public String mTitle = "";
    /**
     * 类型
     */
    public BasicFieldTypeEnum mBasicFieldTypeEnum;
    /**
     * 是否必录
     */
    public boolean mBust = false;
    /**
     * 是否唯一
     * 多个字段组合成联合唯一
     */
    public boolean mOnly = false;
    /**
     * 数据校验接口
     */
    IBasicFieldCheckVal mFieldCheckVal;
    /**
     * 默认值
     */
    public String mDefVal = "";
    /**
     * 分割符
     * 分割后取第一个索引位结果
     */
    public String mSplit = "";
    /**
     * 最大输入长度
     */
    public int maxInputLength = 0;
    /**
     * 是否特殊字段 代表不是schema的字段 默认true
     * 批量增员时 资格证需要根据证件类型/资格证号/业务员姓名匹配 这三个字段不是资格证表字段
     * true代表是schema字段 会自动把结果映射到schema false则不会
     */
    public boolean mSpecial = true;
}
