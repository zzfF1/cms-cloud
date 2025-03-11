package com.sinosoft.notice.service;

import com.sinosoft.notice.domain.dto.TodoResultDTO;

import java.util.List;

/**
 * 业务待办服务接口
 */
public interface ITodoService {
    /**
     * 获取用户所有待办
     * @param userId 用户ID
     * @return 待办结果列表
     */
    List<TodoResultDTO> getUserTodoList(Long userId);
}
