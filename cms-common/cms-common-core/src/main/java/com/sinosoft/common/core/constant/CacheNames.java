package com.sinosoft.common.core.constant;

/**
 * 缓存组名称常量
 * <p>
 * key 格式为 cacheNames#ttl#maxIdleTime#maxSize
 * <p>
 * ttl 过期时间 如果设置为0则不过期 默认为0
 * maxIdleTime 最大空闲时间 根据LRU算法清理空闲数据 如果设置为0则不检测 默认为0
 * maxSize 组最大长度 根据LRU算法清理溢出数据 如果设置为0则无限长 默认为0
 * <p>
 * 例子: test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500
 *
 * @author zzf
 */
public interface CacheNames {

    /**
     * 演示案例
     */
    String DEMO_CACHE = "demo:cache#60s#10m#20";

    /**
     * 系统配置
     */
    String SYS_CONFIG = "sys_config";

    /**
     * 数据字典
     */
    String SYS_DICT = "sys_dict";

    /**
     * 租户
     */
    String SYS_TENANT = GlobalConstants.GLOBAL_REDIS_KEY + "sys_tenant#30d";

    /**
     * 客户端
     */
    String SYS_CLIENT = GlobalConstants.GLOBAL_REDIS_KEY + "sys_client#30d";

    /**
     * 用户账户
     */
    String SYS_USER_NAME = "sys_user_name#30d";

    /**
     * 用户名称
     */
    String SYS_NICKNAME = "sys_nickname#30d";

    /**
     * 部门
     */
    String SYS_DEPT = "sys_dept#30d";

    /**
     * OSS内容
     */
    String SYS_OSS = "sys_oss#30d";

    /**
     * OSS配置
     */
    String SYS_OSS_CONFIG = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss_config";

    /**
     * 在线用户
     */
    String ONLINE_TOKEN = "online_tokens";

    /**
     * 缓存客户端配置
     */
    String SYS_CLIENT_CONFIG = "cache:sys_client_config";
    /**
     * 缓存客户端权限
     */
    String SYS_CLIENT_API_PERMISSION = "cache:sys_client_api_permission";
    /**
     * 业务员姓名缓存
     */
    String AGENT_NAME = "cache:agent_name#60s";
    /**
     * 销售机构编码+名称缓存
     */
    String BRANCH = "cache:branch#60s";
    /**
     * 管理机构编码缓存
     */
    String MANAGE_NAME = "cache:manage_name#60s";
    /**
     * 职级名称缓存
     */
    String GRADE_NAME = "cache:grade_name#60s";
    /**
     * 中介机构编码缓存
     */
    String AGENT_COM_NAME = "cache:agent_com_name#60s";
    /**
     * 险种名称
     */
    String RISK_NAME = "cache:risk_name#60s";
    /**
     * 界面配置对象
     */
    String PAGE_CONFIG = "cache:sys_page:config";
    /**
     * 界面配置对象查询
     */
    String PAGE_CONFIG_QUERY = "cache:sys_page:query";
    /**
     * 界面配置对象列表
     */
    String PAGE_CONFIG_TABLE = "cache:sys_page:table";
    /**
     * 导出配置对象
     */
    String SYS_EXPORT_CONFIG = "cache:sys_export:config";
    /**
     * 导出sheet配置
     */
    String SYS_EXPORT_SHEET = "cache:sys_export:sheet";
    /**
     * 导出item配置
     */
    String SYS_EXPORT_ITEM = "cache:sys_export:item";

    /**
     * 导入配置对象
     */
    String SYS_IMPORT_CONFIG = "cache:sys_import:config";
    /**
     * 导入item配置
     */
    String SYS_IMPORT_ITEM = "cache:sys_import:item";
}
