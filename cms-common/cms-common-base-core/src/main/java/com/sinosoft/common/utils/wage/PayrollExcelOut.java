package com.sinosoft.common.utils.wage;

import com.sinosoft.common.domain.ExcelConfigBase;
import com.sinosoft.common.domain.ExcelSheetBase;
import com.sinosoft.common.schema.commission.domain.LaWageIndexInfo;
import com.sinosoft.common.schema.commission.domain.vo.WageBaseInfoVo;
import com.sinosoft.common.schema.common.domain.WageCalElementsConfig;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;
import com.sinosoft.common.service.ICommissionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.core.utils.reflect.ReflectUtils;
import com.sinosoft.common.domain.dto.ExcelConfigDto;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.enums.IndexcalTypeEnum;
import com.sinosoft.common.excel.ExcelUtils;
import com.sinosoft.common.excel.HeadEntity;
import com.sinosoft.common.schema.commission.domain.Lawage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 工资单打印
 * @author: zzf
 * @create: 2024-02-06 09:52
 */
@Slf4j
public class PayrollExcelOut {

    /**
     * 渠道
     */
    private final BranchTypeEnum mBranchTypeEnum;
    /**
     * 佣金类型
     */
    private final IndexcalTypeEnum mIndexcalTypeEnum;
    /**
     * 基本法版本
     */
    private final Integer CAL_BASE;
    /**
     * 佣金规则服务
     */
    private final ICommissionService wageCalculationDefinitionService;

    /**
     * 构造函数
     *
     * @param branchTypeEnum   渠道
     * @param indexcalTypeEnum 佣金类型
     * @param base             基本法版本
     */
    public PayrollExcelOut(BranchTypeEnum branchTypeEnum, IndexcalTypeEnum indexcalTypeEnum, Integer base) {
        this.mBranchTypeEnum = branchTypeEnum;
        this.mIndexcalTypeEnum = indexcalTypeEnum;
        this.CAL_BASE = base;
        this.wageCalculationDefinitionService = SpringUtils.getBean(ICommissionService.class);
    }

    /**
     * 打印工资单
     *
     * @param headEntities    标题
     * @param dataList        数据
     * @param wageList        工资数据
     * @param wageBaseInfoVos 人员基础信息集合
     * @param wageNo          佣金年月
     * @param response        响应对象
     */
    public void printPayroll(List<HeadEntity> headEntities, List<List<String>> dataList, List<Lawage> wageList, List<WageBaseInfoVo> wageBaseInfoVos, String wageNo, HttpServletResponse response) {
        log.info("渠道：{}，佣金类型：{}，开始打印工资单", mBranchTypeEnum, mIndexcalTypeEnum);
        ExcelConfigDto excelConfigDto = new ExcelConfigDto();
        ExcelConfigBase excelConfigBase = new ExcelConfigBase();
        excelConfigBase.setId(0L);
        excelConfigBase.setCode("");
        excelConfigBase.setName("");
        excelConfigBase.setIsImport(false);

        excelConfigDto.setConfigBase(excelConfigBase);
        excelConfigDto.setSheetBases(new ArrayList<>());
        //转换map
        Map<String, WageBaseInfoVo> agentBaseInfoMap = wageBaseInfoVos.stream().collect(Collectors.toMap(WageBaseInfoVo::getAgentCode, agent -> agent));
        //查询工资单配置
        List<WageCalculationDefinition> definitionList = wageCalculationDefinitionService.getOutExcelDefinitionList(mBranchTypeEnum, mIndexcalTypeEnum, this.CAL_BASE);
        //基础工资单
        initBaseWage(excelConfigDto, headEntities, dataList, wageList, definitionList, wageNo);
        //过程工资单
        initProcessWage(excelConfigDto, definitionList, wageNo, wageList.stream().map(Lawage::getAgentcode).toList(), agentBaseInfoMap);

        ExcelUtils.downloadExcelTemplateMultiSheet(response, excelConfigDto);
    }


    /**
     * 初始化基础工资单
     *
     * @param excelConfigDto
     * @param headEntities
     * @param dataList
     * @param wageList
     * @param definitionList
     * @param wageNo
     */
    private void initBaseWage(ExcelConfigDto excelConfigDto, List<HeadEntity> headEntities, List<List<String>> dataList, List<Lawage> wageList, List<WageCalculationDefinition> definitionList, String wageNo) {
        ExcelSheetBase sheetE = new ExcelSheetBase();
        sheetE.setSheetIndex(0);
        sheetE.setTitle(wageNo + "工资单");
        sheetE.setSheetName("工资明细");
        int nIndex = headEntities.size();
        List<String> wageFields = new ArrayList<>();
        //循环配置
        for (WageCalculationDefinition definition : definitionList) {
            headEntities.add(new HeadEntity(nIndex++, definition.getCalName(), "2", 5000));
            wageFields.add(definition.getTableColname());
        }
        //获取字段map
        Map<String, Field> fieldMap = ReflectUtils.getFieldMapByLower(Lawage.class);
        //工资数据转换
        Map<String, Lawage> wageMap = wageList.stream().collect(Collectors.toMap(Lawage::getAgentcode, wage -> wage));
        //获取工号
        List<String> agents = wageList.stream().map(Lawage::getAgentcode).toList();
        for (int i = 0; i < dataList.size(); i++) {
            List<String> datas = dataList.get(i);
            String agentCode = agents.get(i);
            Lawage laWage = wageMap.get(agentCode);
            //循环工资字段
            for (String field : wageFields) {
                datas.add(String.valueOf(ReflectUtils.getFieldValue(laWage, fieldMap.get(field))));
            }
        }
        sheetE.setSheetIndex(1);
        sheetE.convert(headEntities);
        sheetE.setDataList(dataList);
        excelConfigDto.getSheetBases().add(sheetE);
    }

    /**
     * 初始化过程工资
     *
     * @param excelConfigDto excel配置对象
     * @param definitionList 过程配置
     * @param wageNo         佣金年月
     * @param agents         工号
     */
    public void initProcessWage(ExcelConfigDto excelConfigDto, List<WageCalculationDefinition> definitionList, String wageNo, List<String> agents, Map<String, WageBaseInfoVo> agentBaseInfoMap) {
        int nSheetIndex = 2;
        //循环配置
        for (WageCalculationDefinition definition : definitionList) {
            //如果有过程
            if (StringUtils.isNotBlank(definition.getCalProcessElem())) {
                String[] elems = definition.getCalProcessElem().split(",");
                String[] titles = definition.getCalGroupName().split(",");
                int eleIndex = 0;
                //循环过程
                for (String elem : elems) {
                    ExcelSheetBase indexSheet = new ExcelSheetBase();
                    List<WageCalElementsConfig> elementsConfigs = this.wageCalculationDefinitionService.getCalElementsList(this.CAL_BASE, elem);
                    List<HeadEntity> wageHeadTiles = new ArrayList<>();
                    wageHeadTiles.add(new HeadEntity(0, "序号", "9", 1500));
                    wageHeadTiles.add(new HeadEntity(1, "管理机构", "0", 8500));
                    wageHeadTiles.add(new HeadEntity(2, "工号", "0", 2500));
                    wageHeadTiles.add(new HeadEntity(3, "姓名", "0", 2500));
                    int headIndex = 4;
                    for (WageCalElementsConfig elementsConfig : elementsConfigs) {
                        wageHeadTiles.add(HeadEntity.convert(headIndex++, elementsConfig));
                    }
                    indexSheet.setSheetIndex(nSheetIndex++);
                    indexSheet.setTitle(titles[eleIndex]);
                    indexSheet.setSheetName(titles[eleIndex]);
                    indexSheet.convert(wageHeadTiles);
                    List<LaWageIndexInfo> wageIndexInfoList = this.wageCalculationDefinitionService.getWageIndexInfoList(this.mIndexcalTypeEnum, wageNo, elem, agents);
                    List<List<String>> dataValList = new ArrayList<>(wageIndexInfoList.size());
                    int nRow = 0;
                    if (!wageIndexInfoList.isEmpty()) {
                        String[] defValues = new String[elementsConfigs.size()];
                        Arrays.fill(defValues, "");
                        long nCalId = wageIndexInfoList.get(0).getCalId();
                        String curAgentCode = wageIndexInfoList.get(0).getAgentCode();
                        int nReadIndex = 0;
                        for (int i = 0; i < wageIndexInfoList.size(); i++) {
                            LaWageIndexInfo infoSchema = wageIndexInfoList.get(i);
                            if (nCalId != infoSchema.getCalId()) {
                                nRow++;
                                WageBaseInfoVo info = agentBaseInfoMap.get(curAgentCode);
                                List<String> dList = new ArrayList<>();
                                dList.addAll(List.of(String.valueOf(nRow), info.getManageCom(), info.getAgentCode(), info.getAgentName()));
                                dList.addAll(List.of(Arrays.copyOf(defValues, defValues.length)));
                                dataValList.add(dList);
                                nCalId = infoSchema.getCalId();
                                curAgentCode = infoSchema.getAgentCode();
                                nReadIndex = 0;
                            }
                            String[] strProessValues = WageIndexInfo.getLawageInfoValues(infoSchema);
                            //开始位置
                            int rStart = nReadIndex * WageIndexInfo.COLUMN_MAX;
                            //要读的个数
                            int rCount = Math.min(defValues.length - rStart, WageIndexInfo.COLUMN_MAX);
                            System.arraycopy(strProessValues, 0, defValues, rStart, rCount);
                            //最后一行添加
                            if (i == wageIndexInfoList.size() - 1) {
                                nRow++;
                                WageBaseInfoVo info = agentBaseInfoMap.get(curAgentCode);
                                List<String> dList = new ArrayList<>();
                                dList.addAll(List.of(String.valueOf(nRow), info.getManageCom(), info.getAgentCode(), info.getAgentName()));
                                dList.addAll(List.of(Arrays.copyOf(defValues, defValues.length)));
                                dataValList.add(dList);
                            }
                            nReadIndex++;
                        }
                    }
                    indexSheet.setDataList(dataValList);
                    excelConfigDto.getSheetBases().add(indexSheet);
                    eleIndex++;
                }
            }
        }
    }
}
