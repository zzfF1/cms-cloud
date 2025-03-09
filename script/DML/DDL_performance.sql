DROP TABLE IF EXISTS lacommisiondetailb;
/*==============================================================*/
/* Table: lacommisiondetailb                                    */
/*==============================================================*/
CREATE TABLE lacommisiondetailb
(
    edorno           VARCHAR(20)   NOT NULL COMMENT '转储号',
    edortype         VARCHAR(2)    NOT NULL COMMENT '转储类型',
    grpcontno        VARCHAR(20)   NOT NULL COMMENT '集体合同号码',
    agentcode        VARCHAR(10)   NOT NULL COMMENT '代理人编码',
    busirate         DECIMAL(6, 2) NOT NULL COMMENT '业务百分比',
    agentgroup       VARCHAR(12)   NOT NULL COMMENT '代理人组别',
    poltype          VARCHAR(1)    NOT NULL COMMENT '保单类型',
    startserdate     DATE COMMENT '服务起期',
    endserdate       DATE COMMENT '服务止期',
    makedate2        DATE COMMENT '原入机日期',
    maketime2        VARCHAR(8) COMMENT '原入机时间',
    relationship     VARCHAR(2) COMMENT '与投保人关系',
    operator         VARCHAR(60)   NOT NULL COMMENT '操作员',
    makedate         DATE          NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)    NOT NULL COMMENT '入机时间',
    modifydate       DATE          NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)    NOT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60)   not null comment '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管保单分配比例配置备份表';

DROP TABLE IF EXISTS lacommisiondetail;
/*==============================================================*/
/* Table: lacommisiondetail                                     */
/*==============================================================*/
CREATE TABLE lacommisiondetail
(
    id             BIGINT(20)    NOT NULL COMMENT '流水号',
    grpcontno      VARCHAR(20)   NOT NULL COMMENT '集体合同号码',
    agentcode      VARCHAR(10)   NOT NULL COMMENT '代理人编码',
    busirate       DECIMAL(6, 2) NOT NULL COMMENT '业务百分比',
    agentgroup     VARCHAR(12)   NOT NULL COMMENT '代理人组别',
    poltype        VARCHAR(1)    NOT NULL COMMENT '保单类型',
    startserdate   DATE COMMENT '服务起期',
    endserdate     DATE COMMENT '服务止期',
    relationship   VARCHAR(2) COMMENT '与投保人关系',
    mobile         VARCHAR(15) COMMENT '手机',
    operator       VARCHAR(60)   NOT NULL COMMENT '操作员',
    makedate       DATE          NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)    NOT NULL COMMENT '入机时间',
    modifydate     DATE          NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)    NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20)   NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_grpcontno_agentcode (grpcontno, agentcode)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管保单分配比例配置表';

DROP TABLE IF EXISTS lapolicyreceiptaging;
/*==============================================================*/
/* Table: lapolicyreceiptaging                                  */
/*==============================================================*/
CREATE TABLE lapolicyreceiptaging
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    polno          VARCHAR(20) NOT NULL COMMENT '保单号',
    printdate      DATE COMMENT '打印日期',
    aging          VARCHAR(5) COMMENT '时效',
    remark         VARCHAR(50) COMMENT '备注',
    f1             VARCHAR(20) COMMENT 'F1',
    f2             VARCHAR(20) COMMENT 'F2',
    f3             DECIMAL(12, 4) COMMENT 'F3',
    f4             DECIMAL(12, 4) COMMENT 'F4',
    f5             DATE COMMENT 'F5',
    f6             DATE COMMENT 'F6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_managecom_polno (managecom, polno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管保单绩效信息表';

DROP TABLE IF EXISTS labonusdetail;
-- 团险业务绩效清单表
create table labonusdetail
(
    id             bigint         not null comment '流水号'
        primary key,
    agentcode      varchar(10)    not null comment '现销售人员编码',
    bonustype      varchar(2)     not null comment '该字段现在不适用，设置成00',
    polno          varchar(20)    not null comment '保单号码',
    commisionsn    varchar(10)    null comment '系列号',
    receiptno      varchar(20)    not null comment '客户号',
    dutycode       varchar(10)    not null comment '责任编码',
    riskcode       varchar(10)    not null comment '险种编码',
    managecom      varchar(8)     null comment '管理机构',
    wageno         varchar(20)    not null comment '提奖年月代码',
    agentgroup     varchar(12)    null comment '销售机构',
    grpcontno      varchar(20)    not null comment '集体合同号码',
    grppolno       varchar(20)    not null comment '集体保单号码',
    contno         varchar(20)    null comment '固定值00',
    branchtype     varchar(2)     null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate        date           null comment '应付日期，没有审核发放为空',
    mainpolno      varchar(20)    null comment '该字段现在存储agentcode',
    transtype      varchar(3)     not null comment '交易类别',
    paycount       int            not null comment '第几次交费',
    transmoney     decimal(12, 2) null comment '交易金额',
    bonusrate      decimal(12, 6) null comment '绩效奖金比例',
    bonus          decimal(12, 2) null comment '绩效奖金金额',
    calbonusrate   decimal(12, 6) null comment '原绩效奖金比例',
    calbonus       decimal(12, 2) null comment '原绩效奖金额',
    bonusstate     varchar(1)     null comment '0-计算或调整；1-审核发放；2-财务实付',
    branchtype2    varchar(20)    null comment '01 or null -直销 02-中介',
    confdate       date           null comment '财务实付日期',
    healthflag     varchar(2)     not null comment '健康险标识',
    f1             decimal(12, 2) null comment '管理绩效金额',
    f2             varchar(10)    null comment '现管理绩效归属主管编码',
    f3             varchar(10)    null comment '原管理绩效归属主管编码',
    f4             varchar(20)    null comment '健康险标识',
    f5             varchar(20)    null comment '原销售人员编码',
    k3             varchar(20)    null comment 'K3',
    k4             varchar(20)    null comment 'K4',
    k2             decimal(12, 2) null comment 'K2',
    agenttype      varchar(2)     null comment '销售类型',
    operator       varchar(60)    not null comment '操作员',
    makedate       date           not null comment '入机日期',
    maketime       varchar(8)     not null comment '入机时间',
    modifydate     date           not null comment '最后一次修改日期',
    modifytime     varchar(8)     not null comment '最后一次修改时间',
    modifyoperator varchar(20)    not null comment '最后一次修改人',
    constraint unique_composite
        unique (polno, agentcode, receiptno, dutycode, riskcode, wageno, grpcontno, grppolno, transtype, paycount)
) comment '销管团险业务绩效清单表';

DROP TABLE IF EXISTS labonusdetailb;
-- 团险业务绩效清单备份表
create table labonusdetailb
(
    edorno           varchar(20)    not null comment '转储号码'
        primary key,
    edortype         varchar(2)     not null comment '转储类型',
    id               bigint         not null comment '流水号',
    agentcode        varchar(10)    not null comment '现销售人员编码',
    bonustype        varchar(2)     not null comment '该字段现在不适用，设置成00',
    polno            varchar(20)    not null comment '保单号码',
    commisionsn      varchar(10)    null comment '系列号',
    receiptno        varchar(20)    not null comment '客户号',
    dutycode         varchar(10)    not null comment '责任编码',
    riskcode         varchar(10)    not null comment '险种编码',
    managecom        varchar(8)     null comment '管理机构',
    wageno           varchar(20)    not null comment '提奖年月代码',
    agentgroup       varchar(12)    null comment '销售机构',
    grpcontno        varchar(20)    not null comment '集体合同号码',
    grppolno         varchar(20)    not null comment '集体保单号码',
    contno           varchar(20)    null comment '固定值00',
    branchtype       varchar(2)     null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    caldate          date           null comment '应付日期，没有审核发放为空',
    mainpolno        varchar(20)    null comment '该字段现在存储agentcode',
    transtype        varchar(3)     not null comment '交易类别',
    paycount         int            not null comment '第几次交费',
    transmoney       decimal(12, 2) null comment '交易金额',
    bonusrate        decimal(12, 6) null comment '绩效奖金比例',
    bonus            decimal(12, 2) null comment '绩效奖金金额',
    calbonusrate     decimal(12, 6) null comment '原绩效奖金比例',
    calbonus         decimal(12, 2) null comment '原绩效奖金额',
    bonusstate       varchar(1)     null comment '0-计算或调整；1-审核发放；2-财务实付',
    branchtype2      varchar(20)    null comment '01 or null -直销 02-中介',
    confdate         date           null comment '财务实付日期',
    healthflag       varchar(2)     not null comment '健康险标识',
    f1               decimal(12, 2) null comment '管理绩效金额',
    f2               varchar(10)    null comment '现管理绩效归属主管编码',
    f3               varchar(10)    null comment '原管理绩效归属主管编码',
    f4               varchar(20)    null comment '健康险标识',
    f5               varchar(20)    null comment '原销售人员编码',
    k3               varchar(20)    null comment 'K3',
    k4               varchar(20)    null comment 'K4',
    k2               decimal(12, 2) null comment 'K2',
    agenttype        varchar(2)     null comment '销售类型',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           not null comment '入机日期',
    maketime         varchar(8)     not null comment '入机时间',
    modifydate       date           not null comment '最后一次修改日期',
    modifytime       varchar(8)     not null comment '最后一次修改时间',
    modifyoperator   varchar(20)    not null comment '最后一次修改人',
    lastoperator     varchar(60)    null comment '备份人',
    lastmakedatetime datetime       null comment '备份时间'
) comment '销管团险业务绩效清单备份表';

DROP TABLE IF EXISTS labonusgrant;
create table labonusgrant
(
    id               bigint         not null comment '流水号'
        primary key,
    agentcode        varchar(10)    not null comment '销售人员编码',
    realno           varchar(20)    not null comment '实发年月',
    managecom        varchar(8)     not null comment '管理机构',
    agentgroup       varchar(12)    not null comment '销售机构',
    factpremium      decimal(12, 2) null comment '本期实发绩效奖金',
    shopremium       decimal(12, 2) null comment '本期应发绩效奖金',
    newpremium       decimal(12, 2) null comment '本期新增绩效奖金',
    addunpaidpremium decimal(12, 2) null comment '累计未发绩效奖金',
    unpaidpremium    decimal(12, 2) null comment '未发放绩效奖金',
    state            varchar(2)     not null comment '0-已发放 1-未发放',
    branchtype       varchar(2)     not null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    branchtype2      varchar(20)    null comment '01 or null -直销 02-中介',
    healthflag       varchar(2)     not null comment '健康险标识',
    f1               varchar(10)    null comment 'agentcom',
    f2               varchar(10)    null comment 'years',
    f3               varchar(10)    null comment 'F3',
    f4               varchar(20)    null comment 'F4',
    f5               varchar(20)    null comment 'F5',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           not null comment '入机日期',
    maketime         varchar(8)     not null comment '入机时间',
    modifydate       date           not null comment '最后一次修改日期',
    modifytime       varchar(8)     not null comment '最后一次修改时间',
    modifyoperator   varchar(20)    not null comment '最后一次修改人',
    constraint unique_composite
        unique (agentcode, realno, healthflag)
)
    comment '销管团险业务员绩效核发结果表';


DROP TABLE IF EXISTS labonusgrantb;

create table labonusgrantb
(
    edorno           varchar(20)    not null comment '转储号码'
        primary key,
    edortype         varchar(2)     not null comment '转储类型',
    agentcode        varchar(10)    not null comment '销售人员编码',
    realno           varchar(20)    not null comment '实发年月',
    managecom        varchar(8)     not null comment '管理机构',
    agentgroup       varchar(12)    not null comment '销售机构',
    factpremium      decimal(12, 2) null comment '本期实发绩效奖金',
    shopremium       decimal(12, 2) null comment '本期应发绩效奖金',
    newpremium       decimal(12, 2) null comment '本期新增绩效奖金',
    addunpaidpremium decimal(12, 2) null comment '累计未发绩效奖金',
    unpaidpremium    decimal(12, 2) null comment '未发放绩效奖金',
    state            varchar(2)     not null comment '0-已发放 1-未发放',
    branchtype       varchar(2)     not null comment '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    branchtype2      varchar(20)    null comment '01 or null -直销 02-中介',
    healthflag     varchar(2)       not null comment '健康险标识',
    f1               varchar(10)    null comment 'agentcom',
    f2               varchar(10)    null comment 'years',
    f3               varchar(10)    null comment 'F3',
    f4               varchar(20)    null comment 'F4',
    f5               varchar(20)    null comment 'F5',
    operator         varchar(60)    not null comment '操作员',
    makedate         date           not null comment '入机日期',
    maketime         varchar(8)     not null comment '入机时间',
    modifydate       date           not null comment '最后一次修改日期',
    modifytime       varchar(8)     not null comment '最后一次修改时间',
    modifyoperator   varchar(20)    not null comment '最后一次修改人',
    lastoperator     varchar(60)    null comment '备份人',
    lastmakedatetime datetime       null comment '备份时间'
)
    comment '销管团险业务员绩效核发结果备份表';


DROP TABLE IF EXISTS lagrpconregulate;
/*==============================================================*/
/* Table: lagrpconregulate                                      */
/*==============================================================*/
CREATE TABLE lagrpconregulate
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    grpcontno      VARCHAR(20) NOT NULL COMMENT '集体合同号码',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    wageno         VARCHAR(20) NOT NULL COMMENT '佣金计算年月代码',
    branchtype     VARCHAR(2) COMMENT '展业类型',
    state          VARCHAR(10) COMMENT '状态',
    standbyflag1   VARCHAR(20) COMMENT '备用字段1',
    standbyflag2   VARCHAR(20) COMMENT '备用字段2',
    standbyflag3   VARCHAR(200) COMMENT '备用字段3',
    standbyflag4   DATE COMMENT '备用字段4',
    standbyflag5   VARCHAR(10) COMMENT '备用字段5',
    tmakedate      DATE        NOT NULL COMMENT '入机日期2',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (grpcontno, wageno, agentcode)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险保单调整记录信息表';

DROP TABLE IF EXISTS lainsbusperformancescale;
-- 创建销管团险险种业务绩效比例表
CREATE TABLE lainsbusperformancescale
(
    id             bigint(20)  NOT NULL COMMENT '主键',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    riskcode       VARCHAR(10) NOT NULL COMMENT '险种代码',
    risktype       VARCHAR(40) NOT NULL COMMENT '意外-YW定寿-DS意外医疗-YWYL住院津贴-ZYJT重疾-ZJ境内救援-JZJY补充医疗-BCYL附加学生意外医疗-FJXSYWYL附加学生住院医疗-FJXSZYYL全球医疗-QQYL套餐产品-TCCP长险业务（年金、泰和等）-CXYW',
    validflag      VARCHAR(2) COMMENT '00-无效01-有效',
    riskrate       DECIMAL(12, 4) COMMENT '绩效比例',
    agenttype      VARCHAR(20) COMMENT '销售类型',
    validstart     DATE COMMENT '生效起期',
    validend       DATE COMMENT '生效止期',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道',
    branchtype2    VARCHAR(2) COMMENT '子渠道',
    remark         VARCHAR(240) COMMENT '备注',
    t1             VARCHAR(50) COMMENT 'T1',
    t2             VARCHAR(50) COMMENT 'T2',
    t3             DATE COMMENT 'T3',
    t4             DATE COMMENT 'T4',
    t5             DECIMAL(12, 4) COMMENT 'T5',
    t6             DECIMAL(12, 4) COMMENT 'T6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_lainsbusperformancescale (riskcode, branchtype)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管团险险种业务绩效比例表';


-- 删除表（如果已存在）
DROP TABLE IF EXISTS lagrpcardratebonus;

-- 创建销管团险险种提奖比例配置信息表
CREATE TABLE lagrpcardratebonus
(
    id             bigint(20)    NOT NULL COMMENT '主键',
    managecom      VARCHAR(8)    NOT NULL COMMENT '管理机构',
    riskcode       VARCHAR(10)   NOT NULL COMMENT '险种代码',
    rate           DECIMAL(6, 4) NOT NULL COMMENT '提奖比例',
    state          VARCHAR(1)    NOT NULL COMMENT '状态',
    startdate      DATE COMMENT '有效起期',
    enddate        DATE COMMENT '有效止期',
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
    modifyoperator VARCHAR(60)   NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_lagrpcardratebonus (managecom, riskcode)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管团险险种提奖比例配置信息表';



DROP TABLE IF EXISTS lacommisionb;
-- 创建销管扎帐信息备份表
CREATE TABLE `lacommisionb`
(
    `edorno`           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    `edortype`         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    `commisionsn`      VARCHAR(10)  NOT NULL COMMENT '系列号',
    `commissionbaseno` VARCHAR(10)  NOT NULL COMMENT '系列原始号',
    `wageno`           VARCHAR(20) COMMENT '佣金计算年月代码',
    `grpcontno`        VARCHAR(20)  NOT NULL COMMENT '集体合同号码',
    `grppolno`         VARCHAR(20)  NOT NULL COMMENT '集体保单号码',
    `contno`           VARCHAR(20)  NOT NULL COMMENT '个人合同号码',
    `polno`            VARCHAR(20)  NOT NULL COMMENT '保单号码',
    `mainpolno`        VARCHAR(20)  NOT NULL COMMENT '主险保单号码',
    `managecom`        VARCHAR(8)   NOT NULL COMMENT '管理机构',
    `appntno`          VARCHAR(24) COMMENT '投保人客户号码',
    `riskcode`         VARCHAR(10) COMMENT '险种编码',
    `riskversion`      VARCHAR(8) COMMENT '险种版本',
    `dutycode`         VARCHAR(10) COMMENT '责任编码',
    `payplancode`      VARCHAR(8) COMMENT '交费计划编码',
    `cvalidate`        DATE COMMENT '保单生效日期',
    `payintv`          INT COMMENT '交费间隔',
    `paymode`          VARCHAR(1) COMMENT '交费方式',
    `receiptno`        VARCHAR(20) COMMENT '交费收据号',
    `tpaydate`         DATE COMMENT '交易交费日期',
    `tenteraccdate`    DATE COMMENT '交易到帐日期',
    `tconfdate`        DATE COMMENT '交易确认日期',
    `tmakedate`        DATE COMMENT '交易入机日期',
    `commdate`         DATE COMMENT '生成扎账信息日期',
    `transmoney`       DECIMAL(12, 2) COMMENT '交易金额',
    `transstandmoney`  DECIMAL(12, 2) COMMENT '交易标准金额',
    `lastpaytodate`    DATE COMMENT '原交至日期',
    `curpaytodate`     DATE COMMENT '现交至日期',
    `transtype`        VARCHAR(3) COMMENT '交易类别',
    `commdire`         VARCHAR(1) COMMENT '佣金计算特征',
    `transstate`       VARCHAR(10) COMMENT '交易处理状态',
    `directwage`       DECIMAL(12, 2) COMMENT '直接佣金',
    `appendwage`       DECIMAL(12, 2) COMMENT '附加佣金',
    `grpfdc`           DECIMAL(12, 2) COMMENT '组提佣金额',
    `calcount`         DECIMAL(12, 2) COMMENT '统计件数',
    `caldate`          DATE COMMENT '统计日期',
    `standfycrate`     DECIMAL(12, 6) COMMENT '标准提佣比例',
    `fycrate`          DECIMAL(12, 6) COMMENT '实际提佣比例',
    `fyc`              DECIMAL(12, 2) COMMENT '实际提佣金额',
    `depfyc`           DECIMAL(12, 2) COMMENT '部提佣金额',
    `standprem`        DECIMAL(12, 2) COMMENT '折算标保',
    `commcharge`       DECIMAL(12, 2) COMMENT '标准网点手续费',
    `commcharge1`      DECIMAL(12, 2) COMMENT '标准分理处手续费',
    `commcharge2`      DECIMAL(12, 2) COMMENT '标准支行手续费',
    `commcharge3`      DECIMAL(12, 2) COMMENT '标准分行手续费',
    `commcharge4`      DECIMAL(12, 2) COMMENT '标准总行手续费',
    `grpfycrate`       DECIMAL(12, 6) COMMENT '组提奖比例',
    `depfycrate`       DECIMAL(12, 6) COMMENT '部提奖比例',
    `standpremrate`    DECIMAL(12, 6) COMMENT '折标比例',
    `f1`               VARCHAR(10) COMMENT '交易业务属性1',
    `f2`               VARCHAR(10) COMMENT '交易业务属性2',
    `f3`               VARCHAR(10) COMMENT '交易业务属性3',
    `f4`               VARCHAR(10) COMMENT '交易业务属性4',
    `f5`               VARCHAR(10) COMMENT '交易业务属性5',
    `f6`               VARCHAR(10) COMMENT 'F6',
    `f7`               VARCHAR(10) COMMENT 'F7',
    `f8`               VARCHAR(10) COMMENT 'F8',
    `f9`               VARCHAR(20) COMMENT 'F9',
    `f10`              VARCHAR(20) COMMENT 'F10',
    `k1`               DECIMAL(16, 6) COMMENT '业务费用1',
    `k2`               DECIMAL(12, 2) COMMENT '业务费用2',
    `k3`               DECIMAL(16, 6) COMMENT '业务费用3',
    `k4`               DECIMAL(12, 2) COMMENT '业务费用4',
    `k5`               DECIMAL(12, 2) COMMENT '业务费用5',
    `flag`             VARCHAR(10) COMMENT '标志位',
    `calcdate`         DATE COMMENT '复效日期/还垫日期',
    `payyear`          INT COMMENT '交费年度',
    `payyears`         INT COMMENT '交费年期',
    `years`            INT COMMENT '保险年期',
    `paycount`         INT COMMENT '第几次交费',
    `signdate`         DATE         NOT NULL COMMENT '签单日期',
    `getpoldate`       DATE COMMENT '保单送达日期',
    `branchtype`       VARCHAR(2)   NOT NULL COMMENT '展业类型',
    `agentcom`         VARCHAR(20) COMMENT '代理机构',
    `bankserver`       VARCHAR(20) COMMENT '柜员姓名',
    `agenttype`        VARCHAR(20) COMMENT '代理机构内部分类',
    `agentcode`        VARCHAR(10)  NOT NULL COMMENT '代理人编码',
    `agentgroup`       VARCHAR(12)  NOT NULL COMMENT '代理人展业机构代码',
    `branchcode`       VARCHAR(12)  NOT NULL COMMENT '代理人组别',
    `branchseries`     VARCHAR(200) NOT NULL COMMENT '展业机构序列编码',
    `branchattr`       VARCHAR(20) COMMENT '展业机构外部编码',
    `poltype`          VARCHAR(1) COMMENT '保单类型',
    `p1`               DECIMAL(14, 6) COMMENT '管理费比例',
    `p2`               DECIMAL(12, 6) COMMENT '分红比例',
    `p3`               DECIMAL(12, 6) COMMENT '被保人的投保年龄',
    `p4`               DECIMAL(14, 6) COMMENT 'Nafyc每月增量',
    `p5`               DECIMAL(12, 2) COMMENT '自动垫交标记',
    `p6`               DECIMAL(12, 2) COMMENT '保单类别标志',
    `p7`               DECIMAL(12, 2) COMMENT 'P7',
    `p8`               DECIMAL(16, 6) COMMENT 'P8',
    `p9`               DECIMAL(12, 2) COMMENT 'P9',
    `p10`              DECIMAL(12, 2) COMMENT 'P10',
    `p11`              VARCHAR(120) COMMENT '投保人名称',
    `p12`              VARCHAR(24) COMMENT '被保人号码',
    `p13`              VARCHAR(120) COMMENT '被保人姓名',
    `p14`              VARCHAR(20) COMMENT '印刷号',
    `p15`              VARCHAR(20) COMMENT '投保单号',
    `makepoldate`      DATE COMMENT '交单日期',
    `customgetpoldate` DATE COMMENT '保单回执客户签收日期',
    `riskmark`         VARCHAR(1) COMMENT '主附险标志',
    `scandate`         DATE COMMENT '扫描日期',
    `branchtype2`      VARCHAR(2) COMMENT '渠道',
    `agentcode1`       VARCHAR(10) COMMENT '其它代理人编码',
    `distict`          VARCHAR(12) COMMENT '区',
    `department`       VARCHAR(12) COMMENT '部',
    `riskchnl`         VARCHAR(2) COMMENT '险种渠道',
    `mainpolyear`      INT COMMENT '主险保单年度',
    `opersource`       VARCHAR(6) COMMENT '操作源',
    `commchargerate`   DECIMAL(12, 6) COMMENT '佣金费率',
    `p16`              VARCHAR(60) COMMENT '联系人电话',
    `remark`           VARCHAR(100) COMMENT '备注',
    `itemcode`         VARCHAR(12) COMMENT '项目代码',
    operator           VARCHAR(60)  NOT NULL COMMENT '操作员',
    makedate           DATE         NOT NULL COMMENT '入机日期',
    maketime           VARCHAR(8)   NOT NULL COMMENT '入机时间',
    modifydate         DATE         NOT NULL COMMENT '最后一次修改日期',
    modifytime         VARCHAR(8)   NOT NULL COMMENT '最后一次修改时间',
    modifyoperator     VARCHAR(60)  NOT NULL COMMENT '最后一次修改人',
    lastoperator       VARCHAR(60)  null comment '备份人',
    lastmakedatetime   DATETIME     null comment '备份时间',
    PRIMARY KEY (`edorno`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '销管扎帐信息备份表';

DROP TABLE IF EXISTS lacommision;
-- 创建新表
CREATE TABLE `lacommision`
(
    `commisionsn`      VARCHAR(10)  NOT NULL COMMENT '系列号',
    `commisionbaseno`  VARCHAR(10)  NOT NULL COMMENT '系列原始号',
    `wageno`           VARCHAR(20) COMMENT '佣金计算年月代码',
    `grpcntno`         VARCHAR(20)  NOT NULL COMMENT '集体合同号码',
    `grppolno`         VARCHAR(20)  NOT NULL COMMENT '集体保单号码',
    `cntno`            VARCHAR(20)  NOT NULL COMMENT '个人合同号码',
    `polno`            VARCHAR(20)  NOT NULL COMMENT '保单号码',
    `mainpolno`        VARCHAR(20)  NOT NULL COMMENT '主险保单号码',
    `managecom`        VARCHAR(8)   NOT NULL COMMENT '管理机构',
    `appntno`          VARCHAR(24) COMMENT '投保人客户号码',
    `riskcode`         VARCHAR(10) COMMENT '险种编码',
    `riskversion`      VARCHAR(8) COMMENT '险种版本',
    `dutycode`         VARCHAR(10) COMMENT '责任编码',
    `payplancode`      VARCHAR(8) COMMENT '交费计划编码',
    `cvalidate`        DATE COMMENT '保单生效日期',
    `payintv`          INT COMMENT '交费间隔',
    `paymode`          VARCHAR(1) COMMENT '交费方式',
    `receiptno`        VARCHAR(20) COMMENT '交费收据号',
    `tpaydate`         DATE COMMENT '交易交费日期',
    `tenteraccdate`    DATE COMMENT '交易到帐日期',
    `tconfdate`        DATE COMMENT '交易确认日期',
    `tmakedate`        DATE COMMENT '交易入机日期',
    `commdate`         DATE COMMENT '报单失效日期或者理赔终止日期',
    `transmoney`       DECIMAL(14, 3) COMMENT '交易金额',
    `transstandmoney`  DECIMAL(12, 2) COMMENT '交易标准金额',
    `lastpaytodate`    DATE COMMENT '原交至日期',
    `curpaytodate`     DATE COMMENT '现交至日期',
    `transtype`        VARCHAR(3) COMMENT '交易类别',
    `commdire`         VARCHAR(1) COMMENT '佣金计算特征',
    `transstate`       VARCHAR(10) COMMENT '交易处理状态',
    `directwage`       DECIMAL(12, 2) COMMENT '直接佣金',
    `appendwage`       DECIMAL(12, 2) COMMENT '附加佣金',
    `grpfdc`           DECIMAL(12, 2) COMMENT '组提佣金额',
    `calcount`         DECIMAL(12, 2) COMMENT '统计件数',
    `caldate`          DATE COMMENT '统计日期',
    `standfycrate`     DECIMAL(12, 6) COMMENT '标准提佣比例',
    `fycrate`          DECIMAL(12, 6) COMMENT '实际提佣比例',
    `fyc`              DECIMAL(12, 2) COMMENT '实际提佣金额',
    `depfdc`           DECIMAL(12, 2) COMMENT '部提佣金额',
    `standprem`        DECIMAL(12, 2) COMMENT '折算标保',
    `commcharge`       DECIMAL(12, 2) COMMENT '标准网点手续费',
    `commcharge1`      DECIMAL(12, 2) COMMENT '标准分理处手续费',
    `commcharge2`      DECIMAL(12, 2) COMMENT '标准支行手续费',
    `commcharge3`      DECIMAL(12, 2) COMMENT '标准分行手续费',
    `commcharge4`      DECIMAL(12, 2) COMMENT '标准总行手续费',
    `grpfycrate`       DECIMAL(12, 6) COMMENT '组提奖比例',
    `depfycrate`       DECIMAL(12, 6) COMMENT '部提奖比例',
    `standpremrte`     DECIMAL(12, 6) COMMENT '折标比例',
    `f1`               VARCHAR(10) COMMENT '交易业务属性1',
    `f2`               VARCHAR(10) COMMENT '交易业务属性2',
    `f3`               VARCHAR(10) COMMENT '交易业务属性3',
    `f4`               VARCHAR(10) COMMENT '交易业务属性4',
    `f5`               VARCHAR(10) COMMENT '交易业务属性5',
    `f6`               VARCHAR(10) COMMENT 'F6',
    `f7`               VARCHAR(10) COMMENT 'F7',
    `f8`               VARCHAR(10) COMMENT 'F8',
    `f9`               VARCHAR(20) COMMENT 'F9',
    `f10`              VARCHAR(20) COMMENT 'F10',
    `f11`              VARCHAR(12) COMMENT 'F11',
    `f12`              VARCHAR(12) COMMENT 'F12',
    `f13`              VARCHAR(12) COMMENT 'F13',
    `f14`              VARCHAR(50) COMMENT 'F14',
    `f15`              VARCHAR(12) COMMENT 'F15',
    `f16`              VARCHAR(12) COMMENT 'F16',
    `f17`              DATE COMMENT 'F17',
    `f18`              DATE COMMENT 'F18',
    `f19`              VARCHAR(60) COMMENT 'F19',
    `f20`              VARCHAR(60) COMMENT 'F20',
    `k1`               DECIMAL(16, 6) COMMENT '业务费用1',
    `k2`               DECIMAL(12, 2) COMMENT '业务费用2',
    `k3`               DECIMAL(16, 6) COMMENT '业务费用3',
    `k4`               DECIMAL(12, 2) COMMENT '业务费用4',
    `k5`               DECIMAL(12, 2) COMMENT '业务费用5',
    `flag`             VARCHAR(10) COMMENT '标志位',
    `calcdate`         DATE COMMENT '复效日期/还垫日期',
    `payyear`          INT COMMENT '交费年度',
    `payyears`         INT COMMENT '交费年期',
    `years`            INT COMMENT '保险年期',
    `paycount`         INT COMMENT '第几次交费',
    `signdate`         DATE         NOT NULL COMMENT '签单日期',
    `getpoldate`       DATE COMMENT '保单送达日期',
    `branchtype`       VARCHAR(2)   NOT NULL COMMENT '展业类型',
    `agentcom`         VARCHAR(20) COMMENT '代理机构',
    `bankserver`       VARCHAR(20) COMMENT '柜员姓名',
    `agenttype`        VARCHAR(20) COMMENT '代理机构内部分类',
    `agentcode`        VARCHAR(10)  NOT NULL COMMENT '代理人编码',
    `agentgroup`       VARCHAR(12)  NOT NULL COMMENT '代理人展业机构代码',
    `branchcode`       VARCHAR(12)  NOT NULL COMMENT '代理人组别',
    `branchseries`     VARCHAR(200) NOT NULL COMMENT '展业机构序列编码',
    `branchattr`       VARCHAR(20) COMMENT '展业机构外部编码',
    `poltype`          VARCHAR(1) COMMENT '保单类型',
    `p1`               DECIMAL(14, 6) COMMENT '管理费比例',
    `p2`               DECIMAL(14, 6) COMMENT '分红比例',
    `p3`               DECIMAL(12, 6) COMMENT '被保人的投保年龄',
    `p4`               DECIMAL(14, 6) COMMENT 'Nafyc每月增量',
    `p5`               DECIMAL(12, 2) COMMENT '自动垫交标记',
    `p6`               DECIMAL(12, 2) COMMENT '保单类别标志',
    `p7`               DECIMAL(12, 2) COMMENT 'P7',
    `p8`               DECIMAL(16, 6) COMMENT 'P8',
    `p9`               DECIMAL(12, 2) COMMENT 'P9',
    `p10`              DECIMAL(12, 2) COMMENT 'P10',
    `p11`              VARCHAR(120) COMMENT '投保人名称',
    `p12`              VARCHAR(24) COMMENT '被保人号码',
    `p13`              VARCHAR(120) COMMENT '被保人姓名',
    `p14`              VARCHAR(20) COMMENT '印刷号',
    `p15`              VARCHAR(20) COMMENT '投保单号',
    `p16`              VARCHAR(60) COMMENT '联系人电话',
    `makepoldate`      DATE COMMENT '制单日期',
    `customgetpoldate` DATE COMMENT '客户收到保单日期',
    `riskmark`         VARCHAR(1) COMMENT '风险标记',
    `scandate`         DATE COMMENT '扫描日期',
    `branchtype2`      VARCHAR(2) COMMENT '渠道',
    `agentcode1`       VARCHAR(10) COMMENT '代理人编码1',
    `distict`          VARCHAR(12) COMMENT '区县',
    `department`       VARCHAR(12) COMMENT '部门',
    `riskchnl`         VARCHAR(2) COMMENT '渠道',
    `mainpolyear`      INT COMMENT '主险保单年度',
    `opersource`       VARCHAR(6) COMMENT '来源',
    `commchargert`     DECIMAL(12, 6) COMMENT '手续费率',
    `remark`           VARCHAR(100) COMMENT '备注',
    `itemcode`         VARCHAR(12) COMMENT '项目编码',
    `returndate`       DATE COMMENT '回访日期',
    `returntype`       VARCHAR(2) COMMENT '回访类型',
    `yearsflag`        VARCHAR(1) COMMENT '保险期间类型',
    `opersource1`      VARCHAR(4) COMMENT '业务来源1',
    `verifystate`      VARCHAR(2) COMMENT '核保状态',
    `verifydate`       DATE COMMENT '核保日期',
    `grpfamilytype`    VARCHAR(2) COMMENT '组别类型',
    `newcustomer`      VARCHAR(1) COMMENT '新客户标识',
    operator           VARCHAR(60)  NOT NULL COMMENT '操作员',
    makedate           DATE         NOT NULL COMMENT '入机日期',
    maketime           VARCHAR(8)   NOT NULL COMMENT '入机时间',
    modifydate         DATE         NOT NULL COMMENT '最后一次修改日期',
    modifytime         VARCHAR(8)   NOT NULL COMMENT '最后一次修改时间',
    modifyoperator     VARCHAR(60)  NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`commisionsn`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管扎帐信息表';

DROP TABLE IF EXISTS lastatsegment;
-- 创建销管时间统计表
CREATE TABLE `lastatsegment`
(
    `id`           bigint(20)  NOT NULL COMMENT '主键',
    `stattype`     VARCHAR(2)  NOT NULL COMMENT '统计类型',
    `yearmonth`    INT         NOT NULL COMMENT '统计间隔',
    `startdate`    DATE COMMENT '起始时间',
    `enddate`      DATE COMMENT '截止时间',
    `extdate`      DATE COMMENT '扩展时间',
    `predate`      DATE COMMENT '预留时间',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_stattype_yearmonth` (`stattype`, `yearmonth`) -- 联合唯一索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管时间统计表';

DROP TABLE IF EXISTS laratestandpremb;
-- 创建销管标准保费险种比例信息备份表
CREATE TABLE `laratestandpremb`
(
    `edorno`         VARCHAR(20)    NOT NULL COMMENT '转储号码',
    `edortype`       VARCHAR(2)     NOT NULL COMMENT '转储类型',
    `serialno`       VARCHAR(10) COMMENT '主键',
    `branchtype`     VARCHAR(2)     NOT NULL COMMENT '展业类型',
    `riskcode`       VARCHAR(10)    NOT NULL COMMENT '险种',
    `sex`            VARCHAR(1) COMMENT '性别',
    `appage`         INT COMMENT '投保年龄',
    `year`           INT COMMENT '保险年期',
    `payintv`        VARCHAR(2) COMMENT '交费间隔',
    `curyear`        INT COMMENT '保单年度',
    `f01`            VARCHAR(10) COMMENT '要素1',
    `f02`            VARCHAR(10) COMMENT '要素2',
    `f03`            VARCHAR(20) COMMENT '要素3',
    `f04`            VARCHAR(20) COMMENT '要素4',
    `f05`            DECIMAL(12, 6) COMMENT '要素5',
    `f06`            DECIMAL(12, 6) COMMENT '要素6',
    `rate`           DECIMAL(10, 4) NOT NULL COMMENT '比率',
    `managecom`      VARCHAR(8)     NOT NULL COMMENT '管理机构',
    `branchtype2`    VARCHAR(2) COMMENT '渠道',
    `startdate`      DATE COMMENT '有效起期',
    `enddate`        DATE COMMENT '有效止期',
    operator         VARCHAR(60)    NOT NULL COMMENT '操作员',
    makedate         DATE           NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     NOT NULL COMMENT '入机时间',
    modifydate       DATE           NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     NOT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60)    NOT NULL COMMENT '最后一次修改人',
    lastoperator     varchar(60)    null comment '备份人',
    lastmakedatetime datetime       null comment '备份时间',
    PRIMARY KEY (`edorno`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管标准保费险种比例信息备份表';



DROP TABLE IF EXISTS laratestandprem;
-- 创建销管标准保费险种比例信息表
CREATE TABLE `laratestandprem`
(
    `serialno`     VARCHAR(10)    NOT NULL COMMENT '主键',
    `branchtype`   VARCHAR(2)     NOT NULL COMMENT '展业类型',
    `riskcode`     VARCHAR(10)    NOT NULL COMMENT '险种',
    `sex`          VARCHAR(1) COMMENT '性别',
    `appage`       INT COMMENT '投保年龄',
    `year`         INT COMMENT '保险年期',
    `payintv`      VARCHAR(2) COMMENT '交费间隔',
    `curyear`      INT COMMENT '保单年度',
    `rate`         DECIMAL(10, 4) NOT NULL COMMENT '比率',
    `managecom`    VARCHAR(8)     NOT NULL COMMENT '管理机构',
    `branchtype2`  VARCHAR(2) COMMENT '渠道',
    `startdate`    DATE COMMENT '有效起期',
    `enddate`      DATE COMMENT '有效止期',
    `f01`          VARCHAR(10) COMMENT '要素1',
    `f02`          VARCHAR(10) COMMENT '要素2',
    `f03`          VARCHAR(20) COMMENT '要素3',
    `f04`          VARCHAR(20) COMMENT '要素4',
    `f05`          DECIMAL(12, 6) COMMENT '要素5',
    `f06`          DECIMAL(12, 6) COMMENT '要素6',
    operator       VARCHAR(60)    NOT NULL COMMENT '操作员',
    makedate       DATE           NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     NOT NULL COMMENT '入机时间',
    modifydate     DATE           NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60)    NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`serialno`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管标准保费险种比例信息表';

-- ----------------------------
-- Table structure for lalinklist-销管联系单信息表
-- ----------------------------
DROP TABLE IF EXISTS lalinklist;
CREATE TABLE IF NOT EXISTS lalinklist
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    serialno       VARCHAR(20) NOT NULL COMMENT '流水号',
    linkcode       VARCHAR(14) NOT NULL COMMENT '联系单编码',
    linkname       VARCHAR(20) NOT NULL COMMENT '联系单名称',
    managecom      VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentcode      VARCHAR(10)    DEFAULT NULL COMMENT '代理人编码',
    name           VARCHAR(20)    DEFAULT NULL COMMENT '代理人姓名',
    linktype       VARCHAR(2)     DEFAULT NULL COMMENT '联系单类型',
    aclass         VARCHAR(2)     DEFAULT NULL COMMENT '奖项类别',
    aitem          VARCHAR(2)     DEFAULT NULL COMMENT '奖项项目',
    money          DECIMAL(12, 2) DEFAULT NULL COMMENT '奖项金额',
    contno         VARCHAR(20)    DEFAULT NULL COMMENT '保单号',
    proposalno     VARCHAR(20)    DEFAULT NULL COMMENT '投保单号',
    department     VARCHAR(100)   DEFAULT NULL COMMENT '会签部门',
    abstract       VARCHAR(200)   DEFAULT NULL COMMENT '内容摘要',
    state          VARCHAR(2)     DEFAULT NULL COMMENT '联系单状态',
    dealway        VARCHAR(20)    DEFAULT NULL COMMENT '处理方式',
    dealresult     VARCHAR(20)    DEFAULT NULL COMMENT '处理结果',
    wageno         VARCHAR(6)     DEFAULT NULL COMMENT '处理年月',
    indexcalno     VARCHAR(6)     DEFAULT NULL COMMENT '加扣执行年月',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_serialno_linkcode (serialno, linkcode)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管联系单信息表';

-- ----------------------------
-- Table structure for lalinklistb-销管联系单备份信息表
-- ----------------------------
DROP TABLE IF EXISTS lalinklistb;
CREATE TABLE IF NOT EXISTS lalinklistb
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号',
    edortype         VARCHAR(2)  NOT NULL COMMENT '备份类型',
    serialno         VARCHAR(20)    DEFAULT NULL COMMENT '流水号',
    linkcode         VARCHAR(14)    DEFAULT NULL COMMENT '联系单编码',
    linkname         VARCHAR(20)    DEFAULT NULL COMMENT '联系单名称',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentcode        VARCHAR(10)    DEFAULT NULL COMMENT '代理人编码',
    name             VARCHAR(20)    DEFAULT NULL COMMENT '代理人姓名',
    linktype         VARCHAR(2)     DEFAULT NULL COMMENT '联系单类型',
    aclass           VARCHAR(2)     DEFAULT NULL COMMENT '奖项类别',
    aitem            VARCHAR(2)     DEFAULT NULL COMMENT '奖项项目',
    money            DECIMAL(12, 2) DEFAULT NULL COMMENT '奖项金额',
    department       VARCHAR(100)   DEFAULT NULL COMMENT '会签部门',
    contno           VARCHAR(20)    DEFAULT NULL COMMENT '保单号',
    proposalno       VARCHAR(20)    DEFAULT NULL COMMENT '投保单号',
    abstract         VARCHAR(200)   DEFAULT NULL COMMENT '内容摘要',
    state            VARCHAR(22)    DEFAULT NULL COMMENT '联系单状态',
    dealway          VARCHAR(20)    DEFAULT NULL COMMENT '处理方式',
    dealresult       VARCHAR(20)    DEFAULT NULL COMMENT '处理结果',
    wageno           VARCHAR(6)     DEFAULT NULL COMMENT '处理年月',
    indexcalno       VARCHAR(6)     DEFAULT NULL COMMENT '加扣执行年月',
    branchtype       VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate         DATE           DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME       DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (id),
    UNIQUE KEY unique_edorno_edortype (edorno, edortype)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管联系单备份信息表';

-- ----------------------------
-- Table structure for lawagecalelement-销管险种信息计算配置表
-- ----------------------------
DROP TABLE IF EXISTS lawagecalelement;
CREATE TABLE IF NOT EXISTS lawagecalelement
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    riskcode       VARCHAR(10) NOT NULL COMMENT 'RISKCODE',
    caltype        VARCHAR(2)  NOT NULL COMMENT 'CALTYPE',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道类型',
    calcode        VARCHAR(6)  DEFAULT NULL COMMENT 'CALCODE',
    branchtype2    VARCHAR(2)  DEFAULT NULL COMMENT 'BRANCHTYPE2',
    f1             VARCHAR(10) DEFAULT NULL COMMENT 'F1',
    f2             VARCHAR(10) DEFAULT NULL COMMENT 'F2',
    f3             VARCHAR(10) DEFAULT NULL COMMENT 'F3',
    f4             VARCHAR(10) DEFAULT NULL COMMENT 'F4',
    f5             VARCHAR(10) DEFAULT NULL COMMENT 'F5',
    f6             VARCHAR(10) DEFAULT NULL COMMENT 'F6',
    f7             VARCHAR(10) DEFAULT NULL COMMENT 'F7',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE        DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_risk_cal_branch (riskcode, caltype, branchtype)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管险种信息计算配置表';

-- ----------------------------
-- Table structure for larateplusprem-销管险种加权比例配置信息表
-- ----------------------------
DROP TABLE IF EXISTS larateplusprem;
CREATE TABLE IF NOT EXISTS larateplusprem
(
    serialno
    VARCHAR(12)    NOT NULL COMMENT '序号',
    risktype       VARCHAR(12)    NOT NULL COMMENT '险种类别',
    risktypename   VARCHAR(50)    NOT NULL COMMENT '险种类别名称',
    riskcode       VARCHAR(10)    NOT NULL COMMENT '险种代码',
    riskname       VARCHAR(50)    NOT NULL COMMENT '险种名称',
    dutycode       VARCHAR(12)    DEFAULT NULL COMMENT '责任编码',
    payplancode    VARCHAR(12)    DEFAULT NULL COMMENT '缴费编码',
    insuaccno      VARCHAR(12)    DEFAULT NULL COMMENT '保险账户号码',
    insuaccname    VARCHAR(50)    DEFAULT NULL COMMENT '保险账户名称',
    branchtype     VARCHAR(2)     NOT NULL COMMENT '渠道',
    payintv        VARCHAR(2)     DEFAULT NULL COMMENT '缴费方式',
    payyearsflag   VARCHAR(1)     DEFAULT NULL COMMENT '缴费年期单位',
    payyears       VARCHAR(2)     DEFAULT NULL COMMENT '缴费年期',
    yearsflag      VARCHAR(1)     DEFAULT NULL COMMENT '保险年期单位',
    years          VARCHAR(5)     DEFAULT NULL COMMENT '保险年期',
    standtype      VARCHAR(2)     DEFAULT NULL COMMENT '标准类型',
    agenttype      VARCHAR(2)     DEFAULT NULL COMMENT '业务类型',
    rate           DECIMAL(11, 3) NOT NULL COMMENT '加权比例',
    startdate      DATE           NOT NULL COMMENT '起期',
    enddate        DATE           NOT NULL COMMENT '止期',
    flag1          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG1',
    flag2          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG2',
    flag3          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG3',
    flag4          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG4',
    flag5          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG5',
    flag6          VARCHAR(20)    DEFAULT NULL COMMENT 'FLAG6',
    flag7          DECIMAL(10, 2) DEFAULT NULL COMMENT 'FLAG7',
    flag8          DECIMAL(10, 2) DEFAULT NULL COMMENT 'FLAG8',
    flag9          DECIMAL(10, 2) DEFAULT NULL COMMENT 'FLAG9',
    flag10         DATE           DEFAULT NULL COMMENT 'FLAG10',
    operator       VARCHAR(60)    DEFAULT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60)    NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (serialno)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管险种加权比例配置信息表';


-- ----------------------------
-- Table structure for launsplitrisk-销管险种拆分失败信息表
-- ----------------------------
DROP TABLE IF EXISTS launsplitrisk;
CREATE TABLE IF NOT EXISTS launsplitrisk
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    wageno         VARCHAR(10) NOT NULL COMMENT '薪资年月',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道',
    t1             VARCHAR(15) DEFAULT NULL COMMENT 'T1',
    t2             VARCHAR(15) DEFAULT NULL COMMENT 'T2',
    t3             VARCHAR(15) DEFAULT NULL COMMENT 'T3',
    t4             VARCHAR(15) DEFAULT NULL COMMENT 'T4',
    t5             VARCHAR(15) DEFAULT NULL COMMENT 'T5',
    t6             VARCHAR(15) DEFAULT NULL COMMENT 'T6',
    state          VARCHAR(2)  DEFAULT NULL COMMENT '状态',
    branchtype2    VARCHAR(2)  DEFAULT NULL COMMENT '子渠道',
    b1             VARCHAR(20) DEFAULT NULL COMMENT 'B1',
    b2             VARCHAR(15) DEFAULT NULL COMMENT 'B2',
    b3             VARCHAR(20) DEFAULT NULL COMMENT 'B3',
    k1             VARCHAR(15) DEFAULT NULL COMMENT 'K1',
    k2             VARCHAR(15) DEFAULT NULL COMMENT 'K2',
    k3             VARCHAR(15) DEFAULT NULL COMMENT 'K3',
    k4             VARCHAR(15) DEFAULT NULL COMMENT 'K4',
    k5             VARCHAR(15) DEFAULT NULL COMMENT 'K5',
    k6             VARCHAR(15) DEFAULT NULL COMMENT 'K6',
    k7             VARCHAR(15) DEFAULT NULL COMMENT 'K7',
    k8             VARCHAR(15) DEFAULT NULL COMMENT 'K8',
    k9             VARCHAR(15) DEFAULT NULL COMMENT 'K9',
    k10            VARCHAR(15) DEFAULT NULL COMMENT 'K10',
    operator       VARCHAR(60) DEFAULT NULL COMMENT '操作员代码',
    makedate       DATE        DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_wageno_managecom_branchtype (wageno, managecom, branchtype)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管险种拆分失败信息表';

-- ----------------------------
-- Table structure for laratecommisionb-销管险种规则配置信息备份表
-- ----------------------------
DROP TABLE IF EXISTS laratecommisionb;
CREATE TABLE IF NOT EXISTS laratecommisionb
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号码',
    serialno         VARCHAR(10) NOT NULL COMMENT '流水号',
    branchtype       VARCHAR(2)  NOT NULL COMMENT '展业类型',
    riskcode         VARCHAR(10) NOT NULL COMMENT '险种',
    sex              VARCHAR(1)     DEFAULT NULL COMMENT '性别',
    appage           INT            DEFAULT NULL COMMENT '投保年龄',
    year             INT            DEFAULT NULL COMMENT '保险年期',
    payintv          VARCHAR(2)     DEFAULT NULL COMMENT '交费间隔',
    curyear          INT            DEFAULT NULL COMMENT '保单年度',
    rate             DECIMAL(12, 6) DEFAULT NULL COMMENT '比率',
    managecom        VARCHAR(8)  NOT NULL COMMENT '管理机构',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    startdate        DATE           DEFAULT NULL COMMENT '使用起期',
    enddate          DATE           DEFAULT NULL COMMENT '使用止期',
    f01              VARCHAR(10)    DEFAULT NULL COMMENT '要素1',
    f02              VARCHAR(10)    DEFAULT NULL COMMENT '要素2',
    f03              DECIMAL(12, 6) DEFAULT NULL COMMENT '要素3',
    f04              DECIMAL(12, 6) DEFAULT NULL COMMENT '要素4',
    f05              VARCHAR(20)    DEFAULT NULL COMMENT '要素5',
    f06              VARCHAR(20)    DEFAULT NULL COMMENT '要素6',
    f07              VARCHAR(20)    DEFAULT NULL COMMENT '备用1',
    f08              VARCHAR(20)    DEFAULT NULL COMMENT '备用2',
    f09              VARCHAR(20)    DEFAULT NULL COMMENT '备用3',
    edortype         VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    operator         VARCHAR(60)    DEFAULT NULL COMMENT '操作员代码',
    makedate         DATE           DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME       DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (id),
    UNIQUE KEY uq_laratecommisionb (edorno, edortype, serialno)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管险种规则配置信息备份表';

-- ----------------------------
-- Table structure for laratecommision-销管险种规则配置信息表
-- ----------------------------
DROP TABLE IF EXISTS laratecommision;
CREATE TABLE IF NOT EXISTS laratecommision
(
    serialno
    VARCHAR(10)    NOT NULL COMMENT '流水号',
    branchtype     VARCHAR(2)     NOT NULL COMMENT '展业类型',
    riskcode       VARCHAR(10)    NOT NULL COMMENT '险种',
    sex            VARCHAR(1)     DEFAULT NULL COMMENT '性别',
    appage         INT            DEFAULT NULL COMMENT '投保年龄',
    year           INT            DEFAULT NULL COMMENT '保险年期',
    payintv        VARCHAR(2)     DEFAULT NULL COMMENT '交费间隔',
    curyear        INT            DEFAULT NULL COMMENT '保单年度',
    rate           DECIMAL(12, 6) NOT NULL COMMENT '比率',
    managecom      VARCHAR(8)     NOT NULL COMMENT '管理机构',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    startdate      DATE           DEFAULT NULL COMMENT '使用起期',
    enddate        DATE           DEFAULT NULL COMMENT '使用止期',
    f01            VARCHAR(10)    DEFAULT NULL COMMENT '要素1',
    f02            VARCHAR(10)    DEFAULT NULL COMMENT '要素2',
    f03            DECIMAL(12, 6) DEFAULT NULL COMMENT '要素3',
    f04            DECIMAL(12, 6) DEFAULT NULL COMMENT '要素4',
    f05            VARCHAR(20)    DEFAULT NULL COMMENT '要素5',
    f06            VARCHAR(20)    DEFAULT NULL COMMENT '要素6',
    f07            VARCHAR(20)    DEFAULT NULL COMMENT '备用1',
    f08            VARCHAR(20)    DEFAULT NULL COMMENT '备用2',
    f09            VARCHAR(20)    DEFAULT NULL COMMENT '备用3',
    operator       VARCHAR(60)    DEFAULT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60)    NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (serialno)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='销管险种规则配置信息表';

-- ----------------------------
-- Table structure for lawagesplitrisk-险种拆分信息表
-- ----------------------------
DROP TABLE IF EXISTS lawagesplitrisk;
CREATE TABLE IF NOT EXISTS lawagesplitrisk
(
    id
    bigint(20)  NOT NULL COMMENT '流水号',
    wageno         VARCHAR(10) NOT NULL COMMENT '佣金年月',
    riskcode       VARCHAR(10) NOT NULL COMMENT '险种代码',
    managecom      VARCHAR(8)  NOT NULL COMMENT '中支机构代码',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道',
    riskcodename   VARCHAR(50) NOT NULL COMMENT '险种名称',
    managecomname  VARCHAR(50)    DEFAULT NULL COMMENT '中支机构名称',
    t1             DECIMAL(15, 6) DEFAULT NULL COMMENT '首年直接佣金',
    t2             DECIMAL(15, 6) DEFAULT NULL COMMENT '续年直接佣金',
    t3             DECIMAL(15, 6) DEFAULT NULL COMMENT '《基本法》首年附加佣金',
    t4             DECIMAL(15, 6) DEFAULT NULL COMMENT '《基本法》续年附加佣金',
    t5             DECIMAL(15, 6) DEFAULT NULL COMMENT '《聘才计划》附加佣金',
    t6             DECIMAL(15, 6) DEFAULT NULL COMMENT '《新人计划》附加佣金',
    state          VARCHAR(2)     DEFAULT NULL COMMENT '状态',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '子渠道',
    b1             VARCHAR(20)    DEFAULT NULL COMMENT 'B1',
    b2             DECIMAL(15, 6) DEFAULT NULL COMMENT 'B2',
    b3             VARCHAR(20)    DEFAULT NULL COMMENT 'B3',
    k1             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K1',
    k2             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K2',
    k3             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K3',
    k4             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K4',
    k5             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K5',
    k6             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K6',
    k7             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K7',
    k8             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K8',
    k9             DECIMAL(15, 6) DEFAULT NULL COMMENT 'K9',
    k10            DECIMAL(15, 6) DEFAULT NULL COMMENT 'K10',
    operator       VARCHAR(60)    DEFAULT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_lawagesplitrisk (wageno, riskcode, managecom, branchtype)
    ) ENGINE = InnoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='险种拆分信息表';


DROP TABLE IF EXISTS lacalrewardlog;
-- 创建销管绩效计算日志表
CREATE TABLE `lacalrewardlog`
(
    `id`             bigint      NOT NULL COMMENT '流水号',
    `managecom`      varchar(8)  NOT NULL COMMENT '管理机构',
    `wageno`         varchar(6)  NOT NULL COMMENT '佣金年月',
    `state`          varchar(2)     DEFAULT NULL COMMENT '01-已计算未核发 02-已核发',
    `branchtype`     varchar(2)  NOT NULL COMMENT '渠道类型',
    `branchtype2`    varchar(2)     DEFAULT NULL COMMENT '子渠道类型',
    `healthflag`     varchar(2)  NOT NULL COMMENT '健康险标识:0-健康险 1-团险',
    `t1`             varchar(20)    DEFAULT NULL COMMENT 'T1',
    `t2`             varchar(20)    DEFAULT NULL COMMENT 'T2',
    `t3`             decimal(16, 4) DEFAULT NULL COMMENT 'T3',
    `t4`             decimal(16, 4) DEFAULT NULL COMMENT 'T4',
    `t5`             date           DEFAULT NULL COMMENT 'T5',
    `t6`             date           DEFAULT NULL COMMENT 'T6',
    `operator`       varchar(60) NOT NULL COMMENT '操作人',
    `makedate`       date        NOT NULL COMMENT '录入日期',
    `maketime`       varchar(8)  NOT NULL COMMENT '录入时间',
    `modifydate`     date        NOT NULL COMMENT '最近一次修改日期',
    `modifytime`     varchar(8)  NOT NULL COMMENT '最近一次修改时间',
    `modifyoperator` varchar(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_managecom_wageno_branchtype` (`managecom`, `wageno`, `branchtype`, `healthflag`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管绩效计算日志表';


DROP TABLE IF EXISTS lacommisionyb;
create table lacommisionyb
(
    commisionsn      varchar(10)    not null comment '系列号',
    commisionbaseno  varchar(10)    not null comment '系列原始号',
    wageno           varchar(20)    null comment '佣金年月',
    grpcontno        varchar(20)    not null comment '集体合同号码',
    grppolno         varchar(20)    not null comment '集体保单号码',
    contno           varchar(20)    not null comment '个人合同号码',
    polno            varchar(20)    not null comment '保单号码',
    mainpolno        varchar(20)    not null comment '投保单号码',
    proposalcontno   varchar(20)    not null comment '投保单号码',
    prtno            varchar(20)    not null comment '保单印刷号',
    managecom        varchar(10)    not null comment '管理机构',
    appntno          varchar(24)    null comment '投保人客户号码',
    appntname        varchar(100)   null comment '投保人姓名',
    appntid          varchar(30)    null comment '投保人证件号码',
    insuredno        varchar(24)    null comment '被保人客户号码',
    insuredname      varchar(100)   null comment '被保人姓名',
    insuredid        varchar(30)    null comment '被保人证件号码',
    riskcode         varchar(10)    null comment '险种编码',
    riskname         varchar(200)   null comment '险种名称',
    riskversion      varchar(8)     null comment '险种版本',
    riskmark         varchar(1)     null comment '主附险标志',
    dutycode         varchar(10)    null comment '责任编码',
    payplancode      varchar(10)    null comment '交费计划编码',
    cvalidate        date           null comment '保单生效日期',
    polapplydate     date           null comment '投保单申请日期',
    edorvalidate     date           null comment '保全生效日期',
    payintv          int            null comment '交费间隔',
    paymode          varchar(1)     null comment '交费方式',
    receiptno        varchar(20)    null comment '交费收据号',
    tpaydate         date           null comment '交易交费日期',
    tenteraccdate    date           null comment '交易到帐日期',
    tconfdate        date           null comment '交易确认日期',
    tmakedate        date           null comment '交易入机日期',
    commdate         date           null comment '报单失效日期或者理陪终止日期',
    transmoney       decimal(16, 2) null comment '交易金额',
    transstandmoney  decimal(16, 2) null comment '交易标准金额',
    amnt             decimal(16, 2) null comment '保额',
    lastpaytodate    date           null comment '原交至日期',
    curpaytodate     date           null comment '现交至日期',
    transtype        varchar(3)     null comment '交易类别',
    commdire         varchar(1)     null comment '佣金计算特征',
    transstate       varchar(10)    null comment '交易处理状态',
    directwage       decimal(12, 2) null comment '直接佣金',
    appendwage       decimal(12, 2) null comment '附加佣金',
    grpfyc           decimal(12, 2) null comment '组提佣金额',
    calcount         decimal(12, 2) null comment '统计件数',
    caldate          date           null comment '统计日期',
    standfycrate     decimal(12, 6) null comment '标准提佣比例',
    fycrate          decimal(12, 6) null comment '实际提佣比例',
    fyc              decimal(12, 2) null comment '实际提佣金额',
    depfyc           decimal(12, 2) null comment '部提佣金额',
    standpremrate    decimal(12, 6) null comment '折标比例',
    standprem        decimal(12, 2) null comment '折算标保',
    commcharge       decimal(12, 2) null comment '标准网点手续费',
    commcharge1      decimal(12, 2) null comment '标准分理处手续费',
    commcharge2      decimal(12, 2) null comment '标准支行手续费',
    commcharge3      decimal(12, 2) null comment '标准分行手续费',
    commcharge4      decimal(12, 2) null comment '标准总行手续费',
    grpfycrate       decimal(12, 6) null comment '组提奖比例',
    depfycrate       decimal(12, 6) null comment '部提奖比例',
    flag             varchar(10)    null comment '标志位',
    calcdate         date           null comment '复效日期/还垫日期',
    payyear          int            null comment '交费年度',
    payyears         int            null comment '交费年期',
    years            int            null comment '保险年期',
    paycount         int            null comment '第几次交费',
    signdate         date           not null comment '签单日期',
    getpoldate       date           null comment '保单送达日期',
    branchtype       varchar(2)     not null comment '展业类型',
    agentcom         varchar(20)    null comment '代理机构',
    bankserver       varchar(20)    null comment '柜员姓名',
    agenttype        varchar(20)    null comment '代理机构内部分类',
    agentcode        varchar(10)    not null comment '代理人编码',
    agentgroup       varchar(12)    not null comment '代理人展业机构代码',
    branchcode       varchar(12)    not null comment '代理人组别',
    branchseries     varchar(200)   not null comment '展业机构序列编码',
    branchattr       varchar(20)    null comment '展业机构外部编码',
    poltype          varchar(1)     null comment '保单类型',
    makepoldate      date           null comment '交单日期',
    customgetpoldate date           null comment '保单回执客户签收日期',
    scandate         date           null comment '扫描日期',
    branchtype2      varchar(2)     null comment '渠道',
    agentcode1       varchar(20)    null comment '代理人编码1',
    distict          varchar(12)    null comment '区',
    department       varchar(12)    null comment '部',
    riskchnl         varchar(2)     null comment '险种渠道',
    mainpolyear      int            null comment '主险保单年度',
    opersource       varchar(6)     null comment '业务来源',
    selltype         varchar(6)     null comment '业务类型',
    commchargerate   decimal(12, 6) null comment '标准手续费比例',
    remark           varchar(100)   null comment '备注',
    itemcode         varchar(12)    null comment '项目编码',
    returndate       date           null comment '回访日期',
    returntype       varchar(6)     null comment '回访类型',
    yearsflag        varchar(20)    null comment '保险年期单位',
    p1               decimal(16, 6) null comment '原基本比例',
    p2               decimal(16, 6) null comment '原业推比例',
    p3               decimal(16, 6) null comment '原直销比例',
    p4               decimal(16, 6) null comment 'Nafyc每月增量',
    p5               decimal(16, 2) null comment '自动垫交标记',
    p6               decimal(16, 2) null comment '保单类别标志',
    p7               decimal(16, 2) null comment '属性7',
    p8               decimal(16, 2) null comment '属性8',
    p9               decimal(16, 2) null comment '属性9',
    p10              decimal(16, 2) null comment '属性10',
    p11              varchar(60)    null comment '属性11',
    p12              varchar(24)    null comment '属性12',
    p13              varchar(20)    null comment '属性13',
    p14              varchar(20)    null comment '属性14',
    p15              varchar(20)    null comment '属性15',
    p16              varchar(60)    null comment '联系人电话',
    f1               varchar(10)    null comment '交易业务属性1',
    f2               varchar(10)    null comment '交易业务属性2',
    f3               varchar(10)    null comment '交易业务属性3',
    f4               varchar(10)    null comment '交易业务属性4',
    f5               varchar(20)    null comment '交易业务属性5',
    f6               varchar(10)    null comment '交易业务属性6',
    f7               varchar(10)    null comment '交易业务属性7',
    f8               varchar(10)    null comment '交易业务属性8',
    f9               varchar(20)    null comment '交易业务属性9',
    f10              varchar(20)    null comment '交易业务属性10',
    k1               decimal(16, 6) null comment '业务推动比例',
    k2               decimal(16, 2) null comment '业务推动佣金',
    k3               decimal(16, 6) null comment '直销奖励比例',
    k4               decimal(16, 2) null comment '直销奖励佣金',
    k5               decimal(16, 2) null comment '业务费用5',
    b1               varchar(10)    null comment '备用字段1',
    b2               varchar(20)    null comment '备用字段2',
    b3               varchar(100)   null comment '备用字段3',
    b4               int            null comment '备用字段4',
    b5               int            null comment '备用字段5',
    b6               decimal(10, 2) null comment '备用字段6',
    b7               decimal(10, 2) null comment '备用字段7',
    b8               date           null comment '备用字段8',
    b9               date           null comment '备用字段9',
    operator         VARCHAR(60)    NOT NULL COMMENT '操作员',
    makedate         DATE           NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     NOT NULL COMMENT '入机时间',
    modifyoperator   VARCHAR(60)    NOT NULL COMMENT '最后一次修改人',
    modifydate       DATE           NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     NOT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (`commisionsn`),
    KEY `idx_lacommisionyb_index1` (`branchtype`),
    KEY `idx_lacommisionyb_index2` (`managecom`),
    KEY `idx_lacommisionyb_index3` (`agentcode`),
    KEY `idx_lacommisionyb_index4` (`contno`),
    KEY `idx_lacommisionyb_index5` (`wageno`),
    KEY `idx_lacommisionyb_index6` (`agentgroup`),
    KEY `idx_lacommisionyb_index7` (`signdate`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管扎帐信息表-银保';

DROP TABLE IF EXISTS lmduty;
create table lmduty
(
    dutycode          varchar(10)    not null comment '责任代码'
        primary key,
    dutyname          varchar(80)    not null comment '责任名称'
)
    comment '责任信息表';




