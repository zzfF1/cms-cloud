drop table if exists la_branch_information;
create table la_branch_information
(
    id                  bigint(20) auto_increment comment '主键',
    wage_no             varchar(8)   default '' not null comment '快照年月',
    manage_com          VARCHAR(10)             not null comment '管理机构',
    manage_com_name     varchar(200) default '' null comment '管理机构',
    agent_group         varchar(20)  default '' null comment '销售机构',
    name                VARCHAR(100)            not null comment '销售机构名称',
    branch_attr         varchar(70)  default '' null comment '销售机构外部代码',
    branch_manager      varchar(30)  default '' null comment '主管',
    branch_manager_name varchar(100) default '' null comment '主管',
    branch_series       VARCHAR(100)            not null comment '销售机构序列',
    branch_type         CHAR(2)                 not null comment '渠道',
    branch_level        CHAR(2)                 null comment '销售机构级别',
    f1                  varchar(200) default '' null comment '入司人数',
    f2                  varchar(300) default '' null comment '离职人数',
    f3                  varchar(200) default '' null comment '出单人数',
    f4                  varchar(200) default '' null comment '挂0人数',
    f5                  varchar(200) default '' null comment '',
    f6                  varchar(200) default '' null comment '',
    f7                  varchar(200) default '' null comment '',
    f8                  varchar(200) default '' null comment '',
    f9                  varchar(200) default '' null comment '',
    f10                 varchar(200) default '' null comment '',
    f11                 varchar(200) default '' null comment '',
    f12                 varchar(200) default '' null comment '',
    f13                 varchar(200) default '' null comment '',
    f14                 varchar(200) default '' null comment '',
    f15                 varchar(200) default '' null comment '',
    f16                 varchar(200) default '' null comment '',
    f17                 varchar(200) default '' null comment '',
    f19                 varchar(200) default '' null comment '',
    f20                 varchar(200) default '' null comment '',
    create_by           bigint(20) comment '创建者',
    create_time         datetime comment '创建时间',
    update_by           bigint(20) comment '更新者',
    update_time         datetime comment '更新时间',
    primary key (id),
    KEY `la_branch_information_index` (`agent_group`, `wage_no`)
) engine = innodb
  auto_increment = 1
  default charset = utf8mb4
  collate = utf8mb4_general_ci comment ='机构信息快照';


-- ----------------------------
-- Table structure for labranchgroupb-销管销售机构信息备份表
-- ----------------------------
DROP TABLE IF EXISTS labranchgroupb;
CREATE TABLE labranchgroupb
(
    edorno               VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype             VARCHAR(2)  NOT NULL COMMENT '转储类型',
    agentgroup        VARCHAR(12)  NOT NULL COMMENT '销售机构代码',
    name              VARCHAR(80)  NOT NULL COMMENT '销售机构名称',
    managecom         VARCHAR(8)   NOT NULL COMMENT '管理机构',
    upbranch          VARCHAR(12) DEFAULT NULL COMMENT '上级销售机构代码',
    branchattr        VARCHAR(70)  NOT NULL COMMENT '销售机构外部编码',
    branchseries      VARCHAR(200) NOT NULL COMMENT '销售机构序列编码',
    branchtype        VARCHAR(2)   NOT NULL COMMENT '渠道类型',
    branchlevel       VARCHAR(2)  DEFAULT NULL COMMENT '销售机构级别',
    branchmanager     VARCHAR(10) DEFAULT NULL COMMENT '销售机构管理人员',
    branchaddresscode VARCHAR(12) DEFAULT NULL COMMENT '销售机构地址编码',
    branchaddress     VARCHAR(40) DEFAULT NULL COMMENT '销售机构地址',
    branchphone       VARCHAR(18) DEFAULT NULL COMMENT '销售机构电话',
    branchfax         VARCHAR(18) DEFAULT NULL COMMENT '销售机构传真',
    branchzipcode     VARCHAR(6)  DEFAULT NULL COMMENT '销售机构邮编',
    founddate         DATE        DEFAULT NULL COMMENT '成立日期',
    enddate           DATE        DEFAULT NULL COMMENT '停业日期',
    endflag           VARCHAR(1)   NOT NULL COMMENT '停业标识',
    fieldflag         VARCHAR(1)  DEFAULT NULL COMMENT '是否有独立的营销职场',
    state             VARCHAR(10) DEFAULT NULL COMMENT '状态标识',
    branchmanagername VARCHAR(20) DEFAULT NULL COMMENT '销售机构管理人员姓名',
    upbranchattr      VARCHAR(1)  DEFAULT NULL COMMENT '销售机构的上下级属性',
    branchjobtype     VARCHAR(1)  DEFAULT NULL COMMENT '销售机构工作类型',
    branchtype2       VARCHAR(2)  DEFAULT NULL COMMENT '子渠道类型',
    astartdate        DATE        DEFAULT NULL COMMENT '调整日期',
    brancharea        VARCHAR(1)  DEFAULT NULL COMMENT '参与分配否的开关',
    branchclass       VARCHAR(2)  DEFAULT NULL COMMENT '销售机构分类',
    operator          VARCHAR(60)  NOT NULL COMMENT '操作员代码',
    makedate          DATE        DEFAULT NULL COMMENT '入机日期',
    maketime          VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifydate        DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime        VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator    VARCHAR(60)  NOT NULL COMMENT '最后一次修改人',
    lastoperator         VARCHAR(60) COMMENT '备份人',
    lastmakedatetime     DATETIME DEFAULT NULL COMMENT '备份时间',
    indexcalno        varchar(6)   DEFAULT NULL comment '年月',
    branchkind        varchar(2)   DEFAULT NULL comment '销售机构类别',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管销售机构信息备份表';

-- ----------------------------
-- Table structure for labranchgroup-销管销售机构信息表
-- ----------------------------
DROP TABLE IF EXISTS labranchgroup;
CREATE TABLE IF NOT EXISTS labranchgroup
(
    agentgroup        VARCHAR(12)  NOT NULL COMMENT '销售机构代码',
    name              VARCHAR(80)  NOT NULL COMMENT '销售机构名称',
    managecom         VARCHAR(8)   NOT NULL COMMENT '管理机构',
    upbranch          VARCHAR(12) DEFAULT NULL COMMENT '上级销售机构代码',
    branchattr        VARCHAR(70)  NOT NULL COMMENT '销售机构外部编码',
    branchseries      VARCHAR(200) NOT NULL COMMENT '销售机构序列编码',
    branchtype        VARCHAR(2)   NOT NULL COMMENT '渠道类型',
    branchlevel       VARCHAR(2)  DEFAULT NULL COMMENT '销售机构级别',
    branchmanager     VARCHAR(10) DEFAULT NULL COMMENT '销售机构管理人员',
    branchaddresscode VARCHAR(12) DEFAULT NULL COMMENT '销售机构地址编码',
    branchaddress     VARCHAR(40) DEFAULT NULL COMMENT '销售机构地址',
    branchphone       VARCHAR(18) DEFAULT NULL COMMENT '销售机构电话',
    branchfax         VARCHAR(18) DEFAULT NULL COMMENT '销售机构传真',
    branchzipcode     VARCHAR(6)  DEFAULT NULL COMMENT '销售机构邮编',
    founddate         DATE        DEFAULT NULL COMMENT '成立日期',
    enddate           DATE        DEFAULT NULL COMMENT '停业日期',
    endflag           VARCHAR(1)   NOT NULL COMMENT '停业标识',
    fieldflag         VARCHAR(1)  DEFAULT NULL COMMENT '是否有独立的营销职场',
    state             VARCHAR(10) DEFAULT NULL COMMENT '状态标识',
    branchmanagername VARCHAR(20) DEFAULT NULL COMMENT '销售机构管理人员姓名',
    upbranchattr      VARCHAR(1)  DEFAULT NULL COMMENT '销售机构的上下级属性',
    branchjobtype     VARCHAR(1)  DEFAULT NULL COMMENT '销售机构工作类型',
    branchtype2       VARCHAR(2)  DEFAULT NULL COMMENT '子渠道类型',
    astartdate        DATE        DEFAULT NULL COMMENT '调整日期',
    brancharea        VARCHAR(1)  DEFAULT NULL COMMENT '参与分配否的开关',
    branchclass       VARCHAR(2)  DEFAULT NULL COMMENT '销售机构分类',
    operator          VARCHAR(60)  NOT NULL COMMENT '操作员代码',
    makedate          DATE        DEFAULT NULL COMMENT '入机日期',
    maketime          VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifyoperator    VARCHAR(60)  NOT NULL COMMENT '最后一次修改人',
    modifydate        DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime        VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (agentgroup),
    KEY `idx_labranchgroup_index1` (`branchtype`),
    KEY `idx_labranchgroup_index2` (`managecom`),
    KEY `idx_labranchgroup_index3` (`branchattr`),
    KEY `idx_labranchgroup_index4` (`branchseries`)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管销售机构信息表';

-- ----------------------------
-- Table structure for labranchlevel-销管销售组织架构信息表
-- ----------------------------
DROP TABLE IF EXISTS labranchlevel;
CREATE TABLE IF NOT EXISTS labranchlevel
(
    id                   bigint(20)  NOT NULL COMMENT '流水号',
    branchlevelcode      VARCHAR(6)  NOT NULL COMMENT '机构级别编码',
    branchtype           VARCHAR(2)  NOT NULL COMMENT '渠道类型',
    branchtype2          VARCHAR(2)  NOT NULL COMMENT '子渠道类型',
    branchlevelname      VARCHAR(50)  DEFAULT NULL COMMENT '机构级别名称',
    branchleveltype      VARCHAR(1)  NOT NULL COMMENT '机构级别分类',
    branchlevelid        INT         NOT NULL COMMENT '机构级别的id',
    branchproperty       VARCHAR(1)   DEFAULT NULL COMMENT '机构属性',
    agentkind            VARCHAR(6)   DEFAULT NULL COMMENT '机构对应属性',
    subjectproperty      VARCHAR(1)   DEFAULT NULL COMMENT '直辖属性',
    branchlevelproperty1 VARCHAR(2)   DEFAULT NULL COMMENT '机构对应属性1',
    branchlevelproperty2 VARCHAR(2)   DEFAULT NULL COMMENT '机构对应属性2',
    branchlevelproperty3 VARCHAR(2)   DEFAULT NULL COMMENT '机构对应属性3',
    branchlevelproperty4 VARCHAR(2)   DEFAULT NULL COMMENT '机构对应属性4',
    branchlevelproperty5 VARCHAR(2)   DEFAULT NULL COMMENT '机构对应属性5',
    noti                 VARCHAR(200) DEFAULT NULL COMMENT '备注',
    operator             VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate             DATE         DEFAULT NULL COMMENT '入机日期',
    maketime             VARCHAR(8)   DEFAULT NULL COMMENT '入机时间',
    modifydate           DATE         DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime           VARCHAR(8)   DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator       VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_branchlevel (branchlevelcode, branchtype, branchtype2)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管销售组织架构信息表';

DROP TABLE IF EXISTS labranchchange;
create table labranchchange
(
    id             bigint         not null primary key,
    oldmanagecom   varchar(20)    null comment '调整前管理机构',
    newmanagecom   varchar(255)   null comment '调整后管理机构',
    manoeuvredate  date           not null comment '申请日期',
    effectdate     date    not null comment '生效日期',
    oldagentgroup  varchar(20)    null comment '调整前机构内部编码',
    newagentgroup  varchar(20)    null comment '调整后机构内部编码',
    oldbranchattr  varchar(20)    null comment '调整前机构编码',
    newbranchattr  varchar(20)    null comment '调整后机构编码',
    oldbranchname  varchar(20)    null comment '调整前机构机构名称',
    newbranchname  varchar(20)    null comment '调整后上级机构名称',
    branchtype     varchar(10)    not null comment '渠道',
    state          varchar(10)    null comment '状态',
    remark         varchar(200)   null comment '调整说明',
    f1             varchar(20)    null comment '备用字段',
    f2             varchar(50)    null comment '备用字段',
    f3             varchar(20)    null comment '备用字段',
    f4             decimal(20, 6) null comment '备用字段',
    f5             varchar(20)    null comment '备用字段',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           null comment '入机日期',
    maketime       varchar(8)     null comment '入机时间',
    modifyoperator varchar(60)    not null comment '最后一次修改人',
    modifydate     date           null comment '最后一次修改日期',
    modifytime     varchar(8)     null comment '最后一次修改时间',
    KEY `idx_labranchchange_index1` (`branchtype`),
    KEY `idx_labranchchange_index2` (`oldmanagecom`),
    KEY `idx_labranchchange_index3` (`oldagentgroup`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '机构异动申请表';

DROP TABLE IF EXISTS labranchchangeb;
create table labranchchangeb
(
    edorno           VARCHAR(20)    NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2) DEFAULT NULL COMMENT '备份类型',
    id               bigint         not null,
    oldmanagecom     varchar(20)    null comment '调整前管理机构',
    newmanagecom     varchar(255)   null comment '调整后管理机构',
    manoeuvredate    date           not null comment '申请日期',
    effectdate       date    not null comment '生效日期',
    oldagentgroup    varchar(20)    null comment '调整前机构内部编码',
    newagentgroup    varchar(20)    null comment '调整后机构内部编码',
    oldbranchattr    varchar(20)    null comment '调整前机构编码',
    newbranchattr    varchar(20)    null comment '调整后机构编码',
    oldbranchname    varchar(20)    null comment '调整前机构机构名称',
    newbranchname    varchar(20)    null comment '调整后上级机构名称',
    branchtype       varchar(10)    not null comment '渠道',
    state            varchar(10)    null comment '状态',
    remark           varchar(200)   null comment '调整说明',
    f1               varchar(20)    null comment '备用字段',
    f2               varchar(50)    null comment '备用字段',
    f3               varchar(20)    null comment '备用字段',
    f4               decimal(20, 6) null comment '备用字段',
    f5               varchar(20)    null comment '备用字段',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           null comment '入机日期',
    maketime         varchar(8)     null comment '入机时间',
    modifyoperator   varchar(60)    not null comment '最后一次修改人',
    modifydate       date           null comment '最后一次修改日期',
    modifytime       varchar(8)     null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME   DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '机构异动申请备份表';
