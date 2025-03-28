package com.sinosoft.common.sync.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务同步适配器接口
 * 用于适配不同的同步服务实现，解决模块间依赖问题
 */
public interface ServiceSyncAdapter {
    /**
     * 同步人员基础信息
     */
    SyncResult syncAgentData(List<?> agentList, String businessCode);

    /**
     * 同步人员职级信息
     */
    SyncResult syncTreeData(List<?> treeList, String businessCode);

    /**
     * 同步销售机构信息
     */
    SyncResult syncBranchData(List<?> branchList, String businessCode);
    /**
     * 同步结果
     */
    @Data
    @AllArgsConstructor
    class SyncResult {
        private boolean success;
        private String message;
        private String resultData;

        public SyncResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * 空实现（默认不做任何同步）
     */
    @Slf4j
    class NoOpAdapter implements ServiceSyncAdapter {
        @Override
        public SyncResult syncAgentData(List<?> agentList, String businessCode) {
            log.warn("使用默认空实现，未配置实际同步适配器。数据类型: Agent, 数据数量: {}", agentList != null ? agentList.size() : 0);
            return new SyncResult(false, "No sync adapter configured");
        }

        @Override
        public SyncResult syncTreeData(List<?> treeList, String businessCode) {
            log.warn("使用默认空实现，未配置实际同步适配器。数据类型: Tree, 数据数量: {}", treeList != null ? treeList.size() : 0);
            return new SyncResult(false, "No sync adapter configured");
        }

        @Override
        public SyncResult syncBranchData(List<?> branchList, String businessCode) {
            log.warn("使用默认空实现，未配置实际同步适配器。数据类型: Branch, 数据数量: {}", branchList != null ? branchList.size() : 0);
            return new SyncResult(false, "No sync adapter configured");
        }
    }
}
