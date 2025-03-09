-- 考核相关
drop table if exists base_assess_version;
create TABLE `base_assess_version`
(
    `id`             bigint(8)    NOT NULL AUTO_INCREMENT comment '主键',
    name             varchar(100) null comment '基本说名称',
    `branch_type`    varchar(2)  DEFAULT NULL comment '渠道',
    `index_cal_type` varchar(10) DEFAULT NULL comment '考核类型',
    `status`         tinyint(4)  DEFAULT NULL comment '启用状态',
    remark           varchar(255) null comment '说明',
    create_by        bigint(20) comment '创建者',
    create_time      datetime comment '创建时间',
    update_by        bigint(20) comment '更新者',
    update_time      datetime comment '更新时间',
    PRIMARY KEY (`id`),
    KEY `base_assess_version_index` (`branch_type`, `index_cal_type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='考核基本法版本';

drop table if exists la_assess_config;
CREATE TABLE la_assess_config
(
    `id`                 bigint(19)     NOT NULL AUTO_INCREMENT comment '主键',
    assess_version_id    bigint(8)      NOT NULL COMMENT '考核版本',
    manage_scope         varchar(100)   NOT NULL DEFAULT '' COMMENT '管理机构适用范围',
    assess_code          varchar(20)    NOT NULL DEFAULT '' COMMENT '考核编码',
    assess_way           varchar(10)    NULL     DEFAULT '' COMMENT '考核方式（正式考核，试用期考核，预警考核）',
    assess_type          varchar(20)    NOT NULL DEFAULT '' COMMENT '考核类型（晋升，维持，降级，清退）',
    assess_name          varchar(50)    NOT NULL DEFAULT '' COMMENT '考核名称',
    assess_grade         varchar(10)    NOT NULL DEFAULT '' COMMENT '考核职级',
    dest_grade           varchar(10)    NOT NULL DEFAULT '' COMMENT '目标职级',
    result_grade         varchar(10)    NULL     DEFAULT '' COMMENT '结果职级（如果达不到目标职级，就是结果职级）',
    result_way           varchar(10)    NULL     DEFAULT '' COMMENT '结果类型（晋升，维持，降级，清退）',
    assess_period        varchar(10)    NULL     DEFAULT '' COMMENT '考核周期',
    data_period          varchar(2)     NULL     DEFAULT '' COMMENT '数据统计周期',
    period_parm          varchar(20)    NULL     DEFAULT '' COMMENT '周期参数(例:年度考核在2月不在1月)',
    cal_order            decimal(10, 2) NOT NULL COMMENT '同职级内考核计算顺序',
    branch_type          varchar(10)    NOT NULL DEFAULT '' COMMENT '渠道',
    branch_type2         varchar(10)    NULL     DEFAULT '' COMMENT '子渠道',
    cal_mode_id          varchar(20)    NOT NULL DEFAULT '' COMMENT '考核计算ID',
    handler_class        varchar(50)    NOT NULL DEFAULT '' COMMENT '考核确认处理类',
    personnel_conditions varchar(200)   NOT NULL DEFAULT '' COMMENT '人员条件SPEL表达式laagentassess对象',
    create_by            bigint(20) comment '创建者',
    create_time          datetime comment '创建时间',
    update_by            bigint(20) comment '更新者',
    update_time          datetime comment '更新时间',
    PRIMARY KEY (`id`),
    KEY `la_assess_config_index` (`branch_type`, `assess_way`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='考核配置表';

drop table if exists la_assess_cal_mode;
create table la_assess_cal_mode
(
    `id`              varchar(20)   not null comment '主键',
    assess_version_id bigint(8)     NOT NULL COMMENT '考核版本',
    cal_sql           varchar(1000) not null comment '计算条件',
    remark            varchar(1000) not null comment '说明',
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间',
    primary key (`id`),
    KEY `la_assess_cal_mode_index` (`assess_version_id`)
) engine = innodb
  default charset = utf8mb4
  COLLATE = utf8mb4_general_ci comment '考核计算公式表';

drop table if exists la_assess_cal_index_config;
CREATE TABLE la_assess_cal_index_config
(
    `id`                     bigint(19)    NOT NULL AUTO_INCREMENT COMMENT '主键',
    assess_version_id        bigint(8)     NOT NULL COMMENT '考核版本',
    cal_index_code           varchar(20)   NOT NULL DEFAULT '' COMMENT '考核指标编码',
    assess_way               varchar(10)   NOT NULL DEFAULT '' COMMENT '考核方式（正式考核，试用期考核，预警考核）',
    index_name               varchar(50)   NOT NULL DEFAULT '' COMMENT '指标名称',
    result_table_name        varchar(30)   NOT NULL DEFAULT '' COMMENT '数据结果表',
    result_table_column_name varchar(10)   NOT NULL DEFAULT '' COMMENT '数据结果列',
    cal_order                decimal(4, 2) NOT NULL COMMENT '指标计算顺序',
    branch_type              varchar(10)   NOT NULL DEFAULT '' COMMENT '渠道',
    branch_type2             varchar(10)   NULL     DEFAULT '' COMMENT '子渠道',
    handler_class            varchar(80)   NOT NULL DEFAULT '' COMMENT '计算处理类',
    construction_parms       varchar(80)   NULL     DEFAULT '' COMMENT '实现类的构造参数,多个参数用,号分割,String类型参数',
    create_by                bigint(20) comment '创建者',
    create_time              datetime comment '创建时间',
    update_by                bigint(20) comment '更新者',
    update_time              datetime comment '更新时间',
    PRIMARY KEY (`id`),
    KEY `la_assess_cal_index_config_index` (`assess_version_id`, `cal_index_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '考核计算指标配置';

drop table if exists la_grade_confirm_config;
CREATE TABLE la_grade_confirm_config
(
    `id`           bigint(19)   not null auto_increment comment '主键',
    assess_way     varchar(10)  NOT NULL DEFAULT '' COMMENT '考核方式（正式考核，试用期考核，预警考核）',
    assess_type    varchar(20)  NOT NULL DEFAULT '' COMMENT '考核类型（晋升，维持，降级，清退）',
    grade          varchar(6)   NOT NULL DEFAULT '' COMMENT '当前职级',
    dest_grades    varchar(50)  NOT NULL DEFAULT '' COMMENT '目标职级可以声明多个职级用,号分割',
    branch_type    varchar(2)   NOT NULL DEFAULT '' COMMENT '渠道',
    handler_class  varchar(255) NOT NULL DEFAULT '' COMMENT '实现类',
    flag           int(1)       NOT NULL COMMENT '是否启动0不启用1启用',
    remark         varchar(255) NULL     DEFAULT '' COMMENT '备注',
    operator       varchar(60)  not null comment '操作员',
    makedate       date null comment '入机日期',
    maketime       varchar(8) null comment '入机时间',
    modifyoperator varchar(60)  not null comment '最后一次修改人',
    modifydate     date null comment '最后一次修改日期',
    modifytime     varchar(8) null comment '最后一次修改时间',
    primary key (`id`),
    KEY            `la_grade_confirm_config_index` (`branch_type`, `assess_way`)
) engine = innodb
  auto_increment = 1
  default charset = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '职级确认配置表,配置职级到目标职级的实现类';


drop table if exists la_agent_assess;
CREATE TABLE la_agent_assess
(
    `id`              BIGINT(19)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    assess_date       VARCHAR(10)  NOT NULL COMMENT '考核日期',
    agent_code        VARCHAR(20)  NOT NULL COMMENT '人员编码',
    assess_type       VARCHAR(10)  NOT NULL COMMENT '考核方式',
    name              VARCHAR(30)  NULL default '' COMMENT '人员编码',
    agent_state       VARCHAR(2)   NULL default '' COMMENT '人员状态',
    agent_series      VARCHAR(2)   NULL default '' COMMENT '职级等级',
    agent_grade       VARCHAR(20)  NULL default '' COMMENT '职级编码',
    agent_group       VARCHAR(20)  NULL default '' COMMENT '机构内部编码',
    branch_attr       VARCHAR(20)  NULL default '' COMMENT '机构外部编码',
    employ_date       DATE         NULL COMMENT '入司日期',
    out_work_date     DATE         NULL COMMENT '离职日期',
    assess_start      VARCHAR(20)  NULL default '' COMMENT '起期',
    assess_end        VARCHAR(20)  NULL default '' COMMENT '止期',
    area_type         VARCHAR(10)  NULL default '' COMMENT '地区类型',
    manage_com        VARCHAR(20)  NULL default '' COMMENT '管理机构',
    branch_type       VARCHAR(10)  NOT NULL COMMENT '渠道',
    first_assess_flag VARCHAR(50)  NULL default '' COMMENT '是否第一次考核',
    astart_date       VARCHAR(30)  NULL default '' COMMENT '职级生效日期',
    f1                VARCHAR(200) NULL default '' COMMENT '续期人员分配渠道',
    f2                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f3                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f4                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f5                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f6                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f7                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f8                VARCHAR(200) NULL default '' COMMENT '备用字段',
    f9                DATE         NULL COMMENT '备用字段',
    f10               DATE         NULL COMMENT '备用字段',
    f11               DATE         NULL COMMENT '备用字段',
    f12               DATE         NULL COMMENT '备用字段',
    create_by         BIGINT(20) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         BIGINT(20) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `la_agent_assess_index` (`branch_type`, `assess_type`, `assess_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '考核人员表';

drop table if exists la_assess_cal_result;
create table la_assess_cal_result
(
    series_no              varchar(15)  not null
        primary key comment '主键',
    assess_version_id      bigint(8)    NOT NULL COMMENT '考核版本',
    index_series_no        varchar(15)  null comment '考核明细主键',
    agent_code             varchar(20)  null comment '工号',
    agent_group            varchar(20)  null comment '销售机构',
    manage_com             varchar(20)  null comment '管理机构',
    assess_date            varchar(20)  null comment '考核日期',
    start_date             varchar(20)  null comment '起期',
    end_date               varchar(20)  null comment '止期',
    assess_grade           varchar(20)  null comment '考核职级',
    assess_way             varchar(20)  null comment '考核方式',
    assess_type            varchar(20)  null comment '考核类型',
    dest_grade             varchar(20)  null comment '目标职级',
    adjust_assess_type     varchar(20)  null comment '考核调整类型',
    adjust_grade           varchar(20)  null comment '考核调整职级',
    confirm_grade          varchar(20)  null comment '确认职级',
    confirm_state          varchar(20)  null comment '确认状态',
    keep_grade_count       varchar(20)  null comment '',
    state                  varchar(20)  null comment '',
    branch_type            varchar(20)  null comment '渠道',
    branch_type2           varchar(20)  null comment '',
    area_type              varchar(20)  null comment '',
    adjust_reason          varchar(200) null comment '调整原因',
    adjust_approval_reason varchar(200) null comment '审批原因',
    first_approval_reason  varchar(200) null comment '',
    final_approval_reason  varchar(200) null comment '',
    create_by              bigint(20) comment '创建者',
    create_time            datetime comment '创建时间',
    update_by              bigint(20) comment '更新者',
    update_time            datetime comment '更新时间',
    KEY `la_assess_cal_result_index` (`agent_code`, `assess_way`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='考核计算结果表';

drop table if exists la_assess_cal_index_info;
create table la_assess_cal_index_info
(
    series_no         varchar(15)             not null
        primary key,
    assess_version_id bigint(8)               NOT NULL COMMENT '考核版本',
    index_cal_no      varchar(20)             null comment '职级计算ID',
    index_type        varchar(20)             null comment '',
    agent_code        varchar(20)             null comment '工号',
    agent_group       varchar(20)             null comment '销售机构',
    manage_com        varchar(20)             null comment '管理机构',
    assess_date       varchar(20)             null comment '考核年月',
    start_date        varchar(20)             null comment '统计起期',
    end_date          varchar(20)             null comment '统计止期',
    assess_type       varchar(20)             null comment '',
    branch_type       varchar(20)             null comment '渠道',
    branch_type2      varchar(20)             null comment '子渠道',
    area_type         varchar(20)             null comment '',
    assess_way        varchar(20)             null comment '',
    a1                varchar(20)  default '' null,
    a2                varchar(20)  default '' null,
    a3                varchar(20)  default '' null,
    a4                varchar(20)  default '' null,
    a5                varchar(20)  default '' null,
    a6                varchar(20)  default '' null,
    a7                varchar(20)  default '' null,
    a8                varchar(20)  default '' null,
    a9                varchar(20)  default '' null,
    a10               varchar(20)  default '' null,
    a11               varchar(20)  default '' null,
    a12               varchar(20)  default '' null,
    a13               varchar(20)  default '' null,
    a14               varchar(20)  default '' null,
    a15               varchar(20)  default '' null,
    a16               varchar(20)  default '' null,
    a17               varchar(20)  default '' null,
    a18               varchar(20)  default '' null,
    a19               varchar(20)  default '' null,
    a20               varchar(20)  default '' null,
    a21               varchar(20)  default '' null,
    a22               varchar(20)  default '' null,
    a23               varchar(20)  default '' null,
    a24               varchar(20)  default '' null,
    a25               varchar(20)  default '' null,
    a26               varchar(20)  default '' null,
    a27               varchar(20)  default '' null,
    a28               varchar(20)  default '' null,
    a29               varchar(20)  default '' null,
    a30               varchar(20)  default '' null,
    a31               varchar(20)  default '' null,
    a32               varchar(20)  default '' null,
    a33               varchar(20)  default '' null,
    a34               varchar(20)  default '' null,
    a35               varchar(20)  default '' null,
    a36               varchar(20)  default '' null,
    a37               varchar(20)  default '' null,
    a38               varchar(20)  default '' null,
    a39               varchar(20)  default '' null,
    a40               varchar(20)  default '' null,
    a41               varchar(20)  default '' null,
    a42               varchar(20)  default '' null,
    a43               varchar(20)  default '' null,
    a44               varchar(20)  default '' null,
    a45               varchar(20)  default '' null,
    a46               varchar(20)  default '' null,
    a47               varchar(20)  default '' null,
    a48               varchar(20)  default '' null,
    a49               varchar(20)  default '' null,
    a50               varchar(20)  default '' null,
    a51               varchar(20)  default '' null,
    a52               varchar(20)  default '' null,
    a53               varchar(20)  default '' null,
    a54               varchar(20)  default '' null,
    a55               varchar(20)  default '' null,
    a56               varchar(20)  default '' null,
    a57               varchar(20)  default '' null,
    a58               varchar(20)  default '' null,
    a59               varchar(20)  default '' null,
    a60               varchar(20)  default '' null,
    b1                varchar(20)  default '' null,
    b2                varchar(20)  default '' null,
    b3                varchar(20)  default '' null,
    b4                varchar(20)  default '' null,
    b5                varchar(20)  default '' null,
    b6                varchar(20)  default '' null,
    b7                varchar(20)  default '' null,
    b8                varchar(20)  default '' null,
    b9                varchar(20)  default '' null,
    b10               varchar(20)  default '' null,
    b11               varchar(20)  default '' null,
    b12               varchar(20)  default '' null,
    b13               varchar(20)  default '' null,
    b14               varchar(20)  default '' null,
    b15               varchar(20)  default '' null,
    b16               varchar(20)  default '' null,
    b17               varchar(20)  default '' null,
    b18               varchar(20)  default '' null,
    b19               varchar(20)  default '' null,
    b20               varchar(20)  default '' null,
    b21               varchar(20)  default '' null,
    b22               varchar(20)  default '' null,
    b23               varchar(20)  default '' null,
    b24               varchar(20)  default '' null,
    b25               varchar(20)  default '' null,
    b26               varchar(20)  default '' null,
    b27               varchar(20)  default '' null,
    b28               varchar(20)  default '' null,
    b29               varchar(20)  default '' null,
    b30               varchar(20)  default '' null,
    b31               varchar(20)  default '' null,
    b32               varchar(20)  default '' null,
    b33               varchar(20)  default '' null,
    b34               varchar(20)  default '' null,
    b35               varchar(20)  default '' null,
    b36               varchar(20)  default '' null,
    b37               varchar(20)  default '' null,
    b38               varchar(20)  default '' null,
    b39               varchar(20)  default '' null,
    b40               varchar(20)  default '' null,
    b41               varchar(20)  default '' null,
    b42               varchar(20)  default '' null,
    b43               varchar(20)  default '' null,
    b44               varchar(20)  default '' null,
    b45               varchar(20)  default '' null,
    b46               varchar(20)  default '' null,
    b47               varchar(20)  default '' null,
    b48               varchar(20)  default '' null,
    b49               varchar(20)  default '' null,
    b50               varchar(20)  default '' null,
    b51               varchar(20)  default '' null,
    b52               varchar(20)  default '' null,
    b53               varchar(20)  default '' null,
    b54               varchar(20)  default '' null,
    b55               varchar(20)  default '' null,
    b56               varchar(20)  default '' null,
    b57               varchar(20)  default '' null,
    b58               varchar(20)  default '' null,
    b59               varchar(20)  default '' null,
    b60               varchar(20)  default '' null,
    aa121             varchar(20)  default '' null,
    aa122             varchar(20)  default '' null,
    aa123             varchar(20)  default '' null,
    aa171             varchar(20)  default '' null,
    aa172             varchar(20)  default '' null,
    aa173             varchar(20)  default '' null,
    aa211             varchar(20)  default '' null,
    aa212             varchar(20)  default '' null,
    aa213             varchar(20)  default '' null,
    aa281             varchar(20)  default '' null,
    aa282             varchar(20)  default '' null,
    aa283             varchar(20)  default '' null,
    aa321             varchar(20)  default '' null,
    aa322             varchar(20)  default '' null,
    aa323             varchar(20)  default '' null,
    aa381             varchar(20)  default '' null,
    aa382             varchar(20)  default '' null,
    aa383             varchar(20)  default '' null,
    aa421             varchar(20)  default '' null,
    aa422             varchar(20)  default '' null,
    aa423             varchar(20)  default '' null,
    aa451             varchar(20)  default '' null,
    aa452             varchar(20)  default '' null,
    aa453             varchar(20)  default '' null,
    ab131             varchar(20)  default '' null,
    ab132             varchar(20)  default '' null,
    ab141             varchar(20)  default '' null,
    ab142             varchar(20)  default '' null,
    ab161             varchar(20)  default '' null,
    ab162             varchar(20)  default '' null,
    ab163             varchar(20)  default '' null,
    ab171             varchar(20)  default '' null,
    ab172             varchar(20)  default '' null,
    ab173             varchar(20)  default '' null,
    ab181             varchar(20)  default '' null,
    ab182             varchar(20)  default '' null,
    ab183             varchar(20)  default '' null,
    aa111             varchar(20)  default '' null,
    aa291             varchar(20)  default '' null,
    ab071             varchar(200) default '' null,
    ab101             varchar(20)  default '' null,
    create_by         bigint(20) comment '创建者',
    create_time       datetime comment '创建时间',
    update_by         bigint(20) comment '更新者',
    update_time       datetime comment '更新时间',
    KEY `la_assess_cal_index_info_index` (`agent_code`, `index_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='考核计算过程表';


DROP TABLE IF EXISTS lagrpassessrateb;
/*==============================================================*/
/* Table: lagrpassessrateb                                      */
/*==============================================================*/
CREATE TABLE lagrpassessrateb
(
    id              BIGINT(20)  NOT NULL COMMENT '流水号',
    serialno        VARCHAR(20) NOT NULL COMMENT '序列号',
    branchtype2     VARCHAR(2)  NOT NULL COMMENT '01-直销 02-中介',
    branchtype2name VARCHAR(10) COMMENT '子渠道名称',
    riskcode        VARCHAR(10) NOT NULL COMMENT '险种编码',
    riskname        VARCHAR(120) COMMENT '险种名称',
    custsource      VARCHAR(2)  NOT NULL COMMENT '01-社会 02-资源',
    isgrpornot      VARCHAR(2)  NOT NULL COMMENT '01-是 02-否',
    rate            DECIMAL(6, 4) COMMENT '考核调节系数',
    state           VARCHAR(10) COMMENT '状态',
    t1              VARCHAR(20) COMMENT 'T1',
    t2              VARCHAR(20) COMMENT 'T2',
    t3              DECIMAL(16, 4) COMMENT 'T3',
    t4              DECIMAL(16, 4) COMMENT 'T4',
    t5              DATE COMMENT 'T5',
    t6              DATE COMMENT 'T6',
    bakdate         DATE        NOT NULL COMMENT '备份日期',
    baktime         VARCHAR(8)  NOT NULL COMMENT '备份时间',
    bakoperator     VARCHAR(20) NOT NULL COMMENT '备份操作人',
    edortype       VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    operator        VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate        DATE        NOT NULL COMMENT '入机日期',
    maketime        VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate      DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime      VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (serialno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团校考核系数配置信息备份表';



DROP TABLE IF EXISTS lagrpassessrate;
/*==============================================================*/
/* Table: lagrpassessrate                                       */
/*==============================================================*/
CREATE TABLE lagrpassessrate
(
    id              BIGINT(20)  NOT NULL COMMENT '流水号',
    branchtype2     VARCHAR(2)  NOT NULL COMMENT '01-直销 02-中介',
    branchtype2name VARCHAR(10) COMMENT '子渠道名称',
    riskcode        VARCHAR(10) NOT NULL COMMENT '险种编码',
    riskname        VARCHAR(120) COMMENT '险种名称',
    custsource      VARCHAR(2)  NOT NULL COMMENT '01-社会 02-资源',
    isgrpornot      VARCHAR(2)  NOT NULL COMMENT '01-是 02-否',
    rate            DECIMAL(6, 4) COMMENT '考核调节系数',
    state           VARCHAR(10) COMMENT '状态',
    t1              VARCHAR(20) NOT NULL COMMENT 'T1',
    t2              VARCHAR(20) COMMENT 'T2',
    t3              DECIMAL(16, 4) COMMENT 'T3',
    t4              DECIMAL(16, 4) COMMENT 'T4',
    t5              DATE COMMENT 'T5',
    t6              DATE COMMENT 'T6',
    operator        VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate        DATE        NOT NULL COMMENT '入机日期',
    maketime        VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate      DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime      VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator  VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (branchtype2, riskcode, custsource, isgrpornot, t1)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团校考核系数配置信息表';


DROP TABLE IF EXISTS lagrppartner;
/*==============================================================*/
/* Table: lagrppartner                                          */
/*==============================================================*/
CREATE TABLE lagrppartner
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    agentcom       VARCHAR(20) NOT NULL COMMENT '通过该字段对应银行专有属性表(可能是银行信息表）',
    managecom      VARCHAR(8) COMMENT '管理机构',
    shortcompass   DECIMAL(10, 4) COMMENT '短险达成率',
    longcompass    DECIMAL(10, 4) COMMENT '长险达成率',
    assessno       DECIMAL(12, 2) COMMENT '考核指标代码',
    assessname     VARCHAR(20) COMMENT '考核指标名称',
    assessresult   DECIMAL(12, 2) COMMENT '考核指标得分',
    wageno         VARCHAR(20) NOT NULL COMMENT '提奖年月代码',
    branchtype     CHAR(2)     NOT NULL COMMENT '展业类型',
    f1             VARCHAR(10) COMMENT '备用1',
    f2             VARCHAR(10) COMMENT '备用2',
    f3             VARCHAR(10) COMMENT '备用3',
    f4             VARCHAR(10) COMMENT '备用4',
    f5             VARCHAR(10) COMMENT '备用5',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (agentcom, wageno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险业务考核达成率配置信息表';

DROP TABLE IF EXISTS lacrosssalesassess;
/*==============================================================*/
/* Table: lacrosssalesassess                                    */
/*==============================================================*/
CREATE TABLE lacrosssalesassess
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构编码',
    agentcode      VARCHAR(10) NOT NULL COMMENT '工号',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '1-个人代理，2-团体代理，3－银行代理，4－收费员，－直销,9－其他',
    branchtype2    VARCHAR(2) COMMENT '子渠道',
    flag           VARCHAR(1)  NOT NULL COMMENT '1-交叉销售专员活动量考评 2-交叉销售专员综合考评',
    assessdate     VARCHAR(10) NOT NULL COMMENT '评分日期',
    t1             DECIMAL(16, 4) COMMENT 'T1(活动量考核得分)',
    t2             DECIMAL(16, 4) COMMENT 'T2(月均活动考评得分)',
    t3             DECIMAL(16, 4) COMMENT '扣分项',
    t4             DECIMAL(16, 4) COMMENT '综合考评得分',
    t5             DECIMAL(16, 4) COMMENT 'T5',
    t6             DECIMAL(16, 4) COMMENT 'T6',
    t7             DECIMAL(16, 4) COMMENT 'T7',
    t8             DECIMAL(16, 4) COMMENT 'T8',
    t9             VARCHAR(100) COMMENT 'T9',
    t10            VARCHAR(20) COMMENT 'T10',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (agentcode, flag, assessdate)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险活动量评分信息表';

DROP TABLE IF EXISTS lagrpassessindexb;
/*==============================================================*/
/* Table: LAGRPASSESSINDEXB                                   */
/*==============================================================*/
CREATE TABLE lagrpassessindexb
(
    serialno       VARCHAR(20) NOT NULL COMMENT '序列号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    managecomname  VARCHAR(60) COMMENT '管理机构名称',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    agentname      VARCHAR(20) NOT NULL COMMENT '业务员姓名',
    gradetype      VARCHAR(2)  NOT NULL COMMENT '01-销售经理序列 02-客户经理序列 03-股东专员序列',
    assessyear     VARCHAR(4)  NOT NULL COMMENT '考核年份',
    assessmonth    VARCHAR(2)  NOT NULL COMMENT '考核月份',
    indexcode      VARCHAR(2)  NOT NULL COMMENT '0-政策执行力, 1-团队领导力, 2-培育辅导力度, 3-亲和力, 4-业务行为品质, 5-销售活动表现, 6-培训考核成绩, 7-考勤及其他 8-客户服务满意度 9-服务质量水平 10-工作能力及表现 11-机构团险部年度KPI',
    indexname      VARCHAR(60) COMMENT '考核指标名称',
    score          DECIMAL(16, 4) COMMENT '考核指标得分',
    t1             VARCHAR(20) COMMENT 'T1',
    t2             VARCHAR(20) COMMENT 'T2',
    t3             DECIMAL(16, 4) COMMENT 'T3',
    t4             DECIMAL(16, 4) COMMENT 'T4',
    t5             DATE COMMENT 'T5',
    t6             DATE COMMENT 'T6',
    bakdate        DATE        NOT NULL COMMENT '备份日期',
    baktime        VARCHAR(8)  NOT NULL COMMENT '备份时间',
    bakoperator    VARCHAR(20) NOT NULL COMMENT '备份操作人',
    edortype       VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60) not null comment '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (serialno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险考核指标得分信息备份表';

DROP TABLE IF EXISTS lagrpassessindex;
/*==============================================================*/
/* Table: LAGRPASSESSINDEX                                   */
/*==============================================================*/
CREATE TABLE lagrpassessindex
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    managecomname  VARCHAR(60) COMMENT '管理机构名称',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    agentname      VARCHAR(20) NOT NULL COMMENT '业务员姓名',
    gradetype      VARCHAR(2)  NOT NULL COMMENT '01-销售经理序列 02-客户经理序列 03-股东专员序列',
    assessyear     VARCHAR(4)  NOT NULL COMMENT '考核年份',
    assessmonth    VARCHAR(2)  NOT NULL COMMENT '考核月份',
    indexcode      VARCHAR(2)  NOT NULL COMMENT '0-政策执行力, 1-团队领导力, 2-培育辅导力度, 3-亲和力, 4-业务行为品质, 5-销售活动表现, 6-培训考核成绩, 7-考勤及其他 8-客户服务满意度 9-服务质量水平 10-工作能力及表现 11-机构团险部年度KPI',
    indexname      VARCHAR(60) COMMENT '考核指标名称',
    score          DECIMAL(16, 4) COMMENT '考核指标得分',
    t1             VARCHAR(20) COMMENT 'T1',
    t2             VARCHAR(20) COMMENT 'T2',
    t3             DECIMAL(16, 4) COMMENT 'T3',
    t4             DECIMAL(16, 4) COMMENT 'T4',
    t5             DATE COMMENT 'T5',
    t6             DATE COMMENT 'T6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_manage_agent_year_month_code (managecom, agentcode, assessyear, assessmonth, indexcode)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险考核指标得分信息表';

DROP TABLE IF EXISTS lapayassess;
-- 创建销管指标计算配置表
CREATE TABLE `lapayassess`
(
    `indexcode`    VARCHAR(6)  NOT NULL COMMENT '指标编码',
    `indexname`    VARCHAR(50) NOT NULL COMMENT '指标名称',
    `indextype`    VARCHAR(2) COMMENT '99-系统指标 00-基本指标 01-（个人/收费员）佣金 02-个人/收费员考核',
    `branichtype`  VARCHAR(2)  NOT NULL COMMENT '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    `itablename`   VARCHAR(50) COMMENT '该指标要存储的表',
    `icolname`     VARCHAR(50) COMMENT '该指标要存储的表的字段',
    `indexset`     VARCHAR(120) COMMENT '相关指标',
    `caltype`      VARCHAR(2) COMMENT '计算类型（0-Sql、1-其它）',
    `calcode`      VARCHAR(10) NOT NULL COMMENT 'Sql计算编码',
    `defaultvalue` VARCHAR(20) COMMENT '计算结果值',
    `calprpty`     VARCHAR(2) COMMENT '01－纵向关系（自身的指标之一） 02－横向关系（建立在其他指标基础上的指标）',
    `branichtype2` VARCHAR(2) COMMENT '01 or null -直销 02-中介 03-交叉销售',
    `wagetype`     VARCHAR(2) COMMENT '佣金分类',
    `wageitem`     VARCHAR(4) COMMENT '佣金项目',
    `b1`           VARCHAR(20) COMMENT 'B1',
    `b2`           DECIMAL(15, 6) COMMENT 'B2',
    `b3`           VARCHAR(20) COMMENT 'B3',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`indexcode`) -- 使用原有的主键字段
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管指标计算配置表';


-- ----------------------------
-- Table structure for lagrpassessindexb2-销管考核指标得分信息备份表2
-- ----------------------------
DROP TABLE IF EXISTS lagrpassessindexb2;
CREATE TABLE IF NOT EXISTS lagrpassessindexb2
(
    serialno       VARCHAR(20) NOT NULL COMMENT '序列号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    managecomname  VARCHAR(60)    DEFAULT NULL COMMENT '管理机构名称',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    agentname      VARCHAR(20) NOT NULL COMMENT '业务员姓名',
    gradetype      VARCHAR(2)  NOT NULL COMMENT '职级序列',
    assessyear     VARCHAR(4)  NOT NULL COMMENT '考核年份',
    assessmonth    VARCHAR(2)  NOT NULL COMMENT '考核月份',
    indexcode      VARCHAR(2)  NOT NULL COMMENT '考核指标代码',
    indexname      VARCHAR(60)    DEFAULT NULL COMMENT '考核指标名称',
    score          DECIMAL(16, 4) DEFAULT NULL COMMENT '考核指标得分',
    t1             VARCHAR(20)    DEFAULT NULL COMMENT 'T1',
    t2             VARCHAR(20)    DEFAULT NULL COMMENT 'T2',
    t3             DECIMAL(16, 4) DEFAULT NULL COMMENT 'T3',
    t4             DECIMAL(16, 4) DEFAULT NULL COMMENT 'T4',
    t5             DATE           DEFAULT NULL COMMENT 'T5',
    t6             DATE           DEFAULT NULL COMMENT 'T6',
    bakdate        DATE        NOT NULL COMMENT '备份日期',
    baktime        VARCHAR(8)  NOT NULL COMMENT '备份时间',
    bakoperator    VARCHAR(60) NOT NULL COMMENT '备份操作人',
    edortype       VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (serialno)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考核指标得分信息备份表2';

-- ----------------------------
-- Table structure for lagrpassessindex2-销管考核指标得分信息表2
-- ----------------------------
DROP TABLE IF EXISTS lagrpassessindex2;
CREATE TABLE IF NOT EXISTS lagrpassessindex2
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    assessyear     VARCHAR(4)  NOT NULL COMMENT '考核年份',
    assessmonth    VARCHAR(2)  NOT NULL COMMENT '考核月份',
    indexcode      VARCHAR(2)  NOT NULL COMMENT '考核指标代码',
    managecomname  VARCHAR(60)    DEFAULT NULL COMMENT '管理机构名称',
    agentname      VARCHAR(20) NOT NULL COMMENT '业务员姓名',
    gradetype      VARCHAR(2)  NOT NULL COMMENT '职级序列',
    indexname      VARCHAR(60)    DEFAULT NULL COMMENT '考核指标名称',
    score          DECIMAL(16, 4) DEFAULT NULL COMMENT '考核指标得分',
    t1             VARCHAR(20)    DEFAULT NULL COMMENT 'T1',
    t2             VARCHAR(20)    DEFAULT NULL COMMENT 'T2',
    t3             DECIMAL(16, 4) DEFAULT NULL COMMENT 'T3',
    t4             DECIMAL(16, 4) DEFAULT NULL COMMENT 'T4',
    t5             DATE           DEFAULT NULL COMMENT 'T5',
    t6             DATE           DEFAULT NULL COMMENT 'T6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_lagrpassessmentindex2 (managecom, agentcode, assessyear, assessmonth, indexcode)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考核指标得分信息表2';

-- ----------------------------
-- Table structure for laassessradix-销管考核标准配置信息表
-- ----------------------------


CREATE TABLE   laassessaccessory
(
    serialno 			 varchar(60) COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务流水',
    versioncode 		 varchar(40) COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本号码',
    indexstep 			 int DEFAULT NULL COMMENT '步骤',
    indexcalno           VARCHAR(8)  NOT NULL COMMENT '考核月',
    assesstype           VARCHAR(2)  NOT NULL COMMENT '考核类型',
    agentcode            VARCHAR(10) NOT NULL COMMENT '考核对象工号',
    branchtype           VARCHAR(2)  NOT NULL COMMENT '渠道类型',
    branchtype2          VARCHAR(2)  NOT NULL COMMENT '渠道',
    branchattr           VARCHAR(20)  DEFAULT NULL COMMENT '展业机构外部编码',
    agentgroup           VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom            VARCHAR(8)   DEFAULT NULL COMMENT '管理机构',
    modifyflag           VARCHAR(2)   DEFAULT NULL COMMENT '考核计算结果',
    agentseries          VARCHAR(3)   DEFAULT NULL COMMENT '原代理人系列',
    agentgrade           VARCHAR(4)   DEFAULT NULL COMMENT '原代理人级别',
    calagentseries       VARCHAR(3)   DEFAULT NULL COMMENT '建议代理人系列',
    calagentgrade        VARCHAR(4)   DEFAULT NULL COMMENT '建议代理人级别',
    agentseries1         VARCHAR(3)   DEFAULT NULL COMMENT '新代理人系列',
    agentgrade1          VARCHAR(4)   DEFAULT NULL COMMENT '新代理人级别',
    agentgroupnew        VARCHAR(12)  DEFAULT NULL COMMENT '新代理人组别',
    confirmer            VARCHAR(20)  DEFAULT NULL COMMENT '确认人代码',
    confirmdate          DATE         DEFAULT NULL COMMENT '确认日期',
    state                VARCHAR(10) NOT NULL COMMENT '状态',
    standassessflag      VARCHAR(1)   DEFAULT NULL COMMENT '是否是基本法考核',
    firstassessflag      VARCHAR(1)   DEFAULT NULL COMMENT '是否是第一次考核',
    calconnmanagerstate  VARCHAR(1)   DEFAULT NULL COMMENT '建议衔接人员状态标志',
    confconnmanagerstate VARCHAR(1)   DEFAULT NULL COMMENT '确认衔接人员状态标志',
    connmanagerstate     VARCHAR(1)   DEFAULT NULL COMMENT '当前衔接人员状态标志',
    curredorno           VARCHAR(20)  DEFAULT NULL COMMENT '考核确认结果',
    remark               VARCHAR(100) DEFAULT NULL COMMENT '股东专员薪资标准',
    appdiscription       VARCHAR(100) DEFAULT NULL COMMENT '申请描述',
    agentkind            VARCHAR(6)   DEFAULT NULL COMMENT '原代理人职档',
    calagentkind         VARCHAR(6)   DEFAULT NULL COMMENT '建议代理人职档',
    agentkind1           VARCHAR(12)  DEFAULT NULL COMMENT '股东专员申请薪资标准',
    calstartdate		  date       DEFAULT NULL COMMENT '考核开始日期',
    calstartdate		  date       DEFAULT NULL COMMENT '考核结束日期',
    areatype			 varchar(2)  COMMENT '地区类型',
    A1					 decimal(12,4) comment '考核月数',
    A2					 decimal(12,4) comment '个人累计标准保费',
    A3					 decimal(12,4) comment '个人月均标准保费',
    A4					 decimal(12,4) comment '个人月均标准保费达成率',
    A5					 decimal(12,4) comment '个人标准保费标准值',
    A6 					 decimal(12,4)  COMMENT '品质得分',
    A7 					 decimal(12,4)  COMMENT '当期考核人数',
    A8 					 decimal(12,4)  COMMENT '当期考核维持通过人数',
    A9 					decimal(12,4)  COMMENT '当期考核维持通过率',
    A10 				decimal(12,4)  COMMENT '销售部经理考评得分',
    A11 				decimal(12,4)  COMMENT '团队累计标准保费',
    A12 				decimal(12,4) COMMENT '团队月均标准保费',
    A13 				decimal(12,4)  COMMENT '团队月均标准保费达成率',
    A14 				decimal(12,4)  COMMENT '股东专员年度考核得分',
    A15 				varchar(20)   COMMENT '股东专员年度考核结果等级',
    A16 				decimal(12,4)  COMMENT '股东专员客户服务满意度得分',
    A17 				decimal(12,4)  COMMENT '股东专员服务质量得分',
    A18 				decimal(12,4)  COMMENT '股东专员工作能力及表现得分',
    A19 				decimal(12,4) comment '累计新客户数',
    A20 				decimal(12,4) comment '月均新客户数',
    calagentgrade1 		varchar(4)  COMMENT '建议代理人级别',
    calagentgrade2 		varchar(4)  COMMENT '建议代理人级别',
    calagentgrade3 	varchar(4)  COMMENT '建议代理人级别',
    calagentgrade4 	varchar(4)  COMMENT '建议代理人级别',
    calagentgrade5 		varchar(4)  COMMENT '建议代理人级别',
    operator             VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate             DATE        NOT NULL COMMENT '入机日期',
    maketime             VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate           DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime           VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator       VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (serialno),
    UNIQUE KEY unique_index (indexcalno, assesstype, agentcode, branchtype, branchtype2)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考核结果表';


CREATE TABLE lacalagentinfo (
                                id 						bigint NOT NULL COMMENT '流水号',
                                backmonth 				varchar(6)  NOT NULL COMMENT '备份年月',
                                backtype 					varchar(2)  NOT NULL COMMENT '备份类型',
                                agentcode 				varchar(10)  NOT NULL COMMENT '代理人编码',
                                managecom 				varchar(10)  NOT NULL COMMENT '管理机构',
                                agentgroup 				varchar(12)  NOT NULL COMMENT '代理人组别',
                                agentgrade 				varchar(4)  NOT NULL COMMENT '代理人职级',
                                startdate 				date NOT NULL COMMENT '现职级开始日期',
                                calstartdate 				date NOT NULL COMMENT '开始日期',
                                calenddate 				date NOT NULL COMMENT '截止日期',
                                months 					decimal(4,2)  COMMENT '月数',
                                calstandprem 				decimal(16,4)  COMMENT '个人累计标准保费',
                                calavgstandprem 			decimal(16,4)  COMMENT '个人月均标准保费',
                                calteamstandprem 			decimal(16,4) COMMENT '团队累计标准保费',
                                calteamavgstandprem 		decimal(16,4)  COMMENT '团队月均标准保费',
                                calnewcustomer 			decimal(16,4)  COMMENT '累计新客户数',
                                employmonths 				decimal(4,2)  COMMENT '受雇月数',
                                areatype					varchar(2) comment '地区类型',
                                taxstartdate 				varchar(6)  COMMENT '计税开始年月',
                                A1 						decimal(16,4) comment '股东专员客户服务满意度得分',
                                A2 						decimal(16,4) comment '股东专员服务质量得分',
                                A3 						decimal(16,4) comment '服务专员工作能力及表现得分',
                                A4 						decimal(16,4) comment '品质得分',
                                A5 						date ,
                                A6 						date ,
                                A7 						varchar(20) ,
                                A8 						varchar(20) ,
                                agentgradedes1			varchar(4) comment '降一级职级',
                                agentgradedes2			varchar(4) comment '降二级职级',
                                agentgradeasc1			varchar(4) comment '升一级职级',
                                agentgradeasc2			varchar(4) comment '升二级职级',
                                operator 					varchar(60) ,
                                makedate 					date NOT NULL,
                                maketime 					varchar(8)  NOT NULL,
                                modifyoperator 			varchar(60)  DEFAULT NULL,
                                modifydate 				date NOT NULL,
                                modifytime 				varchar(8)  NOT NULL,
                                PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='薪资考核预处理表';






-- ----------------------------
-- Table structure for laassessapply-销管工作流申请信息表
-- ----------------------------
DROP TABLE IF EXISTS laassessapply;
CREATE TABLE IF NOT EXISTS laassessapply
(
    `assesscode` varchar(20)  NOT NULL COMMENT '考核编码',
    `managecom` varchar(8)  NOT NULL COMMENT '管理机构',
    `indexcalno` varchar(8)  NOT NULL COMMENT '考核年月',
    `assesstype` varchar(255)  DEFAULT NULL COMMENT '考核类别',
    `assessdate` date NOT NULL COMMENT '考核日期',
    `remark` varchar(2500)  DEFAULT NULL COMMENT '考核审批内容',
    `state` varchar(4)  NOT NULL COMMENT '考核流程',
    `branchtype` char(2)  NOT NULL COMMENT '渠道',
    `t1` varchar(200)  DEFAULT NULL COMMENT '备份字段1',
    `t2` varchar(200)  DEFAULT NULL COMMENT '备份字段2',
    `t3` date DEFAULT NULL COMMENT '备份字段3',
    `t4` date DEFAULT NULL COMMENT '备份字段4',
    `t5` varchar(12)  DEFAULT NULL COMMENT '备份字段5',
    `t6` varchar(12)  DEFAULT NULL COMMENT '备份字段6',
    `operator` varchar(60)  NOT NULL COMMENT '操作员',
    `makedate` date NOT NULL COMMENT '入机日期',
    `maketime` varchar(8)  NOT NULL COMMENT '入机时间',
    `modifydate` date NOT NULL COMMENT '最后一次修改日期',
    `modifytime` varchar(8)  NOT NULL COMMENT '最后一次修改时间',
    `modifyoperator` varchar(60)  NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`assesscode`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销管工作流申请信息表';
