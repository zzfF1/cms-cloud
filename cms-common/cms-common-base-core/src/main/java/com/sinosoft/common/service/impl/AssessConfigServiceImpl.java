package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sinosoft.common.exception.AssessConfigNotFindException;
import com.sinosoft.common.schema.common.mapper.LaAssessCalIndexConfigMapper;
import com.sinosoft.common.schema.common.mapper.LaAssessCalModeMapper;
import com.sinosoft.common.schema.common.mapper.LaAssessConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.schema.common.domain.LaAssessCalIndexConfig;
import com.sinosoft.common.schema.common.domain.LaAssessCalMode;
import com.sinosoft.common.schema.common.domain.LaAssessConfig;
import com.sinosoft.common.domain.dto.AssessWhenCalDto;
import com.sinosoft.common.enums.AssessPeriodEnum;
import com.sinosoft.common.enums.AssessWayEnum;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.service.IAssessConfigService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 考核配置实现
 * @author: zzf
 * @create: 2024-01-22 11:35
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AssessConfigServiceImpl implements IAssessConfigService {
    private final LaAssessConfigMapper laAssessConfigMapper;
    private final LaAssessCalModeMapper laAssessCalModeMapper;
    private final LaAssessCalIndexConfigMapper laAssessCalIndexConfigMapper;

    /**
     * 获取考核配置
     *
     * @param assessVersionId 考核版本
     * @param branchTypeEnum  渠道
     * @param assessWayEnum   考核方式
     * @param assessDate      考核年月
     */
    @Override
    public AssessWhenCalDto getAssessConfig(Long assessVersionId, BranchTypeEnum branchTypeEnum, AssessWayEnum assessWayEnum, String assessDate) {
        AssessWhenCalDto assessWhenCalDto = new AssessWhenCalDto();
        //查询考核配置
        LambdaQueryWrapper<LaAssessConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(LaAssessConfig::getBranchType, branchTypeEnum.getCode());
        lqw.eq(LaAssessConfig::getAssessWay, assessWayEnum.getCode());
        lqw.eq(LaAssessConfig::getAssessVersionId, assessVersionId);
        lqw.orderByAsc(LaAssessConfig::getAssessGrade);
        lqw.orderByAsc(LaAssessConfig::getCalOrder);
        List<LaAssessConfig> configList = laAssessConfigMapper.selectList(lqw);
        //没有找到配置
        if (configList.isEmpty()) {
            log.error("基本法版本:{}-渠道:{}-考核类型:{}考核配置缺失!", assessVersionId, branchTypeEnum.getCode(), assessWayEnum.getCode());
            throw new AssessConfigNotFindException("考核配置缺失，请联系管理员!");
        }
        //配置与计算条件关系
        Map<String, Long> configModeAndIdMap = new HashMap<>();
        Set<String> modeIds = new HashSet<>();
        for (LaAssessConfig laAssessConfig : configList) {
            //判断考核周期
            if (!AssessPeriodEnum.periodIsCal(AssessPeriodEnum.getEnumType(laAssessConfig.getAssessPeriod()), assessDate, laAssessConfig.getPeriodParm())) {
                continue;
            }
            modeIds.add(laAssessConfig.getCalModeId());
            assessWhenCalDto.addGradeConfig(laAssessConfig.getAssessGrade(), laAssessConfig);
            configModeAndIdMap.put(laAssessConfig.getCalModeId(), laAssessConfig.getId());
        }
        //没有配置不到考核期
        if (modeIds.isEmpty()) {
            return assessWhenCalDto;
        }
        List<LaAssessCalMode> calModeList = new ArrayList<>();
        //如果有值
        if (!modeIds.isEmpty()) {
            LambdaQueryWrapper<LaAssessCalMode> calModeQueryWrapper = Wrappers.lambdaQuery();
            calModeQueryWrapper.eq(LaAssessCalMode::getAssessVersionId, assessVersionId);
            calModeQueryWrapper.in(LaAssessCalMode::getId, modeIds);
            calModeList = laAssessCalModeMapper.selectList(calModeQueryWrapper);
        }
        //计算条件与指标关系
        Map<String, LinkedHashMap<String, String>> calModeIndexCodeMap = new HashMap<>();
        //记录计算代码
        Set<String> calIndexCodes = new HashSet<>();
        //匹配??
        Pattern pattern = Pattern.compile("\\?.*?\\?");
        //循环数据提取计算代码
        for (LaAssessCalMode laAssessCalMode : calModeList) {
            //初始化关系
            if (configModeAndIdMap.containsKey(laAssessCalMode.getId())) {
                assessWhenCalDto.addConfigCalMode(configModeAndIdMap.get(laAssessCalMode.getId()), laAssessCalMode);
            }
            Matcher matcher = pattern.matcher(laAssessCalMode.getCalSql());
            LinkedHashMap<String, String> calIndexCodeMap = new LinkedHashMap<>();
            //循环匹配
            while (matcher.find()) {
                String calCode = matcher.group().replaceAll("\\?", "");
                calIndexCodes.add(calCode);
                calIndexCodeMap.put(calCode, "");
            }
            calModeIndexCodeMap.put(laAssessCalMode.getId(), calIndexCodeMap);
        }
        List<LaAssessCalIndexConfig> calIndexConfigList = new ArrayList<>();
        if (!calIndexCodes.isEmpty()) {
            //查询计算指标
            LambdaQueryWrapper<LaAssessCalIndexConfig> calIndexConfigLambdaQueryWrapper = Wrappers.lambdaQuery();
            calIndexConfigLambdaQueryWrapper.eq(LaAssessCalIndexConfig::getAssessVersionId, assessVersionId);
            calIndexConfigLambdaQueryWrapper.in(LaAssessCalIndexConfig::getCalIndexCode, calIndexCodes);
            calIndexConfigList = laAssessCalIndexConfigMapper.selectList(calIndexConfigLambdaQueryWrapper);
        }
        final boolean[] bFind = {true};
        //获取指标配置
        Map<String, LaAssessCalIndexConfig> calIndexConfigMap = calIndexConfigList.stream().collect(Collectors.toMap(LaAssessCalIndexConfig::getCalIndexCode, calCodeConfig -> calCodeConfig));
        //循环计算条件
        calModeIndexCodeMap.forEach((calModeId, calIndexCodeMap) -> {
            //循环指标
            calIndexCodeMap.forEach((calCode, value) -> {
                //如果存在
                if (calIndexConfigMap.containsKey(calCode)) {
                    assessWhenCalDto.addCalModeIndexCode(calModeId, calIndexConfigMap.get(calCode));
                } else {
                    log.error("基本法版本:{}-渠道:{}-考核类型:{}-条件:{}计算指标配置缺失{}!", assessVersionId, branchTypeEnum.getCode(), assessWayEnum.getCode(), calModeId, calCode);
                    bFind[0] = false;
                }
            });
        });
        //检查配置
        if (calModeList.isEmpty() || calIndexConfigList.isEmpty() || !bFind[0]) {
            log.error("基本法版本:{}-渠道:{}-考核类型:{}考核配置缺失!", assessVersionId, branchTypeEnum.getCode(), assessWayEnum.getCode());
            throw new AssessConfigNotFindException("考核配置缺失，请联系管理员!");
        }
        return assessWhenCalDto;
    }

}
