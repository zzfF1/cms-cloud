drop table if exists ldmaxno;
create table ldmaxno
(
    NoType  VARCHAR(90) not null comment '号码类型',
    NoLimit VARCHAR(20) not null comment '号码限制条件',
    MaxNo   INT(10)     not null comment '当前最大值',
    primary key (NoType, NoLimit)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='流水号';



drop table if exists todo_check_rule;
CREATE TABLE `todo_check_rule`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT,
    `name`          varchar(64) NOT NULL COMMENT '规则名称',
    `event`         varchar(64) NOT NULL COMMENT '关联事件',
    `condition_sql` text COMMENT '检查条件SQL',
    `notify_users`  varchar(512) COMMENT '待通知用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='待办规则检查表';

drop table if exists sys_notify;
CREATE TABLE `sys_notify`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知id',
    `title`          varchar(64)  DEFAULT NULL COMMENT '通知标题',
    `content`        text COMMENT '通知内容',
    `type`           tinyint(4)   DEFAULT 1 COMMENT '通知类型:1-系统通知 2-会员通知',
    `status`         tinyint(4)   DEFAULT 0 COMMENT '通知状态:0-未发送 1-已发送',
    `priority`       tinyint(4)   DEFAULT 1 COMMENT '通知优先级:1-普通 2-重要 3-紧急',
    `category`       varchar(32)  DEFAULT '' COMMENT '通知类别',
    `sender`         bigint(20)   DEFAULT 0 COMMENT '发件人',
    `sender_name`    varchar(32)  DEFAULT '' COMMENT '发件人名称',
    `receiver`       bigint(20)   DEFAULT 0 COMMENT '接收人',
    `channel`        varchar(32)  DEFAULT '' COMMENT '接收渠道:EMAIL/SMS/APP',
    `need_receipt`   tinyint(1)   DEFAULT 0 COMMENT '是否需要回执:0-不需要 1-需要',
    `receipt_status` tinyint(1)   DEFAULT 0 COMMENT '回执状态:0-未回执 1-已回执',
    `plan_send_time` datetime     DEFAULT NULL COMMENT '计划发送时间',
    `valid_time`     datetime     DEFAULT NULL COMMENT '通知过期时间',
    `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `send_time`      datetime     DEFAULT NULL COMMENT '发送时间',
    `module`         varchar(32)  DEFAULT '' COMMENT '对应模块',
    `page`           varchar(32)  DEFAULT '' COMMENT '对应页面',
    `component`      varchar(32)  DEFAULT '' COMMENT '对应组件标识',
    `params`         varchar(512) DEFAULT '' COMMENT '回调参数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='系统通知表';


drop table if exists business_help_docs;
CREATE TABLE `business_help_docs`
(
    `id`             bigint(20) NOT NULL comment '主键',
    `bus_code`       varchar(20)  DEFAULT '' comment '业务编码',
    `branch_type`    varchar(2)   DEFAULT '' comment '渠道',
    `title`          varchar(255) DEFAULT '' comment '标题',
    `content`        longtext comment '内容',
    `operator`       varchar(60)  DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60)  DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间',
    PRIMARY KEY (`id`),
    KEY              `business_help_docs_index` (`bus_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='业务帮助文档表';

drop table if exists sys_attachments;
CREATE TABLE `sys_attachments`
(
    att_id           bigint(20) AUTO_INCREMENT,
    bus_code         VARCHAR(50)  NOT NULL comment '业务类型编码',
    file_name        varchar(255) not null default '' comment '文件名',
    original_name    varchar(255) not null default '' comment '原名',
    file_suffix      varchar(10)  not null default '' comment '文件后缀名',
    file_size        bigint(18)   not null default 0 comment '文件大小',
    url              varchar(500) not null comment 'URL地址',
    service          varchar(20)  not null default 'minio' comment '服务商',
    `operator`       varchar(60)           DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60)           DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间',
    PRIMARY KEY (`att_id`),
    KEY              `sys_attachments_index` (`bus_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='附件表';

drop table if exists sys_attachment_business_data;
CREATE TABLE `sys_attachment_business_data`
(
    `id`             bigint(20) AUTO_INCREMENT comment '主键',
    bus_code         VARCHAR(50) NOT NULL comment '业务附件编码',
    bus_data_type    VARCHAR(20) default '' comment '业务类型',
    `att_id`         bigint(20)  NOT NULL comment '附件ID',
    `bus_data_id`    varchar(20) NOT NULL comment '业务ID',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间',
    PRIMARY KEY (`id`),
    KEY              `sys_attachment_business_data_index` (`att_id`),
    KEY              `sys_attachment_business_data_index1` (`bus_data_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='附件业务关联表';

drop table if exists sys_export_config;
create table sys_export_config
(
    id               bigint(20)   not null comment '主键' primary key,
    code             varchar(50)  not null comment '代码',
    name             varchar(255) not null comment '名称',
    filename         varchar(255) null default '' comment '文件名称',
    path             varchar(255) null default '' comment '路径',
    type             int(2)       null default 0 comment '类型 0-生成方 1-模板方式',
    `branch_type`    varchar(2)  DEFAULT '' comment '渠道',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='excel导出配置';

drop table if exists sys_export_config_sheet;
create table sys_export_config_sheet
(
    id               bigint(20)    not null comment '主键' primary key,
    config_id        bigint(20)    not null comment '配置id',
    sheet_index      bigint(5)          default 0 comment 'sheet索引',
    title_name       varchar(255) null default '' comment '标题名称',
    sheet_name       varchar(255) null default '' comment 'sheet名称',
    begin_row        varchar(255) null default 0 comment '起始行',
    begin_col        varchar(255) null default 0 comment '起始列',
    sql_field        text(65535)   null comment '查询sql字段',
    sql_conditions   varchar(5000) null comment 'sql条件',
    sql_group        varchar(500) null default '' comment '汇总字段',
    sql_order        varchar(1000) null default '' comment '排序字段',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='excel导出sheet配置';

drop table if exists sys_export_config_item;
create table sys_export_config_item
(
    id               bigint(20)    not null comment '主键' primary key AUTO_INCREMENT,
    sheet_id         bigint(5)          default 0 comment 'sheet索引',
    config_id        bigint(20)    not null comment '配置id',
    field            varchar(255)  not null comment '字段',
    name             varchar(255) null comment '名称',
    type             int(10)       null default 0 comment '字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号',
    disp_length      int(10)       null default 5000 comment '显示长度',
    format           varchar(255) null default '' comment '格式',
    sort             DECIMAL(4, 2) not null comment '排序',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='excel导出配置明细';

drop table if exists sys_import_config;
create table sys_import_config
(
    id               bigint(20)   not null comment '序列号' primary key,
    code             varchar(50) null default '' comment '代码',
    name             varchar(255) null comment '模板名称',
    title_names      varchar(255) null default '' comment '标题名称 多个sheet标题使用,分割',
    sheet_names      varchar(255) null default '' comment 'sheet名称 多个sheet标题使用,分割',
    branch_type      VARCHAR(2) not null comment '渠道',
    remark           varchar(50) null default '' comment '说明',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='excel导入模板';

drop table if exists sys_import_config_item;
create table sys_import_config_item
(
    id               bigint(20)    not null comment '序列号' primary key,
    config_id        bigint(20)    not null comment '配置id',
    sheet_index      bigint(5)              default 0 comment 'sheet索引',
    title            varchar(100) null     default '' comment '标题',
    field_required   bigint(1)     null     default 0 comment '字段必录',
    data_type        varchar(2) null     default '' comment '字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号',
    fill_sm          varchar(50) null     default '' comment '填写说明',
    width            int(16)       not null default 5000 comment '列宽',
    down_sel_handler varchar(50) null     default '' comment '下拉处理类',
    parameter        varchar(50) null     default '' comment '参数',
    `sort`           DECIMAL(4, 2) null     default 1 comment '显示排序',
    remark           varchar(50) null     default '' comment '备注',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='excel导入模板配置';

drop table if exists sys_notify_sql;
CREATE TABLE `sys_notify_sql`
(
    `id`             bigint(20)    NOT NULL COMMENT '配置id' primary key AUTO_INCREMENT,
    `title`          varchar(64)  DEFAULT NULL COMMENT '标题',
    `check_sql`      text COMMENT '检查SQL',
    `type`           tinyint(4)   DEFAULT 1 COMMENT '通知类型:1-站内消息 2-邮箱 3-短信',
    `priority`       tinyint(4)   DEFAULT 1 COMMENT '通知优先级:1-普通 2-重要 3-紧急',
    sort             DECIMAL(4, 2) not null comment '排序',
    `module`         varchar(32)  DEFAULT '' COMMENT '对应模块',
    `page`           varchar(32)  DEFAULT '' COMMENT '对应页面',
    `component`      varchar(32)  DEFAULT '' COMMENT '对应组件标识',
    `params`         varchar(512) DEFAULT '' COMMENT '回调参数',
    `operator`       varchar(60)  DEFAULT '' comment '创建人',
    `makedate`       date          NOT NULL,
    `maketime`       varchar(10)   NOT NULL,
    `modifyoperator` varchar(60)  DEFAULT '' comment '修改人',
    `modifydate`     date          NOT NULL,
    `modifytime`     varchar(10)   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='通知检查配置表';

drop table if exists sys_page_config;
create table sys_page_config
(
    id               bigint(20)    not null comment '主键' primary key AUTO_INCREMENT,
    code             varchar(50)   not null comment '代码',
    name             varchar(255)  not null comment '名称',
    `branch_type`    varchar(2)  DEFAULT '' comment '渠道',
    sort             DECIMAL(4, 2) not null comment '排序',
    remark           varchar(5000) null default '' comment '说明',
    involve_tab      varchar(3000) null default '' comment '涉及数据表',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='界面配置表';

drop table if exists sys_page_config_query;
create table sys_page_config_query
(
    id               bigint(20)    not null comment '主键' primary key AUTO_INCREMENT,
    page_id          bigint(20)    not null comment '配置id',
    field_type       varchar(255) null default '' comment '类型',
    field_prefix     varchar(255) null default '' comment '字段前缀',
    alias            varchar(255) null default '' comment '别名',
    field_name       varchar(255) null default '' comment '字段名',
    cond_operator    varchar(255) null default '' comment '条件',
    query_order      DECIMAL(4, 2) null default 0 comment '排序字段',
    special_code     varchar(255) null default '' comment '特殊代码',
    remark           varchar(500) null default '' comment '说明',
    type             varchar(2) null default '0' comment '类型 0:前台查询条件 1:后台追加条件 2:排序字段',
    default_value    varchar(1000) null default '' comment '默认值',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='界面查询配置';

drop table if exists sys_page_config_tab;
create table sys_page_config_tab
(
    id               bigint(20)    not null comment '主键' primary key AUTO_INCREMENT,
    page_id          bigint(20)    not null comment '配置id',
    label            varchar(255) null default '' comment '标题',
    prop             varchar(255) null default '' comment '字段名称',
    align            varchar(255) null default '' comment '对齐方式',
    width            varchar(255) null default '' comment '列宽',
    fixed            varchar(255) null default '' comment '固定方式',
    type             varchar(255) null default '' comment '列的类型',
    slot             varchar(255) null default '' comment '插槽',
    code             varchar(255) null default '' comment '格式化字段',
    name             varchar(255) null default '' comment '格式化名称',
    formatter        varchar(255) null default '' comment '自定义格式化方法',
    tooltip          varchar(5) null default '' comment '显示提示',
    sort             DECIMAL(4, 2) not null comment '排序',
    remark           varchar(500) null default '' comment '说明',
    `operator`       varchar(60) DEFAULT '' comment '创建人',
    `makedate`       date comment '创建日期',
    `maketime`       varchar(10) comment '创建时间',
    `modifyoperator` varchar(60) DEFAULT '' comment '修改人',
    `modifydate`     date comment '修改日期',
    `modifytime`     varchar(10) comment '修改时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='界面展示表格';

drop table if exists la_user_video;
create table la_user_video
(
    id             bigint not null comment '主键'
        primary key,
    branch_type    varchar(2) null comment '渠道',
    menu_id        bigint not null comment '菜单ID',
    duration       varchar(20) null comment '时长',
    description    varchar(500) null comment '说明',
    router_path    varchar(200) null comment '路由地址',
    state          varchar(2) null comment '文件上传状态',
    operator       varchar(60) default '' null comment '创建人',
    makedate       date null comment '创建日期',
    maketime       varchar(10) null comment '创建时间',
    modifyoperator varchar(60) default '' null comment '修改人',
    modifydate     date null comment '修改日期',
    modifytime     varchar(10) null comment '修改时间'
);

drop table if exists web_interface_config;
create table web_interface_config
(
    interface_code     varchar(20)  not null comment '接口编码'
        primary key,
    interface_name     varchar(50) null comment '接口名称',
    interface_type     int(2) default 0 null comment '类型 0-发送 1-接收',
    branch_type        varchar(2) null comment '渠道 1-个险 2-团险 3-银保 O-多个渠道共用',
    remark             varchar(200) null comment '备注',
    data_handler_class varchar(200) not null comment '数据处理类',
    send_handler_class varchar(200) not null comment '发送处理类'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='接口定义表';

-- 异步处理器配置表
drop table if exists async_process_config;
CREATE TABLE `async_process_config`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_code` varchar(50)  NOT NULL COMMENT '业务编码',
    `process_bean`  varchar(100) NOT NULL COMMENT '处理器Bean名称',
    `process_desc`  varchar(200) COMMENT '处理器描述',
    `step_flag`     tinyint      NOT NULL DEFAULT 0 COMMENT '是否启用步骤: 1-启用, 0-未启用',
    `create_time`   datetime     NOT NULL COMMENT '创建时间',
    `update_time`   datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asyncProcessConfig_code` (`business_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异步业务处理器配置表';

-- 步骤配置表
drop table if exists async_process_step;
CREATE TABLE `async_process_step`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_code` varchar(50)  NOT NULL COMMENT '业务编码',
    `step_code`     varchar(50)  NOT NULL COMMENT '步骤编码',
    `step_name`     varchar(100) NOT NULL COMMENT '步骤名称',
    `process_bean`  varchar(100) NOT NULL COMMENT '处理器Bean名称',
    `step_order`    int          NOT NULL COMMENT '步骤顺序',
    `status`        tinyint      NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `create_time`   datetime     NOT NULL COMMENT '创建时间',
    `update_time`   datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asyncProcessStep_step` (`business_code`, `step_code`),
    KEY             `idx_asyncProcessStep_code` (`business_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异步处理步骤配置表';

-- 异步处理队列表
drop table if exists async_process_queue;
CREATE TABLE `async_process_queue`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_code` varchar(50) NOT NULL COMMENT '业务编码',
    `business_id`   varchar(50) NOT NULL COMMENT '业务ID',
    `async_step_id` bigint               default 0 COMMENT '步骤主键 默认0没有步骤',
    `request_id`    bigint      NOT NULL COMMENT '请求参数ID',
    `status`        tinyint     NOT NULL DEFAULT 1 COMMENT '状态: 1-待处理, 2-处理中, 3-处理成功, 4-处理失败',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY             `idx_asyncProcessQueue_business` (`business_id`, `business_code`),
    KEY             `idx_asyncProcessQueue_status` (`status`),
    KEY             `idx_asyncProcessQueue_step` (`async_step_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异步处理队列表';

drop table if exists async_process_request;
CREATE TABLE `async_process_request`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_code` varchar(50) NOT NULL COMMENT '业务编码',
    `business_id`   varchar(50) NOT NULL COMMENT '业务ID',
    `parameters`    json COMMENT '参数JSON',
    PRIMARY KEY (`id`),
    KEY             `idx_asyncProcessQueue_business` (`business_id`, `business_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异步处理请求参数表';

-- 异步处理结果表
drop table if exists async_process_result;
CREATE TABLE `async_process_result`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_code` varchar(50) NOT NULL COMMENT '业务编码',
    `business_id`   varchar(50) NOT NULL COMMENT '业务ID',
    `async_step_id` bigint      NOT NULL COMMENT '步骤主键',
    `request_id`    bigint      NOT NULL COMMENT '请求参数ID',
    `status`        tinyint     NOT NULL DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
    `error_message` varchar(500) COMMENT '错误信息',
    `process_time`  datetime    NOT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY             `idx_asyncProcessResult_business` (`business_id`, `business_code`),
    KEY             `idx_asyncProcessResult_status` (`status`),
    KEY             `idx_asyncProcessResult_code` (`async_step_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异步处理结果表';


-- 1. 通知模板表
drop table if exists sys_notification_template;
CREATE TABLE sys_notification_template
(
    template_id       bigint(20)     NOT NULL                   COMMENT '模板ID',
    template_code     varchar(50)  NOT NULL COMMENT '模板代码，唯一标识',
    template_name     varchar(100) NOT NULL COMMENT '模板名称',
    type              varchar(20)  NOT NULL COMMENT '通知类型：todo-待办, alert-预警, announcement-公告',
    title_template    text         NOT NULL COMMENT '标题模板，支持变量占位符',
    content_template  text         NOT NULL COMMENT '内容模板，支持变量占位符',
    sms_template      text COMMENT '短信模板，支持变量占位符',
    email_subject     text COMMENT '邮件主题模板',
    email_content     text COMMENT '邮件内容模板，支持HTML',
    priority          varchar(10)  DEFAULT 'medium' COMMENT '优先级：high-高, medium-中, low-低',
    role_ids          varchar(500) COMMENT '角色ID列表，JSON数组格式',
    menu_perms        varchar(500) COMMENT '菜单权限列表，JSON数组格式',
    data_scope        varchar(500) COMMENT '数据权限条件，JSON格式',
    valid_days        int(4)                                    COMMENT '通知有效天数',
    actions           text COMMENT '可执行操作，JSON格式',
    permission_handler varchar(50) DEFAULT NULL COMMENT '权限处理器类型',
    permission_config text COMMENT '权限处理器配置',
    merge_strategy    varchar(20)  DEFAULT 'none' COMMENT '合并策略: none-不合并, update-更新已有, count-计数合并, list-列表合并',
    business_key_tpl  varchar(255) COMMENT '业务键模板，用于识别重复通知',
    merge_title_tpl   text COMMENT '合并后的标题模板',
    merge_content_tpl text COMMENT '合并后的内容模板',
    status            char(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_dept       bigint(20)     DEFAULT NULL               COMMENT '创建部门',
    create_by         bigint(20)     DEFAULT NULL               COMMENT '创建者',
    create_time       datetime COMMENT '创建时间',
    update_by         bigint(20)     DEFAULT NULL               COMMENT '更新者',
    update_time       datetime COMMENT '更新时间',
    remark            varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (template_id),
    UNIQUE KEY (template_code)
) ENGINE=InnoDB COMMENT='通知模板表';

-- 2. 通知主表
drop table if exists sys_notification;
CREATE TABLE sys_notification
(
    notification_id bigint(20)     NOT NULL                   COMMENT '通知ID',
    template_id     bigint(20)                                COMMENT '关联的模板ID',
    type            varchar(20)  NOT NULL COMMENT '通知类型：todo-待办, alert-预警, announcement-公告',
    title           varchar(200) NOT NULL COMMENT '通知标题',
    content         text         NOT NULL COMMENT '通知内容',
    source_type     varchar(50) COMMENT '通知来源类型，如contract, policy, performance等',
    source_id       varchar(50) COMMENT '通知来源ID，关联到业务ID',
    priority        varchar(10)  DEFAULT 'medium' COMMENT '优先级：high-高, medium-中, low-低',
    role_ids        varchar(500) COMMENT '指定接收角色ID列表，JSON数组格式',
    data_scope_sql  text COMMENT '数据范围SQL条件',
    menu_perms      varchar(500) COMMENT '所需菜单权限，JSON数组格式',
    business_key    varchar(255) COMMENT '业务键，用于识别重复通知',
    is_merged       char(1)      DEFAULT '0' COMMENT '是否为合并通知（0否 1是）',
    merged_count    int(4)         DEFAULT 1                  COMMENT '合并数量，默认为1',
    parent_id       bigint(20)                                COMMENT '父通知ID，用于合并场景',
    expiration_date datetime COMMENT '过期时间',
    actions         text COMMENT '可执行操作，JSON格式',
    attachments     text COMMENT '附件信息，JSON格式',
    business_data   text COMMENT '业务数据，JSON格式，用于动态展示或操作',
    status          char(1)      DEFAULT '0' COMMENT '通知状态（0正常 1过期 2取消）',
    create_dept     bigint(20)     DEFAULT NULL               COMMENT '创建部门',
    create_by       bigint(20)     DEFAULT NULL               COMMENT '创建者',
    create_time     datetime COMMENT '创建时间',
    update_by       bigint(20)     DEFAULT NULL               COMMENT '更新者',
    update_time     datetime COMMENT '更新时间',
    remark          varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (notification_id),
    INDEX           idx_business_key ( business_key),
    INDEX           idx_create_time ( create_time),
    INDEX           idx_source ( source_type, source_id)
) ENGINE=InnoDB COMMENT='通知主表';

-- 3. 通知接收人表
drop table if exists sys_notification_user;
CREATE TABLE sys_notification_user
(
    id              bigint(20)     NOT NULL                   COMMENT '主键ID',
    notification_id bigint(20)     NOT NULL                   COMMENT '通知ID',
    user_id         bigint(20)     NOT NULL                   COMMENT '用户ID',
    is_read         char(1) DEFAULT '0' COMMENT '是否已读（0未读 1已读）',
    read_time       datetime COMMENT '阅读时间',
    is_processed    char(1) DEFAULT '0' COMMENT '是否已处理（0未处理 1已处理）',
    process_time    datetime COMMENT '处理时间',
    process_note    varchar(500) COMMENT '处理备注',
    create_time     datetime COMMENT '创建时间',
    update_time     datetime COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_notification_user ( notification_id, user_id),
    INDEX           idx_user_read ( user_id, is_read),
    INDEX           idx_user_process ( user_id, is_processed)
) ENGINE=InnoDB COMMENT='通知接收人表';

-- 4. 通知发送记录表
drop table if exists sys_notification_delivery;
CREATE TABLE sys_notification_delivery
(
    id              bigint(20)     NOT NULL                   COMMENT '主键ID',
    notification_id bigint(20)     NOT NULL                   COMMENT '通知ID',
    user_id         bigint(20)     NOT NULL                   COMMENT '用户ID',
    channel         varchar(20) NOT NULL COMMENT '发送渠道：system-系统内通知, sms-短信, email-邮件',
    content         text COMMENT '发送内容',
    status          varchar(20) DEFAULT 'pending' COMMENT '发送状态：pending-待发送, sent-已发送, failed-发送失败',
    send_time       datetime COMMENT '发送时间',
    error_message   text COMMENT '错误信息',
    retry_count     int(4)         DEFAULT 0                  COMMENT '重试次数',
    last_retry_time datetime COMMENT '最后重试时间',
    target_address  varchar(255) COMMENT '目标地址（手机号或邮箱）',
    create_time     datetime COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX           idx_notification ( notification_id),
    INDEX           idx_user ( user_id),
    INDEX           idx_channel_status ( channel, status)
) ENGINE=InnoDB COMMENT='通知发送记录表';

-- 5. 通知规则表
drop table if exists sys_notification_rule;
CREATE TABLE sys_notification_rule
(
    rule_id           bigint(20)     NOT NULL                   COMMENT '规则ID',
    rule_name         varchar(100) NOT NULL COMMENT '规则名称',
    rule_type         varchar(20)  NOT NULL COMMENT '规则类型：schedule-定时, event-事件, condition-条件',
    template_id       bigint(20)     NOT NULL                   COMMENT '关联的模板ID',
    trigger_condition text COMMENT '触发条件，JSON格式或表达式',
    trigger_schedule  varchar(100) COMMENT 'CRON表达式，用于定时规则',
    data_source       text COMMENT '数据源配置，JSON格式',
    channels          varchar(100) NOT NULL COMMENT '通知渠道，JSON数组["system","sms","email"]',
    status            char(1)      DEFAULT '0' COMMENT '状态（0启用 1停用）',
    last_exec_time    datetime COMMENT '上次执行时间',
    create_dept       bigint(20)     DEFAULT NULL               COMMENT '创建部门',
    create_by         bigint(20)     DEFAULT NULL               COMMENT '创建者',
    create_time       datetime COMMENT '创建时间',
    update_by         bigint(20)     DEFAULT NULL               COMMENT '更新者',
    update_time       datetime COMMENT '更新时间',
    remark            varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (rule_id),
    INDEX             idx_template ( template_id)
) ENGINE=InnoDB COMMENT='通知规则表';

-- 6. 通知设置表
drop table if exists sys_notification_setting;
CREATE TABLE sys_notification_setting
(
    id                     bigint(20)    NOT NULL                   COMMENT '主键ID',
    user_id                bigint(20)    NOT NULL                   COMMENT '用户ID',
    todo_notify_system     char(1) DEFAULT '1' COMMENT '待办通知-系统通知开关（0关闭 1开启）',
    todo_notify_sms        char(1) DEFAULT '0' COMMENT '待办通知-短信通知开关（0关闭 1开启）',
    todo_notify_email      char(1) DEFAULT '0' COMMENT '待办通知-邮件通知开关（0关闭 1开启）',
    alert_notify_system    char(1) DEFAULT '1' COMMENT '预警通知-系统通知开关（0关闭 1开启）',
    alert_notify_sms       char(1) DEFAULT '0' COMMENT '预警通知-短信通知开关（0关闭 1开启）',
    alert_notify_email     char(1) DEFAULT '0' COMMENT '预警通知-邮件通知开关（0关闭 1开启）',
    announce_notify_system char(1) DEFAULT '1' COMMENT '公告通知-系统通知开关（0关闭 1开启）',
    announce_notify_email  char(1) DEFAULT '0' COMMENT '公告通知-邮件通知开关（0关闭 1开启）',
    do_not_disturb_start   time COMMENT '免打扰开始时间',
    do_not_disturb_end     time COMMENT '免打扰结束时间',
    create_time            datetime COMMENT '创建时间',
    update_time            datetime COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_user ( user_id)
) ENGINE=InnoDB COMMENT='通知设置表';

-- 7. 通知合并详情表
drop table if exists sys_notification_merge;
CREATE TABLE sys_notification_merge
(
    id            bigint(20)    NOT NULL                   COMMENT '主键ID',
    parent_id     bigint(20)    NOT NULL                   COMMENT '父通知ID',
    child_id      bigint(20)    NOT NULL                   COMMENT '子通知ID',
    merged_at     datetime NOT NULL COMMENT '合并时间',
    display_order int(4)        DEFAULT 0                  COMMENT '显示顺序',
    PRIMARY KEY (id),
    UNIQUE KEY uk_parent_child ( parent_id, child_id),
    INDEX         idx_child_id ( child_id)
) ENGINE=InnoDB COMMENT='通知合并详情表';

-- 8. 通知合并配置表
drop table if exists sys_notification_merge_config;
CREATE TABLE sys_notification_merge_config
(
    id                 bigint(20)     NOT NULL                   COMMENT '主键ID',
    template_code      varchar(50) NOT NULL COMMENT '模板代码',
    merge_within_hours int(4)         DEFAULT 24                 COMMENT '合并时间窗口（小时）',
    max_merge_count    int(4)         DEFAULT 10                 COMMENT '最大合并数量',
    merge_by_user      char(1) DEFAULT '1' COMMENT '是否按用户合并（0否 1是）',
    create_time        datetime COMMENT '创建时间',
    update_time        datetime COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code ( template_code)
) ENGINE=InnoDB COMMENT='通知合并配置表';


