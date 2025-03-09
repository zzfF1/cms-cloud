package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.domain.dto.CommissionCalMainDto;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.enums.IndexcalTypeEnum;
import com.sinosoft.common.schema.commission.domain.LaWageIndexInfo;
import com.sinosoft.common.schema.commission.mapper.LaWageIndexInfoMapper;
import com.sinosoft.common.schema.commission.mapper.LawageMapper;
import com.sinosoft.common.schema.common.domain.BaseLawVersion;
import com.sinosoft.common.schema.common.domain.WageCalElementsConfig;
import com.sinosoft.common.schema.common.domain.WageCalGradeRelation;
import com.sinosoft.common.schema.common.domain.WageCalculationDefinition;
import com.sinosoft.common.schema.common.mapper.BaseLawVersionMapper;
import com.sinosoft.common.schema.common.mapper.WageCalElementsConfigMapper;
import com.sinosoft.common.schema.common.mapper.WageCalGradeRelationMapper;
import com.sinosoft.common.schema.common.mapper.WageCalculationDefinitionMapper;
import com.sinosoft.common.service.ICommissionService;
import com.sinosoft.common.utils.wage.WageCalElements;
import com.sinosoft.common.utils.wage.WageRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金Service业务层处理
 *
 * @author zzf
 * @date 2023-07-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommissionServiceImpl implements ICommissionService {
    private final BaseLawVersionMapper baseLawVersionMapper;
    private final WageCalculationDefinitionMapper wageCalculationDefinitionMapper;
    private final WageCalElementsConfigMapper wageCalElementsConfigMapper;
    private final WageCalGradeRelationMapper wageCalGradeRelationMapper;
    private final LaWageIndexInfoMapper laWageIndexInfoMapper;
    private final LawageMapper lawageMapper;

    @Override
    public BaseLawVersion selectByBranchTypeAndCalType(String branchType, String indexCalTYpe) {
        LambdaQueryWrapper<BaseLawVersion> lqw = Wrappers.lambdaQuery();
        lqw.eq(BaseLawVersion::getBranchType, branchType);
        lqw.eq(BaseLawVersion::getIndexCalType, indexCalTYpe);
        lqw.eq(BaseLawVersion::getStatus, "1");
        return baseLawVersionMapper.selectOne(lqw);
    }

    @Override
    public BaseLawVersion selectById(Long id) {
        return baseLawVersionMapper.selectById(id);
    }

    @Override
    public boolean clearAndinitCalRunParms(CommissionCalMainDto wageCalDelayedDto) {
        //缓存数据
        WageRedisUtil wageRedisUtil = new WageRedisUtil(wageCalDelayedDto.getMd5Val(),wageCalDelayedDto.getUuid());
        wageRedisUtil.clearRedis();
        //查询佣金规则定义列表
        List<WageCalculationDefinition> definitionList = getDefinitionList(wageCalDelayedDto.getIndexCalType().getCode(), wageCalDelayedDto.getWageType().getCode(), wageCalDelayedDto.getBaseLawVersion());
        wageRedisUtil.initParmsDEF(definitionList);
//        //过程ID集合
//        List<String> calElements = new ArrayList<>();
        //循环数据
        for (WageCalculationDefinition def : definitionList) {
            //不为空
            if (StringUtils.isNotBlank(def.getCalProcessElem())) {
                //获取过程ID分割
                String[] elems = def.getCalProcessElem().split(",");
                //如果有过程
                if (elems.length > 0) {
                    List<WageCalElements> calElementsList = new ArrayList<>();
                    //循环过程ID
                    for (String elem : elems) {
                        long lCount = getCalElementsCount(wageCalDelayedDto.getBaseLawVersion(), elem);
                        WageCalElements wageCalElements = new WageCalElements();
                        wageCalElements.setCalElementsCode(elem);
                        wageCalElements.setElementCount((int) lCount);
                        calElementsList.add(wageCalElements);
                    }
                    wageRedisUtil.initParmsELE(def, calElementsList);
                }
            }
        }
        //查询佣金与职级关系列表
        List<WageCalGradeRelation> gradeRelationList = getGradeRelationList(wageCalDelayedDto.getBranchType().getCode(), wageCalDelayedDto.getBaseLawVersion());
        wageRedisUtil.initParmsGradeRelation(gradeRelationList);
        return false;
    }

    /**
     * 获取佣金规则定义列表
     *
     * @param indexCalType 计算类型
     * @param wageType     佣金类型
     * @param version      版本
     * @return 佣金规则定义列表
     */
    public List<WageCalculationDefinition> getDefinitionList(String indexCalType, String wageType, long version) {
        LambdaQueryWrapper<WageCalculationDefinition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WageCalculationDefinition::getIndexCalType, indexCalType);
        queryWrapper.eq(WageCalculationDefinition::getWageType, wageType);
        queryWrapper.eq(WageCalculationDefinition::getBaseLawId, version);
        // 修改排序逻辑：先按步长排序，再按cal_order排序
        queryWrapper.orderByAsc(WageCalculationDefinition::getWagecalStep)
            .orderByAsc(WageCalculationDefinition::getCalOrder);
        return wageCalculationDefinitionMapper.selectList(queryWrapper);
    }

    /**
     * 查询职级与工资项关系列表
     *
     * @param branchType 渠道
     * @param version    版本
     * @return 职级与工资项关系列表
     */
    public List<WageCalGradeRelation> getGradeRelationList(String branchType, long version) {
        LambdaQueryWrapper<WageCalGradeRelation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WageCalGradeRelation::getBranchType, branchType);
        queryWrapper.eq(WageCalGradeRelation::getBaseLawId, version);
        return wageCalGradeRelationMapper.selectList(queryWrapper);
    }

    /**
     * 查询过程明细表
     *
     * @param version     版本
     * @param calElements 过程集合
     * @return
     */
    public List<WageCalElementsConfig> getCalElementsList(long version, List<String> calElements) {
        LambdaQueryWrapper<WageCalElementsConfig> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(WageCalElementsConfig::getCalElements, calElements);
        queryWrapper.eq(WageCalElementsConfig::getBaseLawId, version);
        queryWrapper.orderByAsc(WageCalElementsConfig::getCalElements, WageCalElementsConfig::getRowIndex);
        return wageCalElementsConfigMapper.selectList(queryWrapper);
    }

    /**
     * 查询佣金过程配置表
     *
     * @param version    版本
     * @param calElement 过程ID
     * @return 过程配置
     */
    @Override
    public List<WageCalElementsConfig> getCalElementsList(int version, String calElement) {
        LambdaQueryWrapper<WageCalElementsConfig> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WageCalElementsConfig::getCalElements, calElement);
        queryWrapper.eq(WageCalElementsConfig::getBaseLawId, version);
        queryWrapper.orderByAsc(WageCalElementsConfig::getRowIndex);
        return wageCalElementsConfigMapper.selectList(queryWrapper);
    }

    /**
     * 获取计算过程个数
     *
     * @param version 版本
     * @param element 过程ID
     * @return 个数
     */
    public long getCalElementsCount(long version, String element) {
        QueryWrapper<WageCalElementsConfig> queryWrapper = Wrappers.query();
        queryWrapper.eq("cal_elements", element);
        queryWrapper.eq("base_law_id", version);
        return wageCalElementsConfigMapper.selectCount(queryWrapper);
    }

    /**
     * 获取导出excel定义列表
     *
     * @param branchTypeEnum   渠道
     * @param indexcalTypeEnum 佣金类型
     * @param version          版本
     * @return 导出excel定义列表
     */
    @Override
    public List<WageCalculationDefinition> getOutExcelDefinitionList(BranchTypeEnum branchTypeEnum, IndexcalTypeEnum indexcalTypeEnum, int version) {
        LambdaQueryWrapper<WageCalculationDefinition> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WageCalculationDefinition::getBranchType, branchTypeEnum.getCode());
        queryWrapper.eq(WageCalculationDefinition::getIndexCalType, indexcalTypeEnum.getCode());
        queryWrapper.eq(WageCalculationDefinition::getBaseLawId, version);
        queryWrapper.eq(WageCalculationDefinition::getOutExcel, 1);
        queryWrapper.orderByAsc(WageCalculationDefinition::getOutOrder);
        return wageCalculationDefinitionMapper.selectList(queryWrapper);
    }

    /**
     * 查询工资过程数据
     *
     * @param indexcalTypeEnum 计算类型
     * @param wageNo           佣金年月
     * @param calElements      过程ID
     * @param agentCodes       代理人
     * @return 工资过程数据
     */
    @Override
    public List<LaWageIndexInfo> getWageIndexInfoList(IndexcalTypeEnum indexcalTypeEnum, String wageNo, String calElements, List<String> agentCodes) {
        LambdaQueryWrapper<LaWageIndexInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(LaWageIndexInfo::getIndexCalNo, wageNo);
        queryWrapper.eq(LaWageIndexInfo::getIndexCalType, indexcalTypeEnum.getCode());
        queryWrapper.eq(LaWageIndexInfo::getCalElements, calElements);
        queryWrapper.in(LaWageIndexInfo::getAgentCode, agentCodes);
        queryWrapper.orderByAsc(LaWageIndexInfo::getAgentCode, LaWageIndexInfo::getCalId, LaWageIndexInfo::getRowIndex);
        return laWageIndexInfoMapper.selectList(queryWrapper);
    }
}
