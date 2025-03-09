package com.sinosoft.common.domain.dto;

import lombok.Data;
import com.sinosoft.common.schema.common.domain.LaAssessCalIndexConfig;
import com.sinosoft.common.schema.common.domain.LaAssessCalMode;
import com.sinosoft.common.schema.common.domain.LaAssessConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: 考核计算参数
 * @author: zzf
 * @create: 2024-01-23 09:10
 */
@Data
public class AssessWhenCalDto {
    /**
     * 职级考核配置
     */
    Map<String, List<LaAssessConfig>> gradeConfigMap;
    /**
     * 考核职级计算条件
     */
    Map<Long, LaAssessCalMode> configCalModeMap;
    /**
     * 计算条件,指标配置
     */
    Map<String, List<LaAssessCalIndexConfig>> calModeIndexCodeMap;

    /**
     * 构造函数
     */
    public AssessWhenCalDto() {
        this.gradeConfigMap = new HashMap<>();
        this.configCalModeMap = new HashMap<>();
        this.calModeIndexCodeMap = new HashMap<>();
    }

    /**
     * 添加考核配置
     *
     * @param grade  职级
     * @param config 考核配置
     */
    public void addGradeConfig(String grade, LaAssessConfig config) {
        if (config == null) {
            return;
        }
        if (this.gradeConfigMap.containsKey(grade)) {
            this.gradeConfigMap.get(grade).add(config);
        } else {
            List<LaAssessConfig> configList = new ArrayList<>();
            configList.add(config);
            this.gradeConfigMap.put(grade, configList);
        }
    }

    /**
     * 添加计算条件
     *
     * @param configId 职级考核配置主键
     * @param calMode  计算条件
     */
    public void addConfigCalMode(Long configId, LaAssessCalMode calMode) {
        if (calMode == null) {
            return;
        }
        this.configCalModeMap.put(configId, calMode);
    }

    /**
     * 添加计算条件与指标配置
     *
     * @param calModeId      计算条件主键
     * @param calIndexConfig 指标配置
     */
    public void addCalModeIndexCode(String calModeId, LaAssessCalIndexConfig calIndexConfig) {
        if (calIndexConfig == null) {
            return;
        }
        if (this.calModeIndexCodeMap.containsKey(calModeId)) {
            this.calModeIndexCodeMap.get(calModeId).add(calIndexConfig);
        } else {
            List<LaAssessCalIndexConfig> calIndexConfigList = new ArrayList<>();
            calIndexConfigList.add(calIndexConfig);
            this.calModeIndexCodeMap.put(calModeId, calIndexConfigList);
        }
    }

}
