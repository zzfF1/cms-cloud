package com.sinosoft.common.service;


import com.sinosoft.common.domain.*;
import com.sinosoft.common.enums.LcCzTypeEnum;
import com.sinosoft.common.enums.LcTypeSerNoEnum;
import com.sinosoft.common.event.LcProcTrackEvent;
import com.sinosoft.common.exception.FlowException;

import java.util.List;

/**
 * 流程类型定义Service接口
 *
 * @author zzf
 * @date 2023-07-02
 */
public interface ILcMainService {
    /**
     * 查询流程类型定义
     *
     * @param serialno 主键
     * @return 流程类型定义
     */
    LcMain selectLcMainById(Long serialno);

    /**
     * 查询流程
     *
     * @param serialno 主键
     * @return 流程
     */
    LcDefine selectLcDefineById(Long serialno);

    /**
     * 获取数据当前流程节点
     *
     * @param querySql 查询SQL
     * @return 流程节点
     */
    List<String> getDataCurLcId(String querySql);

    /**
     * 根据数据流程节点获取数据ID
     *
     * @param lcId    流程ID
     * @param table   数据库表名
     * @param idField 主键字段
     * @param lcField 流程字段
     * @param tj      查询条件
     * @param subIds  提交数据主键
     * @return 符合条件主键集合
     */
    List<String> getDataIdByLcIdAndTj(Integer lcId, String table, String idField, String lcField, String tj, String subIds);

    /**
     * 修改数据流程节点
     *
     * @param lcId    新流程ID
     * @param oldLcId 旧流程ID
     * @param lcMain  流程配置
     * @param nCzType 操作类型
     * @param ids     id集合
     * @param msg 原因
     * @return 受影响行数
     */
    Integer updateDataLc(Integer lcId, Integer oldLcId, LcMain lcMain, LcCzTypeEnum nCzType, List<String> ids,String msg) throws FlowException;

    /**
     * 更新流程轨迹last_flag
     *
     * @param lcId       流程ID
     * @param lcSerialNo 流程类型主键
     * @param nNextLcId  下一流程ID
     * @param bSave      true保存 false非保存
     * @param ids        流程轨迹主键集合
     * @return 受影响行数
     */
    Integer updateLcProcess(Integer lcId, LcTypeSerNoEnum lcSerialNo, Integer nNextLcId, boolean bSave, List<String> ids);

    /**
     * 流程轨迹插入
     *
     * @param trackEvent 轨迹事件对象
     */
    void insertLcProcess(LcProcTrackEvent trackEvent);

    /**
     * 流程动作中执行检查
     *
     * @param sql 检查SQL
     * @return 结果
     */
    List<String> execSqlSelect(String sql);

    /**
     * 流程动作执行更新
     *
     * @param sql SQL语句
     * @return 受影响行数
     */
    Integer execSqlUpdate(String sql);

    /**
     * 查询流程检查
     *
     * @param lcId 流程ID
     * @param type 类型 1:退回 2:提交
     * @return 流程检查集合
     */
    List<LcCheck> getLcCheckListByLcId(Integer lcId, int type);

    /**
     * 获取流程动作
     *
     * @param lcId 流程ID
     * @param type 类型 1:退回 2:提交
     * @return 动作集合
     */
    List<LcDz> getLcDzListByLcId(Integer lcId, int type);

    /**
     * 获取流程跳转
     *
     * @param lcId 流程ID
     * @param isTh 是否退回
     * @return 流程跳转集合
     */
    List<LcTz> getLcTzListByLcId(Integer lcId, boolean isTh);

    /**
     * 获取上个节点或下个节点
     *
     * @param type     true退回false提交
     * @param lcMain   流程类型对象
     * @param lcDefine 当前流程对象
     * @return -1失败 非-1正常
     */
    Integer getLcPreviousOrTheNext(boolean type, LcMain lcMain, LcDefine lcDefine);

    /**
     * 获取首个流程
     *
     * @param lcSerialno 流程类型
     * @return -1没有找到
     */
    Integer getFirstLcByType(Integer lcSerialno);

    List<Integer> selectLcPropertyByAttr(String attrName, String val);
}
