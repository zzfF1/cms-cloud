package com.sinosoft.common.utils.saveframe;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.core.enums.FormatsType;
import com.sinosoft.common.core.exception.BatchServiceException;
import com.sinosoft.common.exception.SaveFrameworkException;
import com.sinosoft.common.core.exception.base.CErrors;
import com.sinosoft.common.schema.agent.domain.Laagenttemp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.core.utils.reflect.ReflectUtils;
import com.sinosoft.common.enums.BasicFieldTypeEnum;
import com.sinosoft.common.enums.RangeCheckTypeEnum;
import com.sinosoft.common.mapper.SaveFrameworkMapper;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: cms6
 * @description: 保存框架
 * @author: zzf
 * @create: 2023-07-03 17:26
 */
@Slf4j
public class BasicSaveFramework {

    /**
     * 校验字段
     */
    private Map<String, BasicField> mBasicFields;
    /**
     * 辅助校验字段
     */
    private Map<String, RangeCheckField> m_RangeFieldMap;
    /**
     * 数据结果集
     */
    private Map<Integer, DataRecord> m_DataRecordMap;
    /**
     * 数据校验接口
     */
    private List<IBasicDataRecordCheck> m_DataRecordCheckList;
    /**
     * 重复校验规则定义
     */
    private List<DuplicateCheck> m_DuplicateCheckList;
    /**
     * 定义spel表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    /**
     * 是否检查数据库数据重复 默认false
     */
    private boolean m_CheckDataSqlRepeat = true;
    /**
     * 业务数据表名
     */
    private String m_TableName = "";
    /**
     * 保存框架mapper
     */
    private final SaveFrameworkMapper saveFrameworkMapper;
    /**
     * 开始行
     */
    public int mStartRow = 0;

    public BasicSaveFramework() {
        mBasicFields = new LinkedHashMap<>();
        m_RangeFieldMap = new HashMap<>();
        m_DataRecordMap = new LinkedHashMap<>();
        m_DataRecordCheckList = new ArrayList<>();
        m_DuplicateCheckList = new ArrayList<>();
        //获取mapper
        saveFrameworkMapper = SpringUtils.getBean(SaveFrameworkMapper.class);
    }

    /**
     * 添加字段
     *
     * @param strField      字段(转换转换小写)
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param bMust         是否必录
     * @param bOnly         是否唯一字段
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addField(String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, boolean bMust, boolean bOnly, IBasicFieldCheckVal fieldCheckVal) {
        addExcelField(-1, strField, strTitle, fieldTypeEnum, bMust, bOnly, true, "", "", fieldCheckVal);
    }

    /**
     * 添加字段
     *
     * @param colIndex      列索引
     * @param strField      字段(转换转换小写)
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param split         分割符
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addExcelField(int colIndex, String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, String split, IBasicFieldCheckVal fieldCheckVal) {
        addExcelField(colIndex, strField, strTitle, fieldTypeEnum, false, false, true, "", split, fieldCheckVal);
    }

    /**
     * 添加字段(读excel)使用
     *
     * @param colIndex      列索引
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param bMust         是否必录
     * @param bOnly         是否唯一字段
     * @param defVal        默认值
     * @param split         分割符
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addExcelField(int colIndex, String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, boolean bMust, boolean bOnly, String defVal, String split, IBasicFieldCheckVal fieldCheckVal) {
        addExcelField(colIndex, strField, strTitle, fieldTypeEnum, bMust, bOnly, true, defVal, split, fieldCheckVal);
    }

    /**
     * 添加字段(读excel)使用
     *
     * @param colIndex      列索引
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param bMust         是否必录
     * @param bOnly         是否唯一字段
     * @param defVal        默认值
     * @param split         分割符
     * @param maxLength     最大输入长度
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addExcelField(int colIndex, String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, boolean bMust, boolean bOnly, String defVal, String split, int maxLength, IBasicFieldCheckVal fieldCheckVal) {
        addExcelField(colIndex, strField, strTitle, fieldTypeEnum, bMust, bOnly, true, defVal, split, maxLength, fieldCheckVal);
    }

    /**
     * 添加字段(读excel)使用
     *
     * @param colIndex      列索引
     * @param strField      字段
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param bMust         是否必录
     * @param bOnly         是否唯一字段
     * @param bSpecial      是否为schema字段
     * @param defVal        默认值
     * @param split         分割符
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addExcelField(int colIndex, String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, boolean bMust, boolean bOnly, boolean bSpecial, String defVal, String split, IBasicFieldCheckVal fieldCheckVal) {
        addExcelField(colIndex, strField, strTitle, fieldTypeEnum, bMust, bOnly, bSpecial, defVal, split, 0, fieldCheckVal);
    }

    /**
     * 添加字段(读excel)使用
     *
     * @param colIndex      列索引
     * @param strField      字段
     * @param strTitle      字段显示名
     * @param fieldTypeEnum 类型
     * @param bMust         是否必录
     * @param bOnly         是否唯一字段
     * @param bSpecial      是否为schema字段
     * @param defVal        默认值
     * @param split         分割符
     * @param maxLength     最大输入长度
     * @param fieldCheckVal 数据校验接口实现
     */
    public void addExcelField(int colIndex, String strField, String strTitle, BasicFieldTypeEnum fieldTypeEnum, boolean bMust, boolean bOnly, boolean bSpecial, String defVal, String split, int maxLength, IBasicFieldCheckVal fieldCheckVal) {
        BasicField basicField = new BasicField();
        basicField.mColIndex = colIndex;
        basicField.mField = strField.toLowerCase();
        basicField.mTitle = strTitle;
        basicField.mBasicFieldTypeEnum = fieldTypeEnum;
        basicField.mBust = bMust;
        basicField.mOnly = bOnly;
        basicField.mDefVal = defVal;
        basicField.mSplit = split;
        basicField.mFieldCheckVal = fieldCheckVal;
        basicField.mSpecial = bSpecial;
        basicField.maxInputLength = maxLength;
        mBasicFields.put(strField.toLowerCase(), basicField);
    }

    /**
     * 禁用数据库重复校验
     */
    public void disableDataSqlRepeat() {
        this.m_CheckDataSqlRepeat = false;
    }

    /**
     * 添加范围字段规则
     *
     * @param rangeCheckTypeEnum 校验方式
     * @param beginField         开始字段字段
     * @param endField           结束字段
     * @return true成功 false异常
     */
    public boolean addRangeRule(RangeCheckTypeEnum rangeCheckTypeEnum, String beginField, String endField) {
        //如果是唯一字段
        BasicField startF = this.mBasicFields.get(beginField);
        BasicField endF = this.mBasicFields.get(endField);
        //检查字段是否存在
        if (startF == null) {
            return false;
        }
        //检查字段是否存在
        if (endF == null) {
            return false;
        }
        //如果类型不相等
        if (startF.mBasicFieldTypeEnum != endF.mBasicFieldTypeEnum) {
            return false;
        }
        //范围字段
        RangeCheckField rangeCheckField = new RangeCheckField();
        rangeCheckField.setRangeStartField(beginField);
        rangeCheckField.setRangeEndField(endField);
        rangeCheckField.setRangeCheckTypeEnum(rangeCheckTypeEnum);
        rangeCheckField.setBasicFieldTypeEnum(startF.mBasicFieldTypeEnum);
        m_RangeFieldMap.put(beginField + "#" + endField, rangeCheckField);
        return true;
    }

    /**
     * 添加数据校验实现
     *
     * @param iBasicDataRecordCheck 数据校验实现
     */
    public void addDataRecordCheckRule(IBasicDataRecordCheck iBasicDataRecordCheck) {
        this.m_DataRecordCheckList.add(iBasicDataRecordCheck);
    }

    /**
     * 添加重复校验规则定义
     *
     * @param duplicateCheck 重复校验规则定义
     */
    public void addDuplicateCheckRule(DuplicateCheck duplicateCheck) {
        this.m_DuplicateCheckList.add(duplicateCheck);
    }

    /**
     * 数据新增合法性校验
     *
     * @param beanClass    类名
     * @param dataList     数据
     * @param strTableName 表名
     * @return true成功 false异常
     */
    public boolean checkValByInsert(Class<?> beanClass, List<?> dataList, String strTableName) throws SaveFrameworkException {
        if (beanClass == null) {
            throw new SaveFrameworkException("类名不能为空");
        }
        if (dataList == null) {
            throw new SaveFrameworkException("校验数据不能为空");
        }
        //没有校验的数据返回true
        if (dataList.isEmpty()) {
            return true;
        }
        m_TableName = strTableName;
        //记录数据
        List<DataRecord> dataRecordsList = new ArrayList<>();
        try {
            //获取类字段
            Field[] fields = ReflectUtil.getFields(beanClass);
            //循环获取小写字段值
            Map<String, String> fieldLowerMap = new HashMap<>();
            for (Field field : fields) {
                fieldLowerMap.put(field.getName().toLowerCase(), field.getName());
            }
            //列索引
            int nF = -1;
            //循环列字段
            for (String nField : mBasicFields.keySet()) {
                nF++;
                //获取字段对象
                BasicField basicField = mBasicFields.get(nField);
                //如果不是类字段跳过
                if (!basicField.mSpecial) {
                    continue;
                }
                //如果没有匹配到跳过
                if (!fieldLowerMap.containsKey(nField)) {
                    continue;
                }
                //循环数据
                for (int nRindex = 0; nRindex < dataList.size(); nRindex++) {
                    DataRecord record;
                    //如果是第一列
                    if (nF == 0) {
                        record = new DataRecord();
                        record.nRindex = nRindex;
                    } else {
                        //获取对象
                        record = dataRecordsList.get(nRindex);
                    }
                    //获取对象
                    Object oObjData = dataList.get(nRindex);
                    //获取结果
                    setRecordValue(record, basicField, ReflectUtils.invokeGetter(oObjData, fieldLowerMap.get(nField)));
                    //如果是第一列添加到集合中
                    if (nF == 0) {
                        record.nRindex = nRindex;
                        dataRecordsList.add(record);
                        m_DataRecordMap.put(record.nRindex, record);
                    }
                }
            }
            return checkDataRecordVal(dataRecordsList);
        } catch (Exception e) {
            //如果是批量异常放过 因为有全局异常捕获处理
            if (e instanceof BatchServiceException) {
                throw e;
            } else {
                throw new SaveFrameworkException("解析数据出错,错误信息:" + e.getMessage());
            }
        }
    }

    /**
     * 更新集合数据
     *
     * @param beanClass 类名
     * @param dataList  数据
     */
    public void updateCollectionData(Class<?> beanClass, List<?> dataList) throws SaveFrameworkException {
        //数据为空
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        // 获取类的字段并创建字段映射
        Field[] fields = beanClass.getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            fieldMap.put(field.getName().toLowerCase(), field);
        }
        try {
            //循环数据
            for (int i = 0; i < dataList.size(); i++) {
                //获取数据
                Object oObjData = dataList.get(i);
                DataRecord dataRecord = m_DataRecordMap.get(i);
                //如果匹配到了数据
                if (dataRecord != null) {
                    //循环字段
                    for (String nField : mBasicFields.keySet()) {
                        //获取字段对象
                        BasicField basicField = mBasicFields.get(nField);
                        //如果不是类字段跳过
                        if (!basicField.mSpecial) {
                            continue;
                        }
                        //如果没有匹配到跳过
                        if (!fieldMap.containsKey(nField)) {
                            continue;
                        }
                        extracted(nField, fieldMap, basicField, oObjData, dataRecord);
                    }
                }
            }
        } catch (Exception e) {
            log.error("转换实体类出错{}", e.getMessage());
            throw new SaveFrameworkException("转换实体类出错,错误信息:" + e.getMessage());
        }
    }

    /**
     * 设置值
     *
     * @param record     数据记录
     * @param basicField 字段
     * @param oVal       值
     */
    private void setRecordValue(DataRecord record, BasicField basicField, Object oVal) {
        String strVal = String.valueOf(oVal);
        //如果是日期格式特殊转换一下
        if (oVal instanceof Date) {
            if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DATE1) {
                strVal = DateUtils.formatDate((Date) oVal);
            } else if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DATE2) {
                strVal = DateUtils.parseDateToStr(FormatsType.YYYYMMDD, (Date) oVal);
            }
        }
        //如果结果为空
        if (StringUtils.isBlank(strVal) || "null".equals(strVal)) {
            //检查是否有默认值
            strVal = StringUtils.isNotBlank(basicField.mDefVal) ? basicField.mDefVal : "";
        }
        //如果分割符不为空
        if (StringUtils.isNotBlank(basicField.mSplit)) {
            //分割获取
            String[] strParms = strVal.split(basicField.mSplit);
            if (strParms.length >= 1) {
                strVal = strParms[0];
            }
        }
        try {
            //转换结果
            strVal = String.valueOf(basicField.mBasicFieldTypeEnum.convertVal(strVal));
            //设置结果
            record.dataMap.put(basicField.mField, strVal);
        } catch (Exception e) {
            log.error("转换数据出错,错误信息:" + e.getMessage());
            record.errorMsg.addErrorInfo(basicField.mTitle, strVal, "转换数据出错!");
        }
    }

    /**
     * 校验数据
     *
     * @param dataRecordList 数据集合
     * @return true 没有错误 false存在错误
     */
    private boolean checkDataRecordVal(List<DataRecord> dataRecordList) {
        //是否有重复籽段
        boolean bHaveOnlyField = false;
        List<String> onlyKeyList = new ArrayList<>();
        for (String nField : mBasicFields.keySet()) {
            //获取字段对象
            BasicField basicField = mBasicFields.get(nField);
            //如果是唯一字段
            if (basicField.mOnly) {
                onlyKeyList.add(nField);
            }
        }
        //循环结果集
        for (DataRecord record : dataRecordList) {
            //循环列字段
            for (String nField : mBasicFields.keySet()) {
                //获取字段对象
                BasicField basicField = mBasicFields.get(nField);
                //获取结果
                Object oVal = record.dataMap.get(basicField.mField);
                //校验数据合法性
                verifyFieldValLegal(record, basicField, oVal);
                //如果还没有唯一字段
                if (!bHaveOnlyField) {
                    //如果是唯一字段
                    if (basicField.mOnly) {
                        bHaveOnlyField = true;
                    }
                }
            }
            //如果有范围校验
            if (!m_RangeFieldMap.isEmpty()) {
                for (String assistF : m_RangeFieldMap.keySet()) {
                    //获取范围字段
                    RangeCheckField rangeCheckField = m_RangeFieldMap.get(assistF);
                    //当前行结果
                    Object oCurStartVal = record.dataMap.get(rangeCheckField.getRangeStartField());
                    Object oCurEndVal = record.dataMap.get(rangeCheckField.getRangeEndField());
                    //校验数据范围合法
                    if (!rangeCheckField.checkRange(oCurStartVal, oCurEndVal)) {
                        BasicField startField = mBasicFields.get(rangeCheckField.getRangeStartField());
                        BasicField endField = mBasicFields.get(rangeCheckField.getRangeEndField());
                        record.errorMsg.addErrorInfo(String.format("[%s %s]数据不合法,[%s 应小于等于 %s]!", oCurStartVal, oCurEndVal, startField.mTitle, endField.mTitle));
                    }
                }
            }
        }
        //检查抛出异常
        checkRecordThrowException(dataRecordList);
        //如果有数据校验规则
        if (!m_DataRecordCheckList.isEmpty()) {
            //循环规则
            for (IBasicDataRecordCheck iBasicDataRecordCheck : m_DataRecordCheckList) {
                //调用校验规则
                try {
                    iBasicDataRecordCheck.checkDataRecordErrorMsg(dataRecordList);
                } catch (Exception e) {
                    log.error("数据合法性校验出错,错误信息:{}" , e.getMessage(), e);
                    throw new ServiceException("数据合法性校验出错,错误信息:" + e.getMessage());
                }
            }
        }
        //检查抛出异常
        checkRecordThrowException(dataRecordList);
        //如果有重复字段需要检查数据重复
        if (bHaveOnlyField) {
            verifyValRepeat(dataRecordList, onlyKeyList, this.m_RangeFieldMap);
            //校验数据库是否重复
            verifyValRepeatSql(dataRecordList, onlyKeyList, this.m_RangeFieldMap);
        }
        //循环规则
        for (DuplicateCheck duplicateCheck : m_DuplicateCheckList) {
            //校验数据重复.
            verifyValRepeat(dataRecordList, duplicateCheck.getCheckField(), duplicateCheck.getRangeFieldMap());
            verifyValRepeatSql(dataRecordList, duplicateCheck.getCheckField(), duplicateCheck.getRangeFieldMap());
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            BasicSaveFramework basicSaveFramework = new BasicSaveFramework();
            basicSaveFramework.addExcelField(0, "managecom", "管理机构编码", BasicFieldTypeEnum.STRING, true, false, "", "-", null);
            basicSaveFramework.addExcelField(1, "name", "姓名", BasicFieldTypeEnum.STRING, true, false, "", "", null);
            basicSaveFramework.addExcelField(2, "idnotype", "证件类型", BasicFieldTypeEnum.STRING, true, true, "", "-", null);
            basicSaveFramework.addExcelField(3, "idno", "证件号", BasicFieldTypeEnum.STRING, true, true, "", "", null);
            basicSaveFramework.addExcelField(4, "sex", "性别", BasicFieldTypeEnum.STRING, true, false, "", "-", null);
            if (!basicSaveFramework.readExcelCheckInsert("I:\\入司批量导入模板_1696148498749.xlsx", 0, 2, "la_agent_temp")) {
                System.out.println("错误");

            }

            List<Laagenttemp> dataList = basicSaveFramework.dataRecordToEntity(Laagenttemp.class);
            System.out.println(dataList);

        } catch (Exception e) {
            log.error("错误信息:{}", e.getMessage(), e);
        }
    }

    /**
     * 检查dataRecordList 如果存在异常抛出异常
     *
     * @param dataRecordList 数据集合
     */
    private void checkRecordThrowException(List<DataRecord> dataRecordList) {
        List<CErrors> errorsList = new ArrayList<>();
        for (DataRecord record : dataRecordList) {
            //如果存在错误
            if (record.errorMsg.haveError()) {
                record.errorMsg.setErrorKey(String.format("第%s行存在以下错误:", getExcelIndex(record.nRindex) + ""));
                errorsList.add(record.errorMsg);
            }
        }
        if (!errorsList.isEmpty()) {
            throw new BatchServiceException(errorsList);
        }
    }

    /**
     * 校验数据是否合法
     *
     * @param record     数据对象
     * @param basicField 字段类型
     * @param oVal       结果
     */
    private void verifyFieldValLegal(DataRecord record, BasicField basicField, Object oVal) {
        //如果是必录
        if (basicField.mBust) {
            //如果为空
            if (StringUtils.isBlank(String.valueOf(oVal))) {
                record.errorMsg.addErrorInfo(basicField.mTitle, null, "数据为空,请录入!");
                return;
            }
        }
        //如果是必录或者值不为空
        if (basicField.mBust || StringUtils.isNotBlank(String.valueOf(oVal))) {
            //校验字段类型
            if (!basicField.mBasicFieldTypeEnum.checkVal(String.valueOf(oVal))) {
                record.errorMsg.addErrorInfo(basicField.mTitle, String.valueOf(oVal), "数据不合法!", basicField.mBasicFieldTypeEnum.getCorrect());
            }
        }
        //如果最大输入长度大于0
        if (basicField.maxInputLength > 0) {
            //如果是字符串类型
            if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.STRING) {
                if (oVal != null && String.valueOf(oVal).length() > basicField.maxInputLength) {
                    record.errorMsg.addErrorInfo(basicField.mTitle, String.valueOf(oVal), "数据长度超过最大长度" + basicField.maxInputLength + "!");
                }
            }
        }
        //如果字段校验对象不为空
        if (basicField.mFieldCheckVal != null) {
            //如果必录 或者 有值就进行校验
            if (basicField.mBust || StringUtils.isNotBlank(String.valueOf(oVal))) {
                //调用校验
                String strErrorMsg;
                try {
                    strErrorMsg = basicField.mFieldCheckVal.checkValErrorMsg(record.dataMap, String.valueOf(oVal));
                } catch (Exception e) {
                    log.error("校验数据出错,错误信息:" + e.getMessage());
                    strErrorMsg = e.getMessage();
                }
                //如果不为空 代表有错误信息
                if (StringUtils.isNotBlank(strErrorMsg)) {
                    record.errorMsg.addErrorInfo(basicField.mTitle, null, strErrorMsg);
                }
            }
        }
    }

    /**
     * 校验数据重复
     *
     * @param recordList    数据集合
     * @param onlyKeyList   唯一字段
     * @param rangeFieldMap 范围字段
     */
    private void verifyValRepeat(List<DataRecord> recordList, List<String> onlyKeyList, Map<String, RangeCheckField> rangeFieldMap) {
        List<Set<Integer>> repeatList = new ArrayList<>();
        //循环数据计算是否有重复数据
        for (int rowI = 0; rowI < recordList.size(); rowI++) {
            //获取当前行
            DataRecord curRecord = recordList.get(rowI);
            //记录重复的行号
            Set<Integer> rowList = new HashSet<>();
            rowList.add(curRecord.nRindex);
            //循环下一行
            for (int nextRow = rowI + 1; nextRow < recordList.size(); nextRow++) {
                StringBuilder expVal = new StringBuilder();
                //循环下一行数据
                DataRecord nextRecord = recordList.get(nextRow);
                //循环唯一字段
                for (int i = 0; i < onlyKeyList.size(); i++) {
                    String field = onlyKeyList.get(i);
                    //获取字段
                    BasicField basicField = this.mBasicFields.get(field);
                    expVal.append("'").append(curRecord.dataMap.get(field)).append("'=='").append(nextRecord.dataMap.get(field)).append("' && ");
                }
                //循环范围字段
                for (String rangeField : rangeFieldMap.keySet()) {
                    //获取范围字段
                    RangeCheckField rangeCheckField = rangeFieldMap.get(rangeField);
                    //数据结果
                    String startVal = curRecord.dataMap.get(rangeCheckField.getRangeStartField()), endVal = curRecord.dataMap.get(rangeCheckField.getRangeEndField());
                    String nextStartVal = nextRecord.dataMap.get(rangeCheckField.getRangeStartField()), nextEndVal = nextRecord.dataMap.get(rangeCheckField.getRangeEndField());
                    //如果为空
                    if (StringUtils.isBlank(startVal) && StringUtils.isBlank(endVal)) {
                        expVal.append("'").append(nextStartVal).append("'=='' && ");
                        expVal.append("'").append(nextEndVal).append("'=='' && ");
                        continue;
                    }
                    if (StringUtils.isBlank(nextStartVal) && StringUtils.isBlank(nextEndVal)) {
                        expVal.append("'").append(nextStartVal).append("'!='' && ");
                        expVal.append("'").append(nextEndVal).append("'!='' && ");
                        continue;
                    }
                    //添加单引号
                    switch (rangeCheckField.getBasicFieldTypeEnum()) {
                        case INT:
                        case DOUBLE2:
                        case DOUBLE4:
                            break;
                        default:
                            startVal = "'" + startVal + "'";
                            endVal = "'" + endVal + "'";
                            nextStartVal = "'" + nextStartVal + "'";
                            nextEndVal = "'" + nextEndVal + "'";
                            break;
                    }
                    expVal.append("(").append(nextEndVal).append(">").append(startVal).append(" && ").append(nextStartVal).append("<").append(endVal).append(")").append(" && ");
                }
                log.info("表达式:{}" , expVal.substring(0, expVal.length() - 4));
                //获取结果
                boolean flag = parser.parseExpression(expVal.substring(0, expVal.length() - 4)).getValue(Boolean.class);
                //存在重叠数据
                if (flag) {
                    rowList.add(nextRecord.nRindex);
                }
            }
            //如果大于1代表有重复
            if (rowList.size() > 1) {
                repeatList.add(rowList);
            }
        }
        //如果有重复的数据
        if (!repeatList.isEmpty()) {
            //错误信息
            List<CErrors> errorsList = new ArrayList<>();
            CErrors cErrors = new CErrors();
            for (int i = 0; i < repeatList.size(); i++) {
                Set<Integer> rowList = repeatList.get(i);
                StringBuilder strRow = new StringBuilder();
                //循环重复行号
                for (Integer integer : rowList) {
                    strRow.append(getExcelIndex(integer)).append(",");
                }
                cErrors.addErrorInfo("第[" + strRow.substring(0, strRow.length() - 1) + "]行数据重复,请检查!");
            }
            errorsList.add(cErrors);
            throw new BatchServiceException(errorsList);
        }
    }

    /**
     * 返回excel真实行数
     *
     * @param nRowIndex 行数
     * @return 真实行数
     */
    public int getExcelIndex(int nRowIndex) {
        return nRowIndex + mStartRow;
    }

    /**
     * 数据库校验重复
     *
     * @param recordList    数据集合
     * @param onlyKeyList   唯一字段
     * @param rangeFieldMap 范围字段
     * @return true有重复数据 false没有重复数据
     */
    private void verifyValRepeatSql(List<DataRecord> recordList, List<String> onlyKeyList,
                                    Map<String, RangeCheckField> rangeFieldMap) {
        //如果不需要检查数据库重复就返回
        if (!m_CheckDataSqlRepeat) {
            return;
        }
        //判断sql字段是否初始化过
        boolean bSqlFieldInit = false;
        //查找第一个如果已经不为空 代表已经初始化过  不需要再初始化
        for (String key : mBasicFields.keySet()) {
            BasicField basicField = mBasicFields.get(key);
            if (StringUtils.isNotBlank(basicField.mSqlField)) {
                bSqlFieldInit = true;
            }
            break;
        }
        //如果没有初始化过
        if (!bSqlFieldInit) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(m_TableName);
            Map<String, String> fieldMap = new HashMap<>();
            if (tableInfo != null) {
                List<TableFieldInfo> fieldList = tableInfo.getFieldList();
                for (TableFieldInfo fieldInfo : fieldList) {
                    fieldMap.put(fieldInfo.getProperty().toLowerCase(), fieldInfo.getColumn());
                }
            }
            //设置sql字段
            for (String key : mBasicFields.keySet()) {
                BasicField basicField = mBasicFields.get(key);
                //如果存在
                if (fieldMap.containsKey(basicField.mField)) {
                    basicField.mSqlField = fieldMap.get(basicField.mField);
                } else {
                    basicField.mSqlField = basicField.mField;
                }
            }
        }
        //错误信息
        List<CErrors> errorsList = new ArrayList<>();
        //循环数据计算是否有重复数据
        for (DataRecord record : recordList) {
            QueryWrapper<Object> queryWrapper = Wrappers.query();
            //循环唯一字段
            for (String key : onlyKeyList) {
                String sVal = record.dataMap.get(key);
                //获取字段
                BasicField field = mBasicFields.get(key);
                //获取SQL直实字段
                String dataField = field.mSqlField;
                //如果为空
                if (StringUtils.isBlank(dataField)) {
                    dataField = field.mField;
                }
                //结果不为空才校验
                if (StringUtils.isNotBlank(sVal)) {
                    //判断类型
                    switch (field.mBasicFieldTypeEnum) {
                        case INT:
                            queryWrapper.eq(dataField, Long.parseLong(sVal));
                            break;
                        case DOUBLE2:
                        case DOUBLE4:
                            queryWrapper.eq(dataField, Double.parseDouble(sVal));
                            break;
                        default:
                            queryWrapper.eq(dataField, sVal);
                            break;

                    }
                } else {
                    queryWrapper.eq(dataField, "");
                }
            }
            //如果存在范围字段
            if (!rangeFieldMap.isEmpty()) {
                //循环辅助字段
                for (String assistF : rangeFieldMap.keySet()) {
                    //获取范围字段
                    RangeCheckField rangeCheckField = rangeFieldMap.get(assistF);
                    //当前行结果
                    Object oCurStartVal = record.dataMap.get(rangeCheckField.getRangeStartField());
                    Object oCurEndVal = record.dataMap.get(rangeCheckField.getRangeEndField());
                    //检查是否有重复 AB 是否包含CD  CD是否包含AB
                    rangeCheckField.getRangeCheckTypeEnum().checkValSql(rangeCheckField.getBasicFieldTypeEnum(), rangeCheckField.getRangeStartField(), rangeCheckField.getRangeEndField(), oCurStartVal, oCurEndVal, queryWrapper);
                }
            }
            //查询数据库
            String strVal = saveFrameworkMapper.checkDataRepeat(m_TableName, queryWrapper);
            //如果重复
            if ("Y".equals(strVal)) {
                CErrors cErrors = new CErrors();
                cErrors.addErrorInfo("第[" + getExcelIndex(record.nRindex) + "]行数据与数据库数据重复,请检查!");
                errorsList.add(cErrors);
            }
        }
        if (!errorsList.isEmpty()) {
            throw new BatchServiceException(errorsList);
        }
    }

    /**
     * 读取excel校验数据
     *
     * @param fieldPath    excel路径
     * @param sheetIndex   sheet索引
     * @param startRow     开始行
     * @param strTableName 表名
     * @return true没有错误 false异常
     */
    public boolean readExcelCheckInsert(String fieldPath, int sheetIndex, int startRow, String strTableName) throws SaveFrameworkException {
        return readExcelCheckInsert(fieldPath, sheetIndex, startRow, strTableName, true);
    }

    /**
     * 读取excel校验数据
     *
     * @param file         文件流
     * @param sheetIndex   sheet索引
     * @param startRow     开始行
     * @param strTableName 表名
     * @return true没有错误 false异常
     */
    public boolean readExcelCheckInsert(MultipartFile file, int sheetIndex, int startRow, String strTableName) throws SaveFrameworkException {
        return readExcelCheckBase(null, file, sheetIndex, startRow, strTableName, true);
    }

    /**
     * 读取excel校验数据
     *
     * @param fieldPath         excel路径
     * @param sheetIndex        sheet索引
     * @param startRow          开始行
     * @param strTableName      表名
     * @param checkAllFieldNull 是否检查所有字段为空
     *                          true 校验当前sheet页的所有列校验
     *                          false 只校验当前配置的读取字段
     * @return true没有错误 false异常
     */
    public boolean readExcelCheckInsert(String fieldPath, int sheetIndex, int startRow, String strTableName, boolean checkAllFieldNull) throws SaveFrameworkException {
        return readExcelCheckBase(fieldPath, null, sheetIndex, startRow, strTableName, checkAllFieldNull);
    }

    /**
     * 读取excel校验数据
     *
     * @param fieldPath         excel路径
     * @param file              文件流
     * @param sheetIndex        sheet索引
     * @param startRow          开始行
     * @param strTableName      表名
     * @param checkAllFieldNull 是否检查所有字段为空
     *                          true 校验当前sheet页的所有列校验
     *                          false 只校验当前配置的读取字段
     * @return true没有错误 false异常
     */
    private boolean readExcelCheckBase(String fieldPath, MultipartFile file, int sheetIndex, int startRow, String strTableName, boolean checkAllFieldNull) throws SaveFrameworkException {
        ExcelReader excelReader = null;
        //如果文件不为空
        try {
            if (StringUtils.isNotBlank(fieldPath)) {
                excelReader = ExcelUtil.getReader(fieldPath, sheetIndex);
            } else if (file != null) {
                excelReader = ExcelUtil.getReader(file.getInputStream(), sheetIndex);
            }
        } catch (IOException e) {
            throw new SaveFrameworkException("解析excel出错,错误信息:" + e.getMessage());
        }
        //记录开始行
        mStartRow = startRow;
        // 获取最后一行索引
        int nlastRowIndex = excelReader.getRowCount();
        //记录表名
        m_TableName = strTableName;
        //获取最小最大列索引
        int minCellIndex = -1;
        int maxCellIndex = -1;
        //如果不是校验所有字段为空
        if (!checkAllFieldNull) {
            for (String nField : mBasicFields.keySet()) {
                //获取字段对象
                BasicField basicField = mBasicFields.get(nField);
                //如果-1初始化一下
                if (minCellIndex == -1) {
                    minCellIndex = basicField.mColIndex;
                }
                if (maxCellIndex == -1) {
                    maxCellIndex = basicField.mColIndex;
                }
                //如果不是-1代表需要读数据
                if (basicField.mColIndex != -1) {
                    minCellIndex = Math.min(minCellIndex, basicField.mColIndex);
                    maxCellIndex = Math.max(maxCellIndex, basicField.mColIndex);
                }
            }
        }
        //结果
        List<DataRecord> recordList = new ArrayList<>();
        // 循环行
        for (int rIndex = startRow; rIndex <= nlastRowIndex; rIndex++) {
            // 获取行对象
            Row row = excelReader.getOrCreateRow(rIndex);
            // 如果不为空
            if (row != null) {
                // 获取开始单元格
                int firstCellIndex = row.getFirstCellNum();
                // 获取最后一个单元格
                int lastCellIndex = row.getLastCellNum();
                if (checkAllFieldNull) {
                    //校验是否为空行跳过
                    if (!verifyEmptyRow(firstCellIndex, lastCellIndex, row)) {
                        continue;
                    }
                } else {
                    //重新检查是否空白行
                    if (!verifyEmptyRow(minCellIndex, maxCellIndex, row)) {
                        continue;
                    }
                }
                //结果对象
                DataRecord record = new DataRecord();
                record.nRindex = (rIndex - startRow + 1);
                //循环列字段
                for (String nField : mBasicFields.keySet()) {
                    //获取字段对象
                    BasicField basicField = mBasicFields.get(nField);
                    String strVal = "";
                    //如果不是-1代表需要读数据
                    if (basicField.mColIndex != -1) {
                        // 检查单元格合法
                        if (basicField.mColIndex >= firstCellIndex && basicField.mColIndex <= lastCellIndex) {
                            // 如果是数字类型
                            strVal = getCellValue(row, basicField);
                        }
                    }
                    setRecordValue(record, basicField, strVal);
                }
                //添加到集合
                recordList.add(record);
                m_DataRecordMap.put(record.nRindex, record);
            }
        }
        return checkDataRecordVal(recordList);
    }

    /**
     * 检查是否有值
     *
     * @param colStart 开始列
     * @param colEnd   终止列
     * @param row      行对象
     * @return true有数据 false没有数据
     */
    private boolean verifyEmptyRow(int colStart, int colEnd, Row row) {
        for (int j = colStart; j < colEnd; j++) {
            if (row.getCell(j) != null && StringUtils.isNotBlank(row.getCell(j).toString())) {
                // 此行有数据
                return true;
            }
        }
        return false;
    }

    /**
     * 获取单元格内容
     *
     * @param row    行对象
     * @param cReCon 字段对象
     * @return 单元格结果
     */
    private String getCellValue(Row row, BasicField cReCon) {
        String strVal = "";
        Cell cell = row.getCell(cReCon.mColIndex);
        if (cell != null) {
            try {
                strVal = cell.getRichStringCellValue().getString();
            } catch (Exception e) {
                cell.setCellType(CellType.STRING);
                strVal = cell.getRichStringCellValue().getString();
            }
            //如果是日期格式特殊转换一下
            if (cReCon.mBasicFieldTypeEnum == BasicFieldTypeEnum.DATE1) {
                if (NumberUtil.isNumber(strVal)) {
                    strVal = DateUtils.parseNumToDateStr(strVal);
                }
            }

        }
        return strVal != null ? strVal.trim() : strVal;
    }


    /**
     * 校验互斥数据
     *
     * @param record     当前行数据
     * @param nextRecord 下一行数据
     * @return true存在互斥 false正常
     */
    private boolean verifyMutexesVal(Map<String, String> mutDataMap, Map<String, List<List<String>>> mutexesFieldMap,
                                     DataRecord record, DataRecord nextRecord) {
        if (mutexesFieldMap != null) {
            return false;
        }
        //循环互斥字段
        for (String field : mutexesFieldMap.keySet()) {
            String curVal = record.dataMap.get(field);
            String nextVal = nextRecord.dataMap.get(field);
            //当前结果集对比序列
            String curCompareV = mutDataMap.get(field + curVal);
            String nextCompareV = mutDataMap.get(field + nextVal);
            //如果序列不相等代表存在互斥
            if (!curCompareV.equals(nextCompareV)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验范围值是否合法
     *
     * @param record        数据对象
     * @param nextRecord    对比数据对象
     * @param rangeFieldMap 范围字段
     * @return true存在重叠 false正常
     */
    private boolean verifyRangeRepeatVal(DataRecord record, DataRecord nextRecord, Map<String, RangeCheckField> rangeFieldMap) {
        //如果空返回true
        if (rangeFieldMap == null) {
            return true;
        }
        //循环数据
        boolean[] bAll = new boolean[rangeFieldMap.size()];
        int nI = 0;
        //循环辅助字段
        for (String assistF : rangeFieldMap.keySet()) {
            boolean flag = false;
            //获取范围字段
            RangeCheckField rangeCheckField = rangeFieldMap.get(assistF);
            //当前行结果
            Object oCurStartVal = record.dataMap.get(rangeCheckField.getRangeStartField());
            Object oCurEndVal = record.dataMap.get(rangeCheckField.getRangeEndField());
            //获取之前结果
            Object oUpStartVal = nextRecord.dataMap.get(rangeCheckField.getRangeStartField());
            Object oUpEndVal = nextRecord.dataMap.get(rangeCheckField.getRangeEndField());
            if (rangeCheckField.getBasicFieldTypeEnum() == BasicFieldTypeEnum.DATE3) {//将date3类型的数据转换成date2类型
                flag = true;
                oCurStartVal = (oCurStartVal + "01");
                oCurEndVal = (oCurEndVal + "01");
                oUpStartVal = (oUpStartVal + "01");
                oUpEndVal = (oUpEndVal + "01");
                rangeCheckField.setBasicFieldTypeEnum(BasicFieldTypeEnum.DATE2);
            }
            //检查是否有重复 AB 是否包含CD  CD是否包含AB
            boolean b1 = rangeCheckField.getRangeCheckTypeEnum().checkVal(rangeCheckField.getBasicFieldTypeEnum(), oCurStartVal, oCurEndVal, oUpStartVal, oUpEndVal);
            bAll[nI++] = b1;
            if (flag) {
                rangeCheckField.setBasicFieldTypeEnum(BasicFieldTypeEnum.DATE3);
            }
        }
        boolean bF = true;
        //循环所有范围结果
        for (boolean b : bAll) {
            //如果有假则结束 代表不重复
            if (!b) {
                bF = false;
                break;
            }
        }
        return bF;
    }

    /**
     * 结果转换实实体类
     *
     * @param clazz
     * @return
     */
    public <T> List<T> dataRecordToEntity(Class<T> clazz) throws SaveFrameworkException {
        List<T> list = new ArrayList<>();
        // 获取类的字段并创建字段映射
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            fieldMap.put(field.getName().toLowerCase(), field);
        }
        //转换的字段
        String fieldName = "";
        try {
            // 实例化对象时考虑使用带有参数的构造函数
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            for (Integer i : m_DataRecordMap.keySet()) {
                T t = constructor.newInstance();
                DataRecord dataRecord = m_DataRecordMap.get(i);
                //循环字段
                for (String nField : mBasicFields.keySet()) {
                    //获取字段对象
                    BasicField basicField = mBasicFields.get(nField);
                    //如果不是类字段跳过
                    if (!basicField.mSpecial) {
                        continue;
                    }
                    //如果没有匹配到跳过
                    if (!fieldMap.containsKey(nField)) {
                        continue;
                    }
                    fieldName = nField;
                    extracted(nField, fieldMap, basicField, t, dataRecord);
                }
                list.add(t);
            }
        } catch (Exception e) {
            log.error("转换实体类出错{} {}", fieldName, e.getMessage());
            throw new SaveFrameworkException("转换实体类出错,请检查数据!");
        }
        return list;
    }

    /**
     * 设置结果值
     *
     * @param nField     字段
     * @param fieldMap   字段映射
     * @param basicField 字段对象
     * @param t          实体类
     * @param dataRecord 数据对象
     * @param <T>        泛型
     * @throws IllegalAccessException
     */
    private <T> void extracted(String nField, Map<String, Field> fieldMap, BasicField basicField, T t, DataRecord dataRecord) throws IllegalAccessException {
        Field field = fieldMap.get(basicField.mField);
        if (field != null) {
            field.setAccessible(true);
            if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DATE1 || basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DATE2) {
                //设置日期
                field.set(t, DateUtils.parseDate(dataRecord.dataMap.get(nField)));
            } else if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE2 || basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.DOUBLE4) {
                if (field.getType() == BigDecimal.class) {
                    field.set(t, NumberUtil.toBigDecimal(dataRecord.dataMap.get(nField)));
                } else {
                    //设置double
                    field.set(t, NumberUtil.toBigDecimal(dataRecord.dataMap.get(nField)).doubleValue());
                }
            } else if (basicField.mBasicFieldTypeEnum == BasicFieldTypeEnum.INT) {
                // 根据字段类型设置
                if (field.getType() == long.class || field.getType() == Long.class) {
                    field.set(t, NumberUtil.parseLong(dataRecord.dataMap.get(nField)));
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    field.set(t, NumberUtil.parseInt(dataRecord.dataMap.get(nField)));
                }
            } else {
                Object oVal = dataRecord.dataMap.get(nField);
                if (oVal != null) {
                    field.set(t, oVal.toString().trim());
                }
            }
        }
    }

    /**
     * 遍历数据比较
     *
     * @param checkValueMap 明细数据
     * @param rangeFieldMap 范围字段集合
     * @return 是否存在重复数据
     */
    private List<Set<Integer>> traverseCompare(Map<String, List<Integer>> checkValueMap, Map<String, RangeCheckField> rangeFieldMap) {
        List<Set<Integer>> repeatList = new ArrayList<>();
        //循环重复的key
        for (String repeatKey : checkValueMap.keySet()) {
            //获取重复的行号
            List<Integer> rList = checkValueMap.get(repeatKey);
            //如果重复数据大于等于2
            if (rList.size() >= 2) {
                //循环重复的行号
                for (int nR = 0; nR < rList.size(); nR++) {
                    DataRecord curRecord = this.m_DataRecordMap.get(rList.get(nR));
                    //循环下一条数据
                    for (int nN = nR + 1; nN < rList.size(); nN++) {
                        DataRecord nextRecord = this.m_DataRecordMap.get(rList.get(nN));
                        boolean bVal = false;
                        //如果是范围校验
                        bVal = verifyRangeRepeatVal(curRecord, nextRecord, rangeFieldMap);
                        if (bVal) {
                            Set<Integer> rowList = new HashSet<>();
                            rowList.add(getExcelIndex(curRecord.nRindex));
                            rowList.add(getExcelIndex(nextRecord.nRindex));
                            repeatList.add(rowList);
                        }
                    }
                }
            }
        }
        return repeatList;
    }
}
