package com.sinosoft.system.service;

import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.api.domain.bo.RemoteOperLogBo;
import com.sinosoft.system.domain.bo.SysOperLogBo;
import com.sinosoft.system.domain.bo.SysOperlogStrategyBo;
import com.sinosoft.system.domain.vo.SysOperLogStatisticVo;
import com.sinosoft.system.domain.vo.SysOperLogVo;

import java.util.List;
import java.util.Map;

/**
 * 操作日志 服务层
 *
 * @author zzf
 */
public interface ISysOperLogService {

    TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery);

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    void insertOperlog(SysOperLogBo bo);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    List<SysOperLogVo> selectOperLogList(SysOperLogBo operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    SysOperLogVo selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    void cleanOperLog();

    /**
     * 操作日志统计
     * @param bo
     * @return
     */
    List<SysOperLogStatisticVo> operLogStatistic(SysOperLogBo bo);

    /**
     * 获取日志策略配置
     * @return
     */
    Map<String,Object> obtainStrategy();

    /**
     * 编辑日志策略配置
     * @param bo
     */
    void editStrategy(SysOperlogStrategyBo bo);

    /**
     * 忽略日志记录
     * @param sysOperLog
     * @return
     */
    boolean ignoreLog(RemoteOperLogBo sysOperLog);

}
