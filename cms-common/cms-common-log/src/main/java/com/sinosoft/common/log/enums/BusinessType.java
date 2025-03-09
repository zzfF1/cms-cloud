package com.sinosoft.common.log.enums;

/**
 * 业务操作类型
 *
 * @author zzf
 */
public enum BusinessType {
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 生成代码
     */
    GENCODE,

    /**
     * 清空数据
     */
    CLEAN,
    /**
     * 下载
     */
    DOWNLOAD,
    /**
     * 销定
     */
    LOCK,
    /**
     * 解锁
     */
    UNLOCK,
    /**
     * 审批
     */
    APPROVAL,
    /**
     * 计算
     */
    CALCULATE,
    /**
     * 打印
     */
    PRINT,
}
