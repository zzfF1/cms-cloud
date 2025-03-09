-- 探查表配置：存储源表或源SQL与目标表的映射
DROP TABLE IF EXISTS exploration_table_mapping;
CREATE TABLE exploration_table_mapping
(
    id              BIGINT PRIMARY KEY,
    source_type     TINYINT     NOT NULL COMMENT '源类型：1-表名，2-SQL语句',
    source_table    VARCHAR(64) NOT NULL COMMENT '源表名',
    source_comment  VARCHAR(200)         default '' COMMENT '源表注释',
    source_content  TEXT COMMENT 'SQL语句',
    source_alias    VARCHAR(64) COMMENT 'SQL语句时的别名',
    data_source     VARCHAR(20) NOT NULL COMMENT '源数据',
    source_db_type  VARCHAR(20) NOT NULL COMMENT '源数据库类型',
    module_name     VARCHAR(50) NOT NULL COMMENT '模块名称',
    where_condition TEXT COMMENT '数据过滤条件',
    status          TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_dept     bigint(20) comment '创建部门',
    create_by       BIGINT(20) COMMENT '创建者',
    create_time     DATETIME COMMENT '创建时间',
    update_by       BIGINT(20) COMMENT '更新者',
    update_time     DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_exploration_table_mapping (source_table),
    KEY             idx_exploration_source_table (source_table)
) COMMENT '探查表配置表';

-- 探查字段配置：存储源表字段与目标表字段的映射关系
DROP TABLE IF EXISTS exploration_field_mapping;
CREATE TABLE exploration_field_mapping
(
    id                BIGINT PRIMARY KEY,
    exploration_id    BIGINT      NOT NULL COMMENT '关联的探查表配置ID',
    source_field      VARCHAR(64) NOT NULL COMMENT '源字段名',
    source_field_type VARCHAR(50) COMMENT '源字段类型',
    is_required       TINYINT     NOT NULL DEFAULT 0 COMMENT '是否必填：0-否，1-是',
    is_primary_key    TINYINT     NOT NULL DEFAULT 0 COMMENT '是否主键：0-否，1-是',
    column_comment    VARCHAR(500) COMMENT '字段注释',
    sort              INT COMMENT '排序',
    create_dept       bigint(20) comment '创建部门',
    create_by         BIGINT(20) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         BIGINT(20) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_exploration_field_mapping (exploration_id, source_field),
    KEY               idx_exploration_table_mapping_id (exploration_id)
) COMMENT '探查字段配置表';

-- 数据质量规则表
DROP TABLE IF EXISTS exploration_quality_rule;
CREATE TABLE exploration_quality_rule
(
    id          BIGINT PRIMARY KEY,
    rule_name   VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type   TINYINT      NOT NULL COMMENT '规则类型：1-空值检查，2-重复值检查，3-值域检查，4-格式检查',
    rule_params TEXT COMMENT '规则参数(JSON格式)',
    severity    TINYINT      NOT NULL COMMENT '严重程度：1-提示，2-警告，3-错误',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_dept bigint(20) comment '创建部门',
    create_by   BIGINT(20) COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by   BIGINT(20) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT '数据质量规则表';

-- 字段质量规则关联表
DROP TABLE IF EXISTS exploration_field_rule;
CREATE TABLE exploration_field_rule
(
    id              BIGINT PRIMARY KEY,
    field_id        BIGINT NOT NULL COMMENT '探查字段ID',
    rule_id         BIGINT NOT NULL COMMENT '质量规则ID',
    threshold_value VARCHAR(500) COMMENT '阈值',
    create_dept     bigint(20) comment '创建部门',
    create_by       BIGINT(20) COMMENT '创建者',
    create_time     DATETIME COMMENT '创建时间',
    update_by       BIGINT(20) COMMENT '更新者',
    update_time     DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_field_rule (field_id, rule_id)
) COMMENT '字段质量规则关联表';

-- 迁移方案表：存储数据迁移的整体方案
drop table if exists migration_plan;
CREATE TABLE migration_plan
(
    id                BIGINT PRIMARY KEY,
    plan_name         VARCHAR(100) NOT NULL COMMENT '方案名称',
    source_db         VARCHAR(50)  NOT NULL COMMENT '源数据库',
    target_db         VARCHAR(50)  NOT NULL COMMENT '目标数据库',
    description       TEXT COMMENT '方案描述',
    last_execute_time DATETIME COMMENT '上次执行时间',
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_dept       bigint(20) comment '创建部门',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间',
    UNIQUE KEY uk_plan_name (plan_name)
) COMMENT '数据迁移方案表';

DROP TABLE IF EXISTS migration_table_mapping;
CREATE TABLE migration_table_mapping
(
    id                BIGINT PRIMARY KEY,
    source_table      VARCHAR(255) NOT NULL COMMENT '源表名',
    source_db_type    VARCHAR(20)  NOT NULL COMMENT '源数据库类型',
    source_comment    VARCHAR(200)          default '' COMMENT '源表注释',
    module_name       VARCHAR(50)  NOT NULL COMMENT '模块名称',
    data_source       VARCHAR(20)  NOT NULL COMMENT '源数据',
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_execute_date DATETIME COMMENT '上次执行日期',
    index_json        TEXT COMMENT '索引对象',
    create_dept       bigint(20) comment '创建部门',
    create_by         BIGINT(20) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         BIGINT(20) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_migration_table_mapping (source_table),
    KEY               idx_migration_source_table (source_table)
) COMMENT '迁移表配置表';


DROP TABLE IF EXISTS migration_field_mapping;
CREATE TABLE migration_field_mapping
(
    id                BIGINT PRIMARY KEY,
    table_mapping_id  BIGINT       NOT NULL COMMENT '关联的迁移表ID',
    source_field      VARCHAR(255) NOT NULL COMMENT '源字段名',
    source_field_type VARCHAR(100) COMMENT '源字段类型',
    is_required       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否必填：0-否，1-是',
    is_primary_key    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否主键：0-否，1-是',
    column_comment    VARCHAR(500) COMMENT '字段注释',
    sort              INT COMMENT '排序',
    create_dept       bigint(20) comment '创建部门',
    create_by         BIGINT(20) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         BIGINT(20) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_migration_field_mapping (table_mapping_id, source_field),
    KEY               idx_migration_table_mapping_id (table_mapping_id)
) COMMENT '迁移字段配置表';


-- 迁移配置表：用于配置探查表到迁移表的映射关系
DROP TABLE IF EXISTS migration_config;
CREATE TABLE migration_config
(
    id                BIGINT PRIMARY KEY,
    plan_id           BIGINT       NOT NULL COMMENT '关联的方案ID',
    exploration_id    BIGINT       NOT NULL COMMENT '关联的探查表ID',
    exploration_table VARCHAR(255)          default '' COMMENT '关联的探查表',
    migration_id      BIGINT       NOT NULL COMMENT '关联的迁移表ID',
    migration_table   VARCHAR(255) NOT NULL COMMENT '关联的迁移表',
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-待确认, 2-已确认',
    last_execute_date DATETIME COMMENT '上次执行日期',
    create_dept       bigint(20) comment '创建部门',
    create_by         BIGINT(20) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         BIGINT(20) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_config_mapping (plan_id, exploration_id, migration_id),
    KEY               idx_migration_plan_id (plan_id),
    KEY               idx_exploration_id (exploration_id),
    KEY               idx_migration_id (migration_id)
) COMMENT '迁移配置表';

-- 迁移配置字段表：用于配置字段级别的映射关系
DROP TABLE IF EXISTS migration_config_field;
CREATE TABLE migration_config_field
(
    id                   BIGINT PRIMARY KEY,
    config_id            BIGINT  NOT NULL COMMENT '关联的迁移配置ID',
    exploration_field_id BIGINT  NOT NULL COMMENT '关联的探查字段ID',
    migration_field_id   BIGINT  NOT NULL COMMENT '关联的迁移字段ID',
    transform_rule       TEXT COMMENT '转换规则',
    default_value        VARCHAR(255) COMMENT '默认值',
    status               TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_dept          bigint(20) comment '创建部门',
    create_by            BIGINT(20) COMMENT '创建者',
    create_time          DATETIME COMMENT '创建时间',
    update_by            BIGINT(20) COMMENT '更新者',
    update_time          DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_config_field (config_id, exploration_field_id, migration_field_id),
    KEY                  idx_migration_config_id (config_id),
    KEY                  idx_exploration_field_id (exploration_field_id),
    KEY                  idx_migration_field_id (migration_field_id)
) COMMENT '迁移配置字段表';

-- 主键映射记录：记录源表和目标表的主键对应关系，用于增量更新和删除判断
drop table if exists migration_primary_key_mapping;
CREATE TABLE migration_primary_key_mapping
(
    id                        BIGINT PRIMARY KEY,
    table_mapping_id          BIGINT       NOT NULL COMMENT '关联的表映射ID',
    batch_no                  VARCHAR(32)  NOT NULL COMMENT '迁移批次号',
    source_primary_key_values VARCHAR(200) NOT NULL COMMENT '源表主键值',
    target_primary_key_value  VARCHAR(100) NOT NULL COMMENT '目标表主键值',
    row_status                TINYINT      NOT NULL COMMENT '状态：0-待新增,1-新增成功',
    create_time               datetime comment '创建时间',
    update_time               datetime comment '更新时间',
    KEY                       idx_target_pk (target_primary_key_value),
    KEY                       idx_source_pk (table_mapping_id,source_primary_key_values)
) COMMENT '主键映射记录表';

-- 迁移执行日志：记录每次迁移任务的执行情况和统计信息
drop table if exists migration_execution_log;
CREATE TABLE migration_execution_log
(
    id               BIGINT PRIMARY KEY,
    task_id          BIGINT      NOT NULL COMMENT '关联的任务ID',
    table_mapping_id BIGINT      NOT NULL COMMENT '关联的表映射ID',
    batch_no         VARCHAR(32) NOT NULL COMMENT '迁移批次号',
    execution_type   TINYINT     NOT NULL COMMENT '执行类型：1-全量，2-增量',
    start_time       DATETIME    NOT NULL COMMENT '开始时间',
    end_time         DATETIME COMMENT '结束时间',
    total_count      INT         NOT NULL DEFAULT 0 COMMENT '处理总记录数',
    insert_count     INT         NOT NULL DEFAULT 0 COMMENT '插入记录数',
    update_count     INT         NOT NULL DEFAULT 0 COMMENT '更新记录数',
    delete_count     INT         NOT NULL DEFAULT 0 COMMENT '删除记录数',
    fail_count       INT         NOT NULL DEFAULT 0 COMMENT '失败记录数',
    error_message    TEXT COMMENT '错误信息',
    status           TINYINT     NOT NULL COMMENT '状态：0-进行中，1-成功，2-失败',
    create_dept      bigint(20) comment '创建部门',
    create_by        bigint(20) comment '创建者',
    create_time      datetime comment '创建时间',
    update_by        bigint(20) comment '更新者',
    update_time      datetime comment '更新时间',
    KEY              idx_task_id (task_id),
    KEY              idx_table_mapping_id (table_mapping_id),
    KEY              idx_batch_no (batch_no)
) COMMENT '迁移执行日志表';

-- 迁移失败记录表：记录每条失败的数据详情
drop table if exists migration_fail_record;
CREATE TABLE migration_fail_record
(
    id               BIGINT PRIMARY KEY,
    execution_log_id BIGINT       NOT NULL COMMENT '关联的执行日志ID',
    table_mapping_id BIGINT       NOT NULL COMMENT '关联的表映射ID',
    batch_no         VARCHAR(32)  NOT NULL COMMENT '迁移批次号',
    new_id           VARCHAR(500) COMMENT '新数据ID',
    source_data      MEDIUMTEXT COMMENT '数据JSON',
    error_message    MEDIUMTEXT   NOT NULL COMMENT '错误信息',
    error_type       VARCHAR(100) NOT NULL COMMENT '错误类型：type_convert-类型转换失败，rule_transform-规则转换失败，required_null-必填为空，insert_error-插入失败，update_error-更新失败',
    create_dept      bigint(20) comment '创建部门',
    create_by        bigint(20) comment '创建者',
    create_time      datetime comment '创建时间',
    update_by        bigint(20) comment '更新者',
    update_time      datetime comment '更新时间',
    KEY              idx_execution_log_id (execution_log_id),
    KEY              idx_table_mapping_id (table_mapping_id),
    KEY              idx_batch_no (batch_no),
    KEY              idx_error_type (error_type)
) COMMENT '迁移失败记录表';
