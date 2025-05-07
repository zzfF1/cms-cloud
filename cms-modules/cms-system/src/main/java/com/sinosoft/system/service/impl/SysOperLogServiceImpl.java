package com.sinosoft.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.dict.vo.CodeNameVo;
import com.sinosoft.common.log.config.OperLogConfig;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.system.api.domain.bo.RemoteOperLogBo;
import com.sinosoft.system.config.OperlogStrategConfig;
import com.sinosoft.system.domain.bo.SysDictDataBo;
import com.sinosoft.system.domain.bo.SysOperlogStrategyBo;
import com.sinosoft.system.domain.vo.SysDictDataVo;
import com.sinosoft.system.domain.vo.SysOperLogStatisticVo;
import com.sinosoft.system.domain.vo.SysOperlogStrategyVo;
import com.sinosoft.system.mapper.SysPostMapper;
import com.sinosoft.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.SysOperLog;
import com.sinosoft.system.domain.bo.SysOperLogBo;
import com.sinosoft.system.domain.vo.SysOperLogVo;
import com.sinosoft.system.mapper.SysOperLogMapper;
import com.sinosoft.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

/**
 * 操作日志 服务层处理
 *
 * @author zzf
 */
@RequiredArgsConstructor
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper baseMapper;
    private final SysDictDataServiceImpl sysDictDataServiceImpl;
    private final ISysRoleService sysRoleService;
    private final SysPostMapper sysPostMapper;
    private final OperlogStrategConfig operlogStrategConfig;
    private final OperLogConfig operLogConfig;
    private final NacosConfigManager nacosConfigManager;
    private final NacosConfigProperties nacosConfigProperties;


    @Override
    public TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOperLog> lqw = buildQueryWrapper(operLog);
        if (StringUtils.isBlank(pageQuery.getOrderByColumn())) {
            lqw.orderByDesc(SysOperLog::getOperId);
        }
        Page<SysOperLogVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(SysOperLogBo operLog) {
        Map<String, Object> params = operLog.getParams();
        return new LambdaQueryWrapper<SysOperLog>()
            .like(StringUtils.isNotBlank(operLog.getOperIp()), SysOperLog::getOperIp, operLog.getOperIp())
            .like(StringUtils.isNotBlank(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
            .eq(operLog.getBusinessType() != null && operLog.getBusinessType() > 0,
                SysOperLog::getBusinessType, operLog.getBusinessType())
            .func(f -> {
                if (ArrayUtil.isNotEmpty(operLog.getBusinessTypes())) {
                    f.in(SysOperLog::getBusinessType, Arrays.asList(operLog.getBusinessTypes()));
                }
            })
            .eq(operLog.getStatus() != null,
                SysOperLog::getStatus, operLog.getStatus())
            .like(StringUtils.isNotBlank(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                SysOperLog::getOperTime, params.get("beginTime"), params.get("endTime"));
    }

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLogBo bo) {
        SysOperLog operLog = MapstructUtils.convert(bo, SysOperLog.class);
        operLog.setOperTime(new Date());
        baseMapper.insert(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogVo> selectOperLogList(SysOperLogBo operLog) {
        LambdaQueryWrapper<SysOperLog> lqw = buildQueryWrapper(operLog);
        return baseMapper.selectVoList(lqw.orderByDesc(SysOperLog::getOperId));
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return baseMapper.deleteByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLogVo selectOperLogById(Long operId) {
        return baseMapper.selectVoById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    public List<SysOperLogStatisticVo> operLogStatistic(SysOperLogBo bo) {
        return baseMapper.operLogStatistic(bo);
    }

    @Override
    public Map<String, Object> obtainStrategy() {
        Map<String, Object> result = new HashMap<String, Object>();
        //获取全部操作类型
        SysDictDataBo bo = new SysDictDataBo();
        bo.setDictType("sys_oper_type");
        List<SysDictDataVo> sysDictDataVos = sysDictDataServiceImpl.selectDictDataList(bo);
        List<CodeNameVo> typeAll = new ArrayList<>();
        for (SysDictDataVo dataVo : sysDictDataVos) {
            if (BusinessType.isShow(Integer.valueOf(dataVo.getDictValue()))) {
                CodeNameVo codeNameVo = new CodeNameVo();
                codeNameVo.setValue(dataVo.getDictValue());
                codeNameVo.setLabel(dataVo.getDictLabel());
                typeAll.add(codeNameVo);
            }
        }
        SysOperlogStrategyVo vo = new SysOperlogStrategyVo();
        if (CollUtil.isNotEmpty(operlogStrategConfig.getDate())) {
            vo.setDate(operlogStrategConfig.getDate());
        }
        if (CollUtil.isNotEmpty(operlogStrategConfig.getRoles())) {
            vo.setRoles(operlogStrategConfig.getRoles());
        }
        if (CollUtil.isNotEmpty(operlogStrategConfig.getTypes())) {
            vo.setTypes(operlogStrategConfig.getTypes());
        }
        result.put("config", vo);
        result.put("typeAll", typeAll);
        result.put("roleAll", sysPostMapper.listDistinctPostCodeName());
        return result;
    }

    @Override
    public void editStrategy(SysOperlogStrategyBo bo) {
        try {
            ConfigService configService = nacosConfigManager.getConfigService();
            String defaultGroup = configService.getConfig("log-strategy.yml", nacosConfigProperties.getGroup(), 1000);
            Yaml yaml = new Yaml();
            Map<String, Object> load = yaml.load(defaultGroup);
            Map<String, Object> strategy = (Map<String, Object>) load.get("oper-log-strategy");
            strategy.remove("types");
            strategy.remove("roles");
            strategy.remove("date");
            strategy.put("types", bo.getTypes());
            strategy.put("roles", bo.getRoles());
            String beginTime = (String) bo.getParams().get("beginTime");
            String endTime = (String) bo.getParams().get("endTime");
            if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
                List<String> date = new ArrayList<>();
                date.add(beginTime);
                date.add(endTime);
                strategy.put("date", date);
            } else {
                strategy.put("date", new ArrayList<>());
            }
            configService.publishConfig("log-strategy.yml", nacosConfigProperties.getGroup(), yaml.dump(load));
        } catch (NacosException e) {
            throw new ServiceException("更新失败 " + e.getMessage());
        }
    }

    @Override
    public boolean ignoreLog(RemoteOperLogBo sysOperLog) {
        if (operLogConfig.getEnable()) {
            boolean flag = false;
            //判断忽略身份
            if (CollUtil.isNotEmpty(operlogStrategConfig.getRoles()) && StringUtils.isNotBlank(sysOperLog.getPostCode())) {
                if (!operlogStrategConfig.getRoles().contains(sysOperLog.getPostCode())) {
                    return false;
                } else {
                    flag = true;
                }
            }
            //判断忽略操作类型
            if (CollUtil.isNotEmpty(operlogStrategConfig.getTypes())) {
                if (!operlogStrategConfig.getTypes().contains(String.valueOf(sysOperLog.getBusinessType()))) {
                    return false;
                } else {
                    flag = true;
                }
            }
            //判断忽略时间
            if (CollUtil.isNotEmpty(operlogStrategConfig.getDate())) {
                String time = DateUtils.getTime();
                if (time.compareTo(operlogStrategConfig.getDate().get(0)) < 0 || time.compareTo(operlogStrategConfig.getDate().get(1)) > 0) {
                    return false;
                } else {
                    flag = true;
                }
            }
            return flag;
        }
        return false;
    }
}
