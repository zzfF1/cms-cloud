package com.sinosoft.workflow.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import com.sinosoft.workflow.domain.ActHiProcinst;
import com.sinosoft.workflow.service.IActHiProcinstService;
import com.sinosoft.workflow.service.IActProcessInstanceService;
import com.sinosoft.workflow.service.WorkflowService;
import com.sinosoft.workflow.utils.WorkflowUtils;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通用 工作流服务实现
 *
 * @author may
 */
@RequiredArgsConstructor
@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired(required = false)
    private TaskService taskService;
    private final IActProcessInstanceService actProcessInstanceService;
    private final IActHiProcinstService actHiProcinstService;
    /**
     * 运行中的实例 删除程实例，删除历史记录，删除业务与流程关联信息
     *
     * @param businessKeys 业务id
     * @return 结果
     */
    @Override
    public boolean deleteRunAndHisInstance(List<String> businessKeys) {
        return actProcessInstanceService.deleteRunAndHisInstance(businessKeys);
    }

    /**
     * 获取当前流程状态
     *
     * @param taskId 任务id
     */
    @Override
    public String getBusinessStatusByTaskId(String taskId) {
        return WorkflowUtils.getBusinessStatusByTaskId(taskId);
    }

    /**
     * 获取当前流程状态
     *
     * @param businessKey 业务id
     */
    @Override
    public String getBusinessStatus(String businessKey) {
        return WorkflowUtils.getBusinessStatus(businessKey);
    }

    /**
     * 设置流程变量(全局变量)
     *
     * @param taskId       任务id
     * @param variableName 变量名称
     * @param value        变量值
     */
    @Override
    public void setVariable(String taskId, String variableName, Object value) {
        taskService.setVariable(taskId, variableName, value);
    }

    /**
     * 设置流程变量(全局变量)
     *
     * @param taskId    任务id
     * @param variables 流程变量
     */
    @Override
    public void setVariables(String taskId, Map<String, Object> variables) {
        taskService.setVariables(taskId, variables);
    }

    /**
     * 设置流程变量(本地变量,非全局变量)
     *
     * @param taskId       任务id
     * @param variableName 变量名称
     * @param value        变量值
     */
    @Override
    public void setVariableLocal(String taskId, String variableName, Object value) {
        taskService.setVariableLocal(taskId, variableName, value);
    }

    /**
     * 设置流程变量(本地变量,非全局变量)
     *
     * @param taskId    任务id
     * @param variables 流程变量
     */
    @Override
    public void setVariablesLocal(String taskId, Map<String, Object> variables) {
        taskService.setVariablesLocal(taskId, variables);
    }

    /**
     * 按照业务id查询流程实例id
     *
     * @param businessKey 业务id
     * @return 结果
     */
    @Override
    public String getInstanceIdByBusinessKey(String businessKey) {
        ActHiProcinst actHiProcinst = actHiProcinstService.selectByBusinessKey(businessKey);
        if (actHiProcinst == null) {
            return StrUtil.EMPTY;
        }
        return actHiProcinst.getId();
    }
}
