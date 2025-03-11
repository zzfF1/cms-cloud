-- 1. 通知模板表
drop table if exists sys_notification_template;
CREATE TABLE sys_notification_template
(
    template_id        bigint(20)     NOT NULL                   COMMENT '模板ID',
    template_code      varchar(50)  NOT NULL COMMENT '模板代码，唯一标识',
    template_name      varchar(100) NOT NULL COMMENT '模板名称',
    type               varchar(20)  NOT NULL COMMENT '通知类型：todo-待办, alert-预警, announcement-公告',
    title_template     text         NOT NULL COMMENT '标题模板，支持变量占位符',
    content_template   text         NOT NULL COMMENT '内容模板，支持变量占位符',
    sms_template       text COMMENT '短信模板，支持变量占位符',
    email_subject      text COMMENT '邮件主题模板',
    email_content      text COMMENT '邮件内容模板，支持HTML',
    priority           varchar(10)  DEFAULT 'medium' COMMENT '优先级：high-高, medium-中, low-低',
    role_ids           varchar(500) COMMENT '角色ID列表，JSON数组格式',
    menu_perms         varchar(500) COMMENT '菜单权限列表，JSON数组格式',
    data_scope         varchar(500) COMMENT '数据权限条件，JSON格式',
    valid_days         int(4)                                    COMMENT '通知有效天数',
    actions            text COMMENT '可执行操作，JSON格式',
    permission_handler varchar(50)  DEFAULT NULL COMMENT '权限处理器类型',
    permission_config  text COMMENT '权限处理器配置',
    status             char(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_dept        bigint(20)     DEFAULT NULL               COMMENT '创建部门',
    create_by          bigint(20)     DEFAULT NULL               COMMENT '创建者',
    create_time        datetime COMMENT '创建时间',
    update_by          bigint(20)     DEFAULT NULL               COMMENT '更新者',
    update_time        datetime COMMENT '更新时间',
    remark             varchar(500) DEFAULT NULL COMMENT '备注',
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
    message_subtype varchar(20) COMMENT '消息子类型：normal-普通消息, alert-预警消息',
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
    template_id       bigint(20)     NOT NULL                   COMMENT '关联的模板ID',
    menu_id           bigint(20) COMMENT '关联菜单ID',
    menu_url          varchar(255) COMMENT '菜单URL',
    todo_type         char(1)      DEFAULT '0' COMMENT '是否为业务待办(0否1是)',
    rule_type         varchar(20)  NOT NULL COMMENT '规则类型：sql-自定义SQL, bean-自定义Bean',
    rule_text         text  NOT NULL COMMENT '规则结果文本',
    channels          varchar(100) NOT NULL COMMENT '通知渠道，JSON数组["system","sms","email"]',
    status            char(1)      DEFAULT '0' COMMENT '状态（0启用 1停用）',
    last_exec_time    datetime COMMENT '上次执行时间',
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
    alert_notify_system    char(1) DEFAULT '1' COMMENT '消息通知-系统通知开关（0关闭 1开启）',
    alert_notify_sms       char(1) DEFAULT '0' COMMENT '消息通知-短信通知开关（0关闭 1开启）',
    alert_notify_email     char(1) DEFAULT '0' COMMENT '消息通知-邮件通知开关（0关闭 1开启）',
    announce_notify_system char(1) DEFAULT '1' COMMENT '公告通知-系统通知开关（0关闭 1开启）',
    announce_notify_email  char(1) DEFAULT '0' COMMENT '公告通知-邮件通知开关（0关闭 1开启）',
    do_not_disturb_start   time COMMENT '免打扰开始时间',
    do_not_disturb_end     time COMMENT '免打扰结束时间',
    create_time            datetime COMMENT '创建时间',
    update_time            datetime COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_user ( user_id)
) ENGINE=InnoDB COMMENT='通知设置表';
