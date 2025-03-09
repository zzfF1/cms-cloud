-- 手续费相关表
drop table if exists la_incentive_index;
create table la_incentive_index
(
    series_no                 bigint(20)  not null primary key comment '主键',
    manage_com                varchar(20) null comment '管理机构',
    agent_com                 varchar(20) null comment '中介机构',
    branch_type               varchar(2) not null comment '渠道',
    charge_type               varchar(2) not null comment '手续费类型',
    frequence                 varchar(2) null comment '发放类型',
    incentive_no              varchar(20) null comment '激励编号',
    incentive_name            varchar(30) null comment '激励名称',
    incentive_start_date      date null comment '激励开始日期',
    incentive_end_date        date null comment '激励结束日期',
    incentive_available_state varchar(2) null comment '方案状态',
    incentive_cal_state       varchar(2) null comment '',
    award_type                varchar(2) null comment '奖项',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    key                       `incentive_index` (`agent_com`),
    key                       `incentive_index1` (`manage_com`),
    key                       `incentive_index2` (`branch_type`)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='激励方案配置';

drop table if exists la_incentive_rule;
create table la_incentive_rule
(
    series_no         bigint(20)   not null primary key comment '主键',
    index_series_no   bigint(20)  not null comment '激励方案主键',
    branch_type       varchar(2) not null comment '渠道',
    rule_no           varchar(10) null comment '规则代码',
    rule_index        varchar(10) null comment '规则索引',
    rule_name         varchar(30) null comment '规则名称',
    rule_level        varchar(2) null comment '',
    rule_remark       varchar(100) null comment '规则说明',
    radix_type        varchar(2) null comment '规则结果元素',
    charge_radix_type varchar(20) null comment '规则结果类型',
    radix_value       varchar(20) null comment '规则结果',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    key               `incentive_rule_index` (`index_series_no`)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='激励方案规则配置';

drop table if exists la_incentive_radix;
create table la_incentive_radix
(
    series_no       bigint(20)    not null primary key comment '主键',
    rule_series_no  bigint(20)   not null comment '规则主键',
    branch_type     varchar(2) not null comment '渠道',
    radix_lbracket  varchar(10) null default '' comment '左括号',
    radix_type      varchar(2) null comment '元素类型',
    radix_signal    varchar(6) null comment '元素符号',
    radix_value     varchar(5000) null comment '元素值',
    radix_rbracket  varchar(10) null default '' comment '右括号',
    radix_condition varchar(6) null comment '元素条件 且 或',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    key             `incentive_radix_index` (`rule_series_no`)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='激励方案元素配置';

drop table if exists la_incentive_charge_grp;
create table la_incentive_charge_grp
(
    series_no                  bigint(20)     not null primary key comment '主键',
    manage_com                 varchar(10) null comment '管理机构',
    agent_com                  varchar(20) null comment '中介机构',
    charge_type                varchar(4) null comment '手续费类型',
    commision_sn               varchar(20) null comment '业绩流水号',
    wage_no                    varchar(20) null comment '佣金年月',
    cal_start_date             date null comment '计算起期',
    cal_date                   date null comment '计算日期',
    cal_end_date               date null comment '计算止期',
    receipt_no                 varchar(20) null comment '交易号',
    grp_cont_no                varchar(20) null comment '集体合同号码',
    grp_pol_no                 varchar(20) null comment '集体保单号码',
    cont_no                    varchar(20) null comment '保单号',
    pol_no                     varchar(20) null comment '投保单号',
    agent_code                 varchar(20) null comment '业务员工号',
    agent_code1                varchar(20) null comment '第三主人员工号',
    risk_code                  varchar(15) null comment '险种代码',
    risk_version               varchar(15) null comment '险种年份',
    risk_mark                  varchar(3) null comment '主附险',
    trans_type                 varchar(3) null comment '交易类别',
    years_flag                 varchar(3) null comment '缴费类型',
    pay_intv                   varchar(3) null comment '缴费间隔',
    pay_years                  varchar(4) null comment '缴费年期',
    pay_count                  varchar(4) null comment '保单年度',
    years                      varchar(3) null comment '保障期限',
    trans_money                decimal(16, 4) null comment '保费',
    calculate_base             decimal(16, 4) null comment '计算基数',
    charge_rate                decimal(16, 4) null comment '手续费比例',
    charge                     decimal(16, 4) null comment '手续费金额',
    radix_value                decimal(16, 4) null comment '元素类型',
    charge_state               varchar(4) null comment '手续费状态',
    branch_type                varchar(4) not null comment '渠道',
    branch_type2               varchar(4) null comment '子渠道',
    charge_rate_series_no      bigint(20)   null comment '方案主键',
    charge_rate_rule_series_no bigint(20)    null comment '规则主键',
    input_tax                  decimal(16, 4) null comment '进项税',
    input_tax_rate             decimal(8, 4) null comment '进项税率',
    sign_date                  date null comment '承保日期',
    get_pol_date               date null comment '回执回销日期',
    result_type                varchar(10) null comment '激励方案结果类型',
    b1                         varchar(4) null comment '',
    f1                         varchar(20) null comment '',
    f2                         varchar(10) null comment '',
    f3                         varchar(4) null comment '',
    f4                         decimal(10, 2) null comment '',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    key                        `la_incentive_charge_grp_index1` (`charge_rate_series_no`),
    key                        `la_incentive_charge_grp_index2` (`charge_rate_rule_series_no`),
    key                        `la_incentive_charge_grp_index3` (`manage_com`,`agent_com`),
    key                        `la_incentive_charge_grp_index4` (`grp_cont_no`),
    key                        `la_incentive_charge_grp_index5` (`cont_no`),
    key                        `la_incentive_charge_grp_index6` (`charge_rate`)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_general_ci comment ='团险激励方案计算结果表';


drop table if exists charge_cal_queue;
create table charge_cal_queue
(
    id             bigint(20) not null primary key comment '主键',
    uid            varchar(100) not null comment '计算批次',
    index_cal_type varchar(3)   not null comment '计算类型 indexcaltypeenum',
    manage_com     varchar(10)  not null comment '管理机构',
    agent_com      varchar(50)  not null comment '代理机构',
    charge_type    varchar(10)  not null default '' comment '手续费类型',
    cal_type       int          not null default 0 comment '计算方式',
    incentive_id   bigint(20) not null default 0 comment '激励id',
    branch_type    varchar(2)   not null default '' comment '渠道 branchtypeenum',
    start_time     datetime              default null null comment '开始时间计算',
    end_time       datetime              default null null comment '结束计算时间',
    operator       varchar(30)  not null comment '操作人',
    remark         varchar(1000) null comment '说明',
    key            `charge_cal_queue_index1` (`manage_com`, `agent_com`, `charge_type`),
    key            `charge_cal_queue_index2` (`incentive_id`),
    key            `charge_cal_queue_index3` (`uid`)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_general_ci comment ='手续费计算队列';

drop table if exists charge_cal_queue_b;
create table charge_cal_queue_b
(
    id             bigint(20) not null primary key comment '主键',
    uid            varchar(100) not null comment '计算批次',
    index_cal_type varchar(3)   not null comment '计算类型 indexcaltypeenum',
    manage_com     varchar(10)  not null comment '管理机构',
    agent_com      varchar(50)  not null comment '代理机构',
    charge_type    varchar(10)  not null default '' comment '手续费类型',
    cal_type       int          not null default 0 comment '计算方式',
    incentive_id   bigint(20) not null default 0 comment '激励id',
    branch_type    varchar(2)   not null default '' comment '渠道 branchtypeenum',
    start_time     datetime              default null null comment '开始时间计算',
    end_time       datetime              default null null comment '结束计算时间',
    operator       varchar(30)  not null comment '操作人',
    remark         varchar(1000) null comment '说明'
) engine = innodb default charset = utf8mb4 collate = utf8mb4_general_ci comment ='手续费计算队列';


drop table if exists charge_calculate_definition;
create table charge_calculate_definition
(
    id                bigint(20) not null primary key comment '主键',
    index_cal_type    varchar(3)   not null comment '计算类型 indexcaltypeenum',
    cal_code          varchar(10)  not null comment '计算代码',
    cal_name          varchar(50) null comment '手续费类型名称',
    table_name        varchar(30)  not null comment '数据表',
    branch_type       varchar(2)   not null comment '渠道 branchtypeenum',
    charge_type       varchar(30)  not null comment '手续费类型',
    cal_order         decimal(4, 2) null comment ' 排序字段',
    handler_class     varchar(100) not null comment '计算处理类',
    construction_parm varchar(80) null comment '实现类的构造参数,多个参数用,号分割,string类型参数',
    parm              varchar(50) null comment '参数配置',
    remark            varchar(255) null comment '说明',
    key               `charge_calculate_definition_index1` (`branch_type`, `index_cal_type`),
    key               `charge_calculate_definition_index2` (`charge_type`)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_general_ci comment = '手续费计算规则定义表';


drop table if exists sys_incentive_main;
create table sys_incentive_main
(
    id                bigint(20)   not null comment '序列号' primary key,
    code              varchar(50) null default '' comment '代码',
    name              varchar(255) null comment '模板名称',
    branch_type       VARCHAR(2) not null comment '渠道',
    data_source_table varchar(255) null comment '数据源表',
    remark            varchar(50) null default '' comment '说明',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='方案配置主表';

drop table if exists sys_incentive_rule_config;
create table sys_incentive_rule_config
(
    id                bigint(20)    not null comment '序列号' primary key,
    incentive_main_id bigint(20)    not null comment '方案id',
    code              varchar(50) null default '' comment '代码',
    name              varchar(255) null comment '字段名称',
    field             varchar(255) null comment '数据字段',
    type              varchar(50) null default '' comment '数字类型',
    data_table_prefix varchar(50) null default '' comment '字段表前缀',
    base_table        varchar(50) null default '' comment '基础库表',
    branch_type       VARCHAR(2) not null comment '渠道',
    sort              DECIMAL(4, 2) null default 0 comment '排序',
    remark            varchar(50) null default '' comment '说明',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='方案元素配置表';

drop table if exists sys_incentive_radix_config;
create table sys_incentive_radix_config
(
    id                bigint(20)    not null comment '序列号' primary key,
    incentive_main_id bigint(20)    not null comment '方案id',
    code              varchar(50) null default '' comment '代码',
    name              varchar(255) null comment '模板名称',
    field             varchar(255) null comment '数字字段',
    type              varchar(50) null default '' comment '数字类型',
    branch_type       VARCHAR(2) not null comment '渠道',
    sort              DECIMAL(4, 2) null default 0 comment '排序',
    remark            varchar(50) null default '' comment '说明',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='方案结果元素配置表';

drop table if exists sys_incentive_type_config;
create table sys_incentive_type_config
(
    id                bigint(20)    not null comment '序列号' primary key,
    incentive_main_id bigint(20)    not null comment '方案id',
    code              varchar(50) null default '' comment '代码',
    name              varchar(255) null comment '模板名称',
    field             varchar(255) null comment '数字字段',
    type              varchar(50) null default '' comment '数字类型',
    branch_type       VARCHAR(2) not null comment '渠道',
    sort              DECIMAL(4, 2) null default 0 comment '排序',
    remark            varchar(50) null default '' comment '说明',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间'
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='方案结果类型配置表';

drop table if exists la_settlement;
create table la_settlement
(
    commision_sn    varchar(20) not null primary key comment '结算单号',
    commision_bn    varchar(20) null comment '汇总结算单号',
    manage_com      varchar(10) null comment '管理机构',
    agent_com       varchar(20) null comment '中介机构',
    start_date      date null comment '计算起期',
    end_date        date null comment '计算止期',
    trans_money     decimal(18, 4) null comment '保费',
    charge          decimal(18, 4) null comment '手续费',
    settlement_date varchar(10) null comment '',
    state           varchar(2) null comment '状态',
    branch_type     varchar(2) null comment '渠道',
    reason          varchar(100) null comment '',
    remark          varchar(100) null comment '',
    audit_state     varchar(4) null comment '',
    material_state  varchar(4) null comment '',
    charge_type     varchar(4) null comment '手续费类型',
    f01             varchar(20) null comment '备用字段',
    f02             varchar(50) null comment '备用字段',
    f03             varchar(20) null comment '备用字段',
    f04             decimal(12, 4) null comment '备用字段',
    f05             date null  comment '备用字段',
    create_by       bigint(20) comment '创建者',
    create_time     datetime comment '创建时间',
    update_by       bigint(20) comment '更新者',
    update_time     datetime comment '更新时间',
    key             `la_settlement_index` (`commision_bn`),
    key             `la_settlement_index1` (`branch_type`),
    key             `la_settlement_index2` (`agent_com`),
    key             `la_settlement_index3` (`state`)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='手续费结算单';

drop table if exists la_bank_cost;
create table la_bank_cost
(
    series_no         bigint(20)  not null comment '主键' primary key,
    manage_com        varchar(10) not null comment '管理机构',
    agent_com         varchar(20) not null comment '代理机构',
    charge_type       char(2)     not null comment '手续费类型',
    commision_sn      varchar(20) null comment '业绩流水号',
    commision_sn2     varchar(20) null comment '结算单号',
    commision_sn3     varchar(20) null comment '应付单号',
    wage_no           varchar(8) null comment '佣金年月',
    cont_no           varchar(20) not null comment '保单号',
    pol_no            varchar(20) not null comment '投保单号',
    agent_code        varchar(20) null comment '业务员工号',
    appnt_no          varchar(20) null comment '客户号',
    appnt_name        varchar(200) null comment '客户名称',
    audit_date        date null comment '调整日期',
    risk_code         varchar(10) not null comment '险种',
    trans_type        char(3)     not null comment '业绩类型',
    pay_count         int(10)        null comment '保单年度',
    trans_money       decimal(18, 4) null comment '规模保费',
    charge_rate       decimal(12, 4) null comment '手续费比例',
    charge            decimal(12, 4) null comment '手续费',
    charge_state      char null comment '',
    cal_charge_rate   decimal(12, 4) null comment '',
    cal_charge        decimal(12, 4) null comment '',
    print_state       char null comment '',
    voucher_no        varchar(20) null comment '',
    single_fee        decimal(12, 2) null comment '出单费',
    unit_price        decimal(12, 2) null comment '出单费',
    policy_num        int(10)        null comment '',
    start_date        date null comment '计算起期',
    end_date          date null comment '计算止期',
    cvali_date        date null comment '生效日期',
    branch_type       VARCHAR(2)  not null comment '渠道',
    input_tax_rate    varchar(4) null comment '进项税率',
    input_tax         decimal(10, 2) null comment '进项税',
    cal_type          varchar(4) null comment '',
    back_charge       decimal(12, 2) null comment '备份手续费',
    back_input_tax    decimal(12, 2) null comment '备份进项税',
    reward_punish     decimal(12, 4) null comment '',
    old_charge        decimal(12, 4) null comment '',
    reward_punish_fee decimal(12, 4) null comment '',
    old_single_fee    decimal(12, 4) null comment '',
    f1                varchar(20) null comment '',
    f2                varchar(10) null comment '',
    f3                varchar(4) null comment '',
    f4                decimal(10, 2) null comment '',
    f5                varchar(20) null comment '',
    f6                varchar(25) null comment '',
    f7                varchar(20) null comment '',
    f8                varchar(20) null comment '',
    f9                varchar(20) null comment '',
    f10               varchar(20) null comment '',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间',
    key               `la_bank_cost_index1` (`agent_com`),
    key               `la_bank_cost_index2` (`cont_no`),
    key               `la_bank_cost_index3` (`risk_code`)
)ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='银保手续费表';



DROP TABLE IF EXISTS lachargetb;
/*==============================================================*/
/* Table: LACHARGEB                                             */
/*==============================================================*/
CREATE TABLE lachargetb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2) COMMENT '转储类别',
    agentcom         VARCHAR(20) NOT NULL COMMENT '代理机构',
    chargetype       VARCHAR(2)  NOT NULL COMMENT '手续费类型',
    polno            VARCHAR(20) NOT NULL COMMENT '保单号码',
    receiptno        VARCHAR(20) COMMENT '交易号',
    commisionsn      VARCHAR(20) COMMENT '系列号',
    wageno           VARCHAR(20) COMMENT '计算年月代码',
    grpcontno        VARCHAR(20) COMMENT '集体合同号码',
    grppolno         VARCHAR(20) NOT NULL COMMENT '集体保单号码',
    contno           VARCHAR(20) COMMENT '合同号码',
    branchtype       VARCHAR(2) COMMENT '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate          DATE COMMENT '统计日期',
    mainpolno        VARCHAR(20) COMMENT '主险保单号码',
    managecom        VARCHAR(8) COMMENT '管理机构',
    riskcode         VARCHAR(10) COMMENT '险种编码',
    transtype        VARCHAR(3)  NOT NULL COMMENT '交易类别',
    paycount         INT         NOT NULL COMMENT '第几次交费',
    transmoney       DECIMAL(12, 2) COMMENT '交易金额',
    chargerate       DECIMAL(12, 6) COMMENT '手续费比例',
    charge           DECIMAL(16, 6) COMMENT '手续费金额',
    chargestate      VARCHAR(1) COMMENT '手续费状态',
    branchtype2      VARCHAR(2) COMMENT '渠道',
    calchargerate    DECIMAL(12, 6) COMMENT '原手续费比例',
    calcharge        DECIMAL(16, 6) COMMENT '原手续金额',
    confdate         DATE COMMENT '财务实付日期',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               DECIMAL(12, 6) COMMENT 'F3',
    tmakedate        DATE COMMENT '业务确认日期',
    printstate       VARCHAR(1) COMMENT '打印状态',
    operator         VARCHAR(60) COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管中介手续费计算结果备份表';

DROP TABLE IF EXISTS lacharge;
/*==============================================================*/
/* Table: LACHARGE                                              */
/*==============================================================*/
create table lacharge
(
    id             bigint         not null comment '流水号'
        primary key,
    agentcom       varchar(20)    not null comment '通过该字段对应银行专有属性表(可能是银行信息表）',
    chargetype     varchar(2)     not null comment '该字段现在不适用，设置成00',
    polno          varchar(20)    not null comment '固定值00',
    receiptno      varchar(20)    null comment '客户号',
    commisionsn    varchar(20)    null comment '支付流水号',
    wageno         varchar(20)    null comment '计算年月代码',
    grpcontno      varchar(20)    null comment '集体合同号码',
    grppolno       varchar(20)    not null comment '集体保单号码',
    contno         varchar(20)    null comment '固定值00',
    branchtype     varchar(2)     null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate        date           null comment '应付日期，没有审核发放为空',
    mainpolno      varchar(20)    null comment '该字段现在存储agentcode',
    managecom      varchar(8)     null comment '管理机构',
    riskcode       varchar(10)    null comment '险种编码',
    transtype      varchar(3)     not null comment '交易类别',
    paycount       int            not null comment '第几次交费',
    transmoney     decimal(12, 2) null comment '交易金额',
    chargerate     decimal(12, 6) null comment '调整后比例',
    charge         decimal(16, 2) null comment '调整后金额',
    taxrate        decimal(12, 6) null comment '调整后税率',
    tax            decimal(12, 2) null comment '调整后税',
    chargestate    varchar(1)     null comment '0-计算或调整；1-审核发放；2-财务实付',
    branchtype2    varchar(2)     null comment '01-直销；02-中介',
    calchargerate  decimal(12, 6) null comment '原手续费比例',
    calcharge      decimal(16, 2) null comment '原手续金额',
    caltaxrate     decimal(12, 6) null comment '系统计算税率',
    caltax         decimal(12, 2) null comment ' 系统计算税',
    confdate       date           null comment '财务实付日期',
    tmakedate      date           null comment '业务确认日期',
    printstate     varchar(1)     null comment '打印状态',
    serialno       varchar(20)    null comment '结算单号',
    healthflag     varchar(2)     null comment '健康险标识',
    f1             varchar(20)    null comment 'F1',
    f2             varchar(20)    null comment 'F2',
    f3             decimal(12, 6) null comment 'F3',
    f4             varchar(20)    null comment 'F4',
    f5             varchar(20)    null comment 'F5',
    f6             varchar(20)    null comment 'F6',
    f7             varchar(20)    null comment 'F7',
    f8             varchar(20)    null comment 'F8',
    f9             varchar(20)    null comment 'F9',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    lcid           varchar(50)    null comment '流程节点',
    rejectreason   varchar(50)    null comment '驳回字段',
    rejectreason1  varchar(50)    null comment '驳回原因字段',
    constraint idx_agentcom_chargetype_polno_grppolno_transtype_paycount
        unique (agentcom, chargetype, polno, grppolno, transtype, paycount)
)
    comment '中介手续费计算结果表';

DROP TABLE IF EXISTS lachargeb;
create table lachargeb
(
    edorno           varchar(20)    not null comment '转储号'
        primary key,
    edortype         varchar(2)     not null comment '转储类型',
    id               bigint         not null comment '流水号',
    agentcom         varchar(20)    not null comment '通过该字段对应银行专有属性表(可能是银行信息表）',
    chargetype       varchar(2)     not null comment '该字段现在不适用，设置成00',
    polno            varchar(20)    not null comment '固定值00',
    receiptno        varchar(20)    null comment '客户号',
    commisionsn      varchar(20)    null comment '支付流水号',
    wageno           varchar(20)    null comment '计算年月代码',
    grpcontno        varchar(20)    null comment '集体合同号码',
    grppolno         varchar(20)    not null comment '集体保单号码',
    contno           varchar(20)    null comment '固定值00',
    branchtype       varchar(2)     null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate          date           null comment '应付日期，没有审核发放为空',
    mainpolno        varchar(20)    null comment '该字段现在存储agentcode',
    managecom        varchar(8)     null comment '管理机构',
    riskcode         varchar(10)    null comment '险种编码',
    transtype        varchar(3)     not null comment '交易类别',
    paycount         int            not null comment '第几次交费',
    transmoney       decimal(12, 2) null comment '交易金额',
    chargerate       decimal(12, 6) null comment '调整后比例',
    charge           decimal(16, 6) null comment '调整后金额',
    taxrate          decimal(12, 6) null comment '调整后税率',
    tax              decimal(12, 6) null comment ' 调整后税',
    chargestate      varchar(1)     null comment '0-计算或调整；1-审核发放；2-财务实付',
    branchtype2      varchar(2)     null comment '01-直销；02-中介',
    calchargerate    decimal(12, 6) null comment '原手续费比例',
    calcharge        decimal(16, 6) null comment '原手续金额',
    caltaxrate       decimal(12, 6) null comment '系统计算税率',
    caltax           decimal(12, 6) null comment ' 系统计算税',
    confdate         date           null comment '财务实付日期',
    tmakedate        date           null comment '业务确认日期',
    printstate       varchar(1)     null comment '打印状态',
    serialno         varchar(20)    null comment '结算单号',
    healthflag       varchar(2)     null comment '健康险标识',
    f1               varchar(20)    null comment 'F1',
    f2               varchar(20)    null comment 'F2',
    f3               decimal(12, 6) null comment 'F3',
    f4               varchar(20)    null comment 'F4',
    f5               varchar(20)    null comment 'F5',
    f6               varchar(20)    null comment 'F6',
    f7               varchar(20)    null comment 'F7',
    f8               varchar(20)    null comment 'F8',
    f9               varchar(20)    null comment 'F9',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           not null comment '入机日期',
    maketime         varchar(8)     not null comment '入机时间',
    modifydate       date           not null comment '最后一次修改日期',
    modifytime       varchar(8)     not null comment '最后一次修改时间',
    modifyoperator   varchar(20)    not null comment '最后一次修改人',
    lastoperator     varchar(60)    not null comment '备份人',
    lastmakedatetime datetime       not null comment '备份时间'
)
    comment '中介手续费计算结果备份表';


DROP TABLE IF EXISTS lagrpratechargeb;
/*==============================================================*/
/* Table: lagrpratechargeb                                      */
/*==============================================================*/
CREATE TABLE lagrpratechargeb
(
    edorno           VARCHAR(20)   NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2) COMMENT '转储类别',
    grpcontno        VARCHAR(20)   NOT NULL COMMENT '团体合同号',
    grppolno         VARCHAR(20)   NOT NULL COMMENT '团体保单号',
    managecom        VARCHAR(8)    NOT NULL COMMENT '管理机构',
    agentcom         VARCHAR(20)   NOT NULL COMMENT '中介机构编码',
    riskcode         VARCHAR(10)   NOT NULL COMMENT '险种代码',
    rate             DECIMAL(6, 4) NOT NULL COMMENT '手续费比例',
    state            VARCHAR(1)    NOT NULL COMMENT '状态',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               VARCHAR(20) COMMENT 'F3',
    f4               DECIMAL(6, 4) COMMENT 'F4',
    f5               DECIMAL(6, 4) COMMENT 'F5',
    enddate          DATE COMMENT '有效止期',
    startdate        DATE COMMENT '有效起期',
    operator         VARCHAR(60)   NOT NULL COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管团险中介手续费比例信息备份表';


DROP TABLE IF EXISTS lagrpratecharge;
/*==============================================================*/
/* Table: lagrpratecharge                                       */
/*==============================================================*/
CREATE TABLE lagrpratecharge
(
    id             BIGINT(20) NOT NULL COMMENT '流水号',
    grpcontno      VARCHAR(20)   NOT NULL COMMENT '团体合同号',
    grppolno       VARCHAR(20)   NOT NULL COMMENT '团体保单号',
    managecom      VARCHAR(8)    NOT NULL COMMENT '管理机构',
    agentcom       VARCHAR(20)   NOT NULL COMMENT '中介机构编码',
    riskcode       VARCHAR(10)   NOT NULL COMMENT '险种代码',
    rate           DECIMAL(6, 4) NOT NULL COMMENT '手续费比例',
    state          VARCHAR(1)    NOT NULL COMMENT '状态',
    startdate      DATE COMMENT '有效起期',
    enddate        DATE COMMENT '有效止期',
    f1             VARCHAR(20) COMMENT 'F1',
    f2             VARCHAR(20)   NOT NULL COMMENT 'F2',
    f3             VARCHAR(20) COMMENT 'F3',
    f4             DECIMAL(6, 4) COMMENT 'F4',
    f5             DECIMAL(6, 4) COMMENT 'F5',
    operator       VARCHAR(60)   NOT NULL COMMENT '操作员',
    makedate       DATE          NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)    NOT NULL COMMENT '入机时间',
    modifydate     DATE          NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)    NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20)   NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (grpcontno, grppolno, riskcode, f2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管团险中介手续费比例信息表';


DROP TABLE IF EXISTS lagrptschargeb2;
/*==============================================================*/
/* Table: lagrptschargeb2                                       */
/*==============================================================*/
CREATE TABLE lagrptschargeb2
(
    edorno           VARCHAR(20) NOT NULL COMMENT '批次号',
    edortype         VARCHAR(2) COMMENT '转储类别',
    agentcom         VARCHAR(20) NOT NULL COMMENT '通过该字段对应银行专有属性表(可能是银行信息表）',
    chargetype       VARCHAR(2)  NOT NULL COMMENT '该字段现在不适用，设置成00',
    polno            VARCHAR(20) NOT NULL COMMENT '固定值00',
    receiptno        VARCHAR(20) COMMENT '客户号',
    commisionsn      VARCHAR(20) COMMENT '支付流水号',
    wageno           VARCHAR(20) COMMENT '计算年月代码',
    grpcontno        VARCHAR(20) COMMENT '集体合同号码',
    grppolno         VARCHAR(20) NOT NULL COMMENT '集体保单号码',
    contno           VARCHAR(20) COMMENT '固定值00',
    branchtype       VARCHAR(2) COMMENT '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate          DATE COMMENT '应付日期，没有审核发放为空',
    mainpolno        VARCHAR(20) COMMENT '该字段现在存储agentcode',
    managecom        VARCHAR(8) COMMENT '管理机构',
    riskcode         VARCHAR(10) COMMENT '险种编码',
    transtype        VARCHAR(3)  NOT NULL COMMENT '交易类别',
    paycount         INTEGER     NOT NULL COMMENT '第几次交费',
    transmoney       DECIMAL(12, 2) COMMENT '交易金额',
    chargerate       DECIMAL(12, 6) COMMENT '调整后比例',
    charge           DECIMAL(16, 6) COMMENT '调整后金额',
    chargestate      VARCHAR(1) COMMENT '0-计算或调整；1-审核发放；2-财务实付',
    branchtype2      VARCHAR(2) COMMENT '01-直销；02-中介',
    calchargerate    DECIMAL(12, 6) COMMENT '原手续费比例',
    calcharge        DECIMAL(16, 6) COMMENT '原手续金额',
    confdate         DATE COMMENT '财务实付日期',
    tmakedate        DATE COMMENT '业务确认日期',
    printstate       VARCHAR(1) COMMENT '打印状态',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               DECIMAL(12, 6) COMMENT 'F3',
    f4               VARCHAR(20) COMMENT 'F4',
    f5               VARCHAR(20) COMMENT 'F5',
    f6               VARCHAR(20) COMMENT 'F6',
    f7               VARCHAR(20) COMMENT 'F7',
    f8               VARCHAR(20) COMMENT 'F8',
    f9               VARCHAR(20) COMMENT 'F9',
    monthgrantflag   VARCHAR(2) COMMENT '是否本月发放',
    commitflag       VARCHAR(2) COMMENT '提交标识',
    operator         VARCHAR(60) COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管团险手续费暂存备份表';


DROP TABLE IF EXISTS lagrpratechargestandb;
/*==============================================================*/
/* Table: lagrpratechargestandb                                 */
/*==============================================================*/
CREATE TABLE lagrpratechargestandb
(
    edorno           VARCHAR(20)   NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2) COMMENT '转储类别',
    managecom        VARCHAR(8)    NOT NULL COMMENT '管理机构',
    agentcom         VARCHAR(20)   NOT NULL COMMENT '中介机构编码',
    riskcode         VARCHAR(10)   NOT NULL COMMENT '险种代码',
    ratestand        DECIMAL(6, 4) NOT NULL COMMENT '手续费比例标准',
    startdate        DATE          NOT NULL COMMENT '有效起期',
    enddate          DATE          NOT NULL COMMENT '有效止期',
    state            VARCHAR(1)    NOT NULL COMMENT '状态',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               VARCHAR(20) COMMENT 'F3',
    f4               DECIMAL(6, 4) COMMENT 'F4',
    f5               DECIMAL(6, 4) COMMENT 'F5',
    operator         VARCHAR(60)   NOT NULL COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管团险手续费比例标准配置信息备份表';


DROP TABLE IF EXISTS lagrpratechargestand;
/*==============================================================*/
/* Table: lagrpratechargestand                                  */
/*==============================================================*/
CREATE TABLE lagrpratechargestand
(
    id             BIGINT(20) NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)    NOT NULL COMMENT '管理机构',
    agentcom       VARCHAR(20)   NOT NULL COMMENT '中介机构编码',
    riskcode       VARCHAR(10)   NOT NULL COMMENT '险种代码',
    ratestand      DECIMAL(6, 4) NOT NULL COMMENT '手续费比例标准',
    startdate      DATE          NOT NULL COMMENT '有效起期',
    enddate        DATE          NOT NULL COMMENT '有效止期',
    state          VARCHAR(1)    NOT NULL COMMENT '状态',
    f1             VARCHAR(20) COMMENT 'F1',
    f2             VARCHAR(20) COMMENT 'F2',
    f3             VARCHAR(20) COMMENT 'F3',
    f4             DECIMAL(6, 4) COMMENT 'F4',
    f5             DECIMAL(6, 4) COMMENT 'F5',
    operator       VARCHAR(60)   NOT NULL COMMENT '操作员',
    makedate       DATE          NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)    NOT NULL COMMENT '入机时间',
    modifydate     DATE          NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)    NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20)   NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (managecom, agentcom, riskcode, startdate, enddate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管团险手续费比例标准配置信息表';


DROP TABLE IF EXISTS laratechargeb;
CREATE TABLE laratechargeb
(
    edorno         VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype       VARCHAR(2)  NOT NULL COMMENT '转储类型',
    agentcom       VARCHAR(20) NOT NULL COMMENT '代理机构',
    riskcode       VARCHAR(10) NOT NULL COMMENT '险种',
    startdate      DATE        NOT NULL COMMENT '起始日期',
    enddate        DATE        NOT NULL COMMENT '终止日期',
    appage         INT         NOT NULL COMMENT '投保年龄',
    years          INT         NOT NULL COMMENT '保险年期',
    payintv        INT         NOT NULL COMMENT '交费间隔',
    f01            VARCHAR(10) NOT NULL COMMENT '要素1',
    f02            VARCHAR(10) NOT NULL COMMENT '要素2',
    rate           VARCHAR(12) COMMENT '手续费比率',
    state          VARCHAR(2) COMMENT '状态',
    seriesno       VARCHAR(20) NOT NULL COMMENT '序列号',
    yearsflag      VARCHAR(20) COMMENT '保险期间类型',
    s1             VARCHAR(20) COMMENT '备用1',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator         varchar(60)    null comment '备份人',
    lastmakedatetime     datetime       null comment '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管手续费比例配置信息备份表';

DROP TABLE IF EXISTS laratecharge;
-- 创建销管手续费比例配置表
CREATE TABLE laratecharge
(
    id             bigint(20) NOT NULL COMMENT '主键',
    agentcom       VARCHAR(20) NOT NULL COMMENT '代理机构',
    riskcode       VARCHAR(10) NOT NULL COMMENT '险种',
    startdate      DATE        NOT NULL COMMENT '起始日期',
    enddate        DATE        NOT NULL COMMENT '终止日期',
    appage         INT         NOT NULL COMMENT '投保年龄',
    years          INT         NOT NULL COMMENT '保险年期',
    payintv        INT         NOT NULL COMMENT '交费间隔',
    f01            VARCHAR(10) NOT NULL COMMENT '要素1',
    f02            VARCHAR(10) NOT NULL COMMENT '要素2',
    rate           VARCHAR(12) COMMENT '手续费比率',
    seriesno       VARCHAR(20) NOT NULL COMMENT '序列号',
    yearsflag      VARCHAR(20) COMMENT '保险期间类型',
    s1             VARCHAR(20) COMMENT '备用1',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_key (agentcom, riskcode, startdate, enddate, appage, years, payintv, f01, f02, seriesno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管手续费比例配置表';

DROP TABLE IF EXISTS laratecharge;
create table lasettlement
(
    commisionsn    varchar(20)    not null comment '结算单号'
        primary key,
    commisionbn    varchar(20)    null comment '汇总结算单号',
    managecom      varchar(10)    null comment '管理机构',
    agentcom       varchar(20)    null comment '中介机构',
    startdate      date           null comment '计算起期',
    enddate        date           null comment '计算止期',
    transmoney     decimal(18, 4) null comment '保费',
    charge         decimal(18, 4) null comment '手续费',
    settlementdate varchar(10)    null,
    state          varchar(2)     null comment '状态',
    branchtype     varchar(2)     null comment '渠道',
    reason         varchar(100)   null,
    remark         varchar(100)   null,
    auditstate     varchar(4)     null,
    materialstate  varchar(4)     null,
    chargetype     varchar(4)     null comment '手续费类型',
    f01            varchar(20)    null comment '备用字段',
    f02            varchar(50)    null comment '银行信息',
    f03            varchar(20)    null comment '备用字段',
    f04            decimal(12, 4) null comment '备用字段',
    f05            date           null comment '备用字段',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    healthflag     varchar(2)     null comment '健康险标识'
)
    comment '手续费结算单';

create index lasettlementindex
    on lasettlement (commisionbn);

create index lasettlementindex1
    on lasettlement (branchtype);

create index lasettlementindex2
    on lasettlement (agentcom);

create index lasettlementindex3
    on lasettlement (state);


DROP TABLE IF EXISTS laratecharge;
create table lasettlementb
(
    edorno           varchar(20)    not null comment '转储号'
        primary key,
    edortype         varchar(2)     not null comment '转储类型',
    commisionsn      varchar(20)    not null comment '结算单号',
    commisionbn      varchar(20)    null comment '汇总结算单号',
    managecom        varchar(10)    null comment '管理机构',
    agentcom         varchar(20)    null comment '中介机构',
    startdate        date           null comment '计算起期',
    enddate          date           null comment '计算止期',
    transmoney       decimal(18, 4) null comment '保费',
    charge           decimal(18, 4) null comment '手续费',
    settlementdate   varchar(10)    null,
    state            varchar(2)     null comment '状态',
    branchtype       varchar(2)     null comment '渠道',
    reason           varchar(100)   null,
    remark           varchar(100)   null,
    auditstate       varchar(4)     null,
    materialstate    varchar(4)     null,
    chargetype       varchar(4)     null comment '手续费类型',
    f01              varchar(20)    null comment '备用字段',
    f02              varchar(50)    null comment '备用字段',
    f03              varchar(20)    null comment '备用字段',
    f04              decimal(12, 4) null comment '备用字段',
    f05              date           null comment '备用字段',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           not null comment '入机日期',
    maketime         varchar(8)     not null comment '入机时间',
    modifydate       date           not null comment '最后一次修改日期',
    modifytime       varchar(8)     not null comment '最后一次修改时间',
    modifyoperator   varchar(20)    not null comment '最后一次修改人',
    lastoperator     varchar(60)    not null comment '备份人',
    lastmakedatetime datetime       not null comment '备份时间',
    healthflag       varchar(2)     null comment '健康险标识'
)
    comment '手续费结算单备份表';

