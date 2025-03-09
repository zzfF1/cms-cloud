DROP TABLE IF EXISTS lalocation;
/*==============================================================*/
/* Table: lalocation                                 */
/*==============================================================*/
CREATE TABLE lalocation
(
    id             BIGINT(20) NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道',
    longitude      VARCHAR(20) NOT NULL COMMENT '经度',
    latitude       VARCHAR(20) NOT NULL COMMENT '纬度',
    operator       VARCHAR(60) NOT NULL COMMENT '操作人',
    makedate       DATE        NOT NULL COMMENT '创建日期',
    maketime       VARCHAR(10) NOT NULL COMMENT '创建时间',
    modifydate     DATE        NOT NULL COMMENT '修改日期',
    modifytime     VARCHAR(10) NOT NULL COMMENT '修改时间',
    modifyoperator VARCHAR(60) not null comment '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_manage_branch_id (managecom, branchtype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='机构定位信息表';


DROP TABLE IF EXISTS lacont;
/*==============================================================*/
/* Table: LACONT                                                */
/*==============================================================*/
create table lacont
(
    id              bigint      not null comment '主键'
        primary key,
    protocolno      varchar(20) not null comment '合同书号',
    signdate        date null comment '协议签订日期',
    protocoltype    varchar(1) null comment '协议书类型',
    protocolcontent varchar(500) null comment '协议书内容',
    agentcom        varchar(20) null comment '签署中介机构',
    managecom       varchar(8) null comment '签署管理机构',
    startdate       date null comment '生效起期',
    enddate         date null comment '生效止期',
    representa      varchar(20) null comment '签署甲方代表人',
    representb      varchar(20) null comment '主协议编码',
    fellbackduty    varchar(1200) null comment '违约责任',
    noti            varchar(120) null comment '备注',
    branchtype      varchar(2) null comment '展业类型',
    settleacc       varchar(200) null comment '备用字段',
    agentstatu      varchar(10) null comment '中介机构状态（有效、无效）',
    protocolstatu   varchar(10) null comment '协议状态（生效、无效）',
    businessident   varchar(20) not null comment '业务标识（健康险业务、团险业务）',
    operator        varchar(60) not null comment '操作员',
    makedate        date        not null comment '入机日期',
    maketime        varchar(8)  not null comment '入机时间',
    modifydate      date        not null comment '最后一次修改日期',
    modifytime      varchar(8)  not null comment '最后一次修改时间',
    modifyoperator  varchar(20) not null comment '最后一次修改人',
    constraint protocolno
        unique (protocolno)
) comment '销管中介协议信息表';



DROP TABLE IF EXISTS lacontb;
/*==============================================================*/
/* Table: LACONTB                                               */
/*==============================================================*/
-- auto-generated definition
create table lacontb
(
    edorno           varchar(20) not null comment '批改号'
        primary key,
    id               bigint      not null comment '主表主键',
    edortype         varchar(2) null comment '转储类别',
    protocolno       varchar(20) not null comment '合同书号码',
    signdate         date null comment '协议签订日期',
    protocoltype     varchar(1) null comment 'state(0-录入；1-待审核；2-审核通过)',
    protocolcontent  varchar(500) null comment '协议书内容',
    agentcom         varchar(20) null comment '签署中介机构',
    managecom        varchar(8) null comment '签署管理机构',
    startdate        date null comment '生效起期',
    enddate          date null comment '生效止期',
    representa       varchar(20) null comment '签署甲方代表人',
    representb       varchar(20) null comment '签署乙方代表人',
    fellbackduty     varchar(1200) null comment '违约责任',
    noti             varchar(120) null comment '备注',
    branchtype       varchar(2) null comment '展业类型',
    operator         varchar(60) not null comment '操作员代码',
    makedate         date null comment '入机日期',
    maketime         varchar(8) null comment '入机时间',
    modifydate       date null comment '最后一次修改日期',
    modifytime       varchar(8) null comment '最后一次修改时间',
    modifyoperator   varchar(20) not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '销管中介协议信息备份表';



DROP TABLE IF EXISTS lacomb;
/*==============================================================*/
/* Table: LACOMB                                                */
/*==============================================================*/
create table lacomb
(
    edorno           varchar(20) not null comment '转储号码'
        primary key,
    edortype         varchar(2)  not null comment '转储类型',
    managecom        varchar(8) null comment '管理机构',
    upagentcom       varchar(20) null comment '上级中介机构',
    agentcom         varchar(20) not null comment '中介机构',
    name             varchar(120) null comment '中介机构名称',
    address          varchar(120) null comment '地址',
    zipcode          varchar(6) null comment '邮编',
    corporation      varchar(20) null comment '法人',
    linkman          varchar(10) null comment '联系人',
    phone            varchar(18) null comment '联系电话',
    email            varchar(50) null comment '邮箱',
    webaddress       varchar(100) null comment '网址',
    banktype         varchar(2) null comment '中介机构级别',
    branchtype       varchar(2) null comment '展业类型',
    licenseno        varchar(30) null comment '监督系统机构编码',
    agentstate       varchar(30) null comment '中介机构状态',
    founddate        date null comment '签约日期',
    sellflag         varchar(1) null comment '销售资格',
    state            varchar(1) null comment '停业标志',
    enddate          date null comment '止约日期',
    grpnature        varchar(10) null comment '单位性质',
    bankaccno        varchar(40) null comment '银行帐号',
    actype           varchar(2) null comment '机构类别',
    areatype         varchar(2)  not null comment '地区类型',
    channeltype      varchar(2)  not null comment '渠道类型',
    fax              varchar(18) null comment '机构传真',
    password         varchar(16) null comment '密码',
    bankcode         varchar(20) null comment '银行编码',
    calflag          varchar(2) null comment '是否统计网点合格率',
    busilicensecode  varchar(30) null comment '工商执照编码',
    insureid         varchar(12) null comment '保险公司id',
    insureprincipal  varchar(8) null comment '保险公司负责人',
    chiefbusiness    varchar(80) null comment '主营业务',
    busiaddress      varchar(80) null comment '营业地址',
    subscribeman     varchar(8) null comment '签署人',
    subscribemanduty varchar(20) null comment '签署人职务',
    regionalismcode  varchar(6) null comment '行政区划代码',
    appagentcom      varchar(20) null comment '上报代码',
    noti             varchar(100) null comment '相关说明',
    businesscode     varchar(20) null comment '行业代码',
    licensestartdate date null comment '许可证登记日期',
    licenseenddate   date null comment '许可证截至日期',
    branchtype2      varchar(2) null comment '渠道',
    assets           decimal(12, 2) null comment '资产',
    income           decimal(12, 2) null comment '营业收入',
    profits          decimal(12, 2) null comment '营业利润',
    personnalsum     int null comment '机构人数',
    businesstype     varchar(20) null comment '行业分类',
    protocalno       varchar(20) null comment '合同编码',
    headoffice       varchar(20) null comment '所属总行',
    busstartdate     date null comment '营业执照有效起期',
    busenddate       date null comment '营业执照有效止期',
    orgstartdate     date null comment '组织机构代码有效起期',
    orgenddate       date null comment '组织机构代码有效止期',
    organ            varchar(20) null comment '组织机构代码',
    attributehall    varchar(2) null comment '营业厅属性',
    dormancydate     date null comment '休眠日期',
    restartdate      date null comment '重启日期',
    dormancyflag     varchar(1) null comment '是否已休眠',
    restartflag      varchar(1) null comment '是否已重启',
    cooperationtype  varchar(20) null comment '合作类型',
    internetflag     varchar(10) null comment '互联网业务标志',
    coopscope        varchar(20) null comment '互联网合作范围',
    coopwebsite      varchar(20) null comment '互联网合作网站地址',
    intbizstart      date null comment '互联网业务开始日期',
    intbizend        date null comment '互联网业务结束日期',
    legalresponsible varchar(20) null comment '法人负责人姓名',
    regjurisdiction  varchar(20) null comment '监管辖区代码',
    operator         varchar(60) not null comment '操作员',
    makedate         date        not null comment '入机日期',
    maketime         varchar(8)  not null comment '入机时间',
    modifydate       date        not null comment '最后一次修改日期',
    modifytime       varchar(8)  not null comment '最后一次修改时间',
    modifyoperator   varchar(20) not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '销管中介机构信息备份表' engine = InnoDB;



DROP TABLE IF EXISTS lacom;
/*==============================================================*/
/* Table: LACOM                                                 */
/*==============================================================*/
-- auto-generated definition
create table lacom
(
    managecom        varchar(8) null comment '管理机构',
    upagentcom       varchar(20) null comment '上级中介机构',
    agentcom         varchar(20) not null comment '中介机构'
        primary key,
    name             varchar(120) null comment '中介机构名称',
    address          varchar(120) null comment '地址',
    zipcode          varchar(6) null comment '邮编',
    corporation      varchar(20) null comment '法人',
    linkman          varchar(10) null comment '联系人',
    phone            varchar(18) null comment '联系电话',
    email            varchar(50) null comment '邮箱',
    webaddress       varchar(100) null comment '网址',
    banktype         varchar(2) null comment '中介机构级别',
    branchtype       varchar(2) null comment '展业类型',
    licenseno        varchar(30) null comment '监督系统机构编码',
    agentstate       varchar(30) null comment '中介机构状态',
    founddate        date null comment '签约日期',
    sellflag         varchar(1) null comment '销售资格',
    state            varchar(1) null comment '停业标志',
    enddate          date null comment '止约日期',
    grpnature        varchar(10) null comment '单位性质',
    bankaccno        varchar(40) null comment '银行帐号',
    actype           varchar(2) null comment '机构类别',
    areatype         varchar(2)  not null comment '地区类型',
    channeltype      varchar(2)  not null comment '渠道类型',
    fax              varchar(18) null comment '机构传真',
    password         varchar(16) null comment '密码',
    bankcode         varchar(20) null comment '银行编码',
    calflag          varchar(2) null comment '是否统计网点合格率',
    busilicensecode  varchar(30) null comment '工商执照编码',
    insureid         varchar(12) null comment '保险公司id',
    insureprincipal  varchar(8) null comment '保险公司负责人',
    chiefbusiness    varchar(80) null comment '主营业务',
    busiaddress      varchar(80) null comment '营业地址',
    subscribeman     varchar(8) null comment '签署人',
    subscribemanduty varchar(20) null comment '签署人职务',
    regionalismcode  varchar(6) null comment '行政区划代码',
    appagentcom      varchar(20) null comment '上报代码',
    noti             varchar(100) null comment '相关说明',
    businesscode     varchar(20) null comment '行业代码',
    licensestartdate date null comment '许可证登记日期',
    licenseenddate   date null comment '许可证截至日期',
    branchtype2      varchar(2) null comment '渠道',
    assets           decimal(12, 2) null comment '资产',
    income           decimal(12, 2) null comment '营业收入',
    profits          decimal(12, 2) null comment '营业利润',
    personnalsum     int null comment '机构人数',
    businesstype     varchar(20) null comment '行业分类',
    protocalno       varchar(20) null comment '合同编码',
    headoffice       varchar(20) null comment '所属总行',
    busstartdate     date null comment '营业执照有效起期',
    busenddate       date null comment '营业执照有效止期',
    orgstartdate     date null comment '组织机构代码有效起期',
    orgenddate       date null comment '组织机构代码有效止期',
    organ            varchar(20) null comment '组织机构代码',
    attributehall    varchar(2) null comment '营业厅属性',
    dormancydate     date null comment '休眠日期',
    restartdate      date null comment '重启日期',
    dormancyflag     varchar(1) null comment '是否已休眠',
    restartflag      varchar(1) null comment '是否已重启',
    cooperationtype  varchar(20) null comment '合作类型',
    internetflag     varchar(10) null comment '互联网业务标志',
    coopscope        varchar(20) null comment '互联网合作范围',
    coopwebsite      varchar(20) null comment '互联网合作网站地址',
    intbizstart      date null comment '互联网业务开始日期',
    intbizend        date null comment '互联网业务结束日期',
    legalresponsible varchar(20) null comment '法人负责人姓名',
    regjurisdiction  varchar(20) null comment '监管辖区代码',
    operator         varchar(60) not null comment '操作员',
    makedate         date        not null comment '入机日期',
    maketime         varchar(8)  not null comment '入机时间',
    modifydate       date        not null comment '最后一次修改日期',
    modifytime       varchar(8)  not null comment '最后一次修改时间',
    modifyoperator   varchar(20) not null comment '最后一次修改人'
) comment '销管中介机构信息表';

DROP TABLE IF EXISTS lacomcardinfo;
/*==============================================================*/
/* Table: lacomcardinfo                                             */
/*==============================================================*/
create table lacomcardinfo
(
    id             bigint      not null comment '流水号'
        primary key,
    managecom      varchar(8)  not null comment '管理机构',
    agentcom       varchar(20) not null comment '中介机构',
    cardtype       varchar(2)  not null comment '证件类型 01-营业执照、02-许可证、03-组织机构、04-准入评估、05-其他',
    cardno         varchar(50) null comment '证件编码',
    startdate      date null comment '有效起期',
    enddate        date null comment '有效止期',
    year           varchar(10) null comment '年度',
    buscope        varchar(500) null comment '业务范围',
    area           varchar(500) null comment '经营区域',
    path           varchar(50) null comment '附件地址',
    branchtype     varchar(2)  not null comment '渠道',
    t1             varchar(100) null comment '备用1',
    t2             varchar(100) null comment '备用2',
    t3             decimal(12, 2) null comment '备用3',
    t4             decimal(12, 2) null comment '备用4',
    t5             date null comment '备用5',
    t6             date null comment '备用6',
    operator       varchar(60) not null comment '操作员',
    makedate       date        not null comment '入机日期',
    maketime       varchar(8)  not null comment '入机时间',
    modifydate     date        not null comment '最后一次修改日期',
    modifytime     varchar(8)  not null comment '最后一次修改时间',
    modifyoperator varchar(60) not null comment '最后一次修改人'
) comment '中介机构证件信息表' engine = InnoDB;

DROP TABLE IF EXISTS lacomcardinfob;
/*==============================================================*/
/* Table: lacomcardinfob                                             */
/*==============================================================*/
create table lacomcardinfob
(
    edorno           bigint      not null comment '转储号码'
        primary key,
    id               bigint      not null comment '流水号',
    edortype         varchar(2)  not null comment '转储类型',
    managecom        varchar(8)  not null comment '管理机构',
    agentcom         varchar(20) not null comment '中介机构',
    cardtype         varchar(2)  not null comment '证件类型 01-营业执照、02-许可证、03-组织机构、04-准入评估、05-其他',
    cardno           varchar(50) null comment '证件编码',
    startdate        date null comment '有效起期',
    enddate          date null comment '有效止期',
    year             varchar(10) null comment '年度',
    buscope          varchar(500) null comment '业务范围',
    area             varchar(500) null comment '经营区域',
    path             varchar(50) null comment '附件地址',
    branchtype       varchar(2)  not null comment '渠道',
    t1               varchar(100) null comment '备用1',
    t2               varchar(100) null comment '备用2',
    t3               decimal(12, 2) null comment '备用3',
    t4               decimal(12, 2) null comment '备用4',
    t5               date null comment '备用5',
    t6               date null comment '备用6',
    operator         varchar(60) not null comment '操作员',
    makedate         date        not null comment '入机日期',
    maketime         varchar(8)  not null comment '入机时间',
    modifydate       date        not null comment '最后一次修改日期',
    modifytime       varchar(8)  not null comment '最后一次修改时间',
    modifyoperator   varchar(60) not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '中介机构证件信息备份表';

DROP TABLE IF EXISTS laqualificationendwarn;
/*==============================================================*/
/* Table: laqualificationendwarn                                             */
/*==============================================================*/
create table laqualificationendwarn
(
    seriesno       bigint       not null comment '序列号'
        primary key,
    managecom      varchar(10)  not null comment '管理机构编码',
    agentcom       varchar(20)  not null comment '代理机构编码',
    username       varchar(20)  not null comment '内勤人员姓名',
    email          varchar(100) not null comment '邮箱',
    branchtype     varchar(2)   not null comment '渠道类型',
    sort           decimal(4, 2) default 0.00 null comment '排序',
    remark         varchar(200) null comment '备注',
    f01            varchar(50) null,
    f02            varchar(10) null,
    f03            varchar(20) null,
    f04            varchar(20) null,
    f05            decimal(12, 6) null,
    f06            decimal(12, 6) null,
    operator       varchar(60)  not null comment '操作员',
    makedate       date         not null comment '入机日期',
    maketime       varchar(8)   not null comment '入机时间',
    modifydate     date         not null comment '最后一次修改日期',
    modifytime     varchar(8)   not null comment '最后一次修改时间',
    modifyoperator varchar(60)  not null comment '最后一次修改人'
) comment '资质到期预警邮件配置';

DROP TABLE IF EXISTS laqualificationendwarnb;
/*==============================================================*/
/* Table: laqualificationendwarnb                                             */
/*==============================================================*/
create table laqualificationendwarnb
(
    edorno           bigint       not null comment '转储号码'
        primary key,
    seriesno         bigint null comment '序列号',
    edortype         varchar(2)   not null comment '转储类型',
    managecom        varchar(10)  not null comment '管理机构编码',
    agentcom         varchar(20)  not null comment '代理机构编码',
    username         varchar(20)  not null comment '内勤人员姓名',
    email            varchar(100) not null comment '邮箱',
    branchtype       varchar(2)   not null comment '渠道类型',
    sort             decimal(4, 2) default 0.00 null comment '排序',
    remark           varchar(200) null comment '备注',
    f01              varchar(50) null,
    f02              varchar(10) null,
    f03              varchar(20) null,
    f04              varchar(20) null,
    f05              decimal(12, 6) null,
    f06              decimal(12, 6) null,
    operator         varchar(60)  not null comment '操作员',
    makedate         date         not null comment '入机日期',
    maketime         varchar(8)   not null comment '入机时间',
    modifydate       date         not null comment '最后一次修改日期',
    modifytime       varchar(8)   not null comment '最后一次修改时间',
    modifyoperator   varchar(60)  not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '资质到期预警邮件配置备份表';

DROP TABLE IF EXISTS lacombankinfo;
/*==============================================================*/
/* Table: lacombankinfo                                             */
/*==============================================================*/
create table lacombankinfo
(
    id             bigint       not null comment '流水号'
        primary key,
    managecom      varchar(8)   not null comment '管理机构',
    agentcom       varchar(20)  not null comment '中介机构',
    bankcode       varchar(20)  not null comment '银行名称',
    bankname       varchar(100) not null comment '账户名称',
    bankaccno      varchar(50)  not null comment '银行账号',
    zdcflag        varchar(2)   not null comment '中电财标识 Y-是，N-否',
    branchtype     varchar(2)   not null comment '渠道',
    t1             varchar(100) null comment '备用1',
    t2             varchar(100) null comment '备用2',
    t3             decimal(12, 2) null comment '备用3',
    t4             decimal(12, 2) null comment '备用4',
    t5             date null comment '备用5',
    t6             date null comment '备用6',
    operator       varchar(60)  not null comment '操作员',
    makedate       date         not null comment '入机日期',
    maketime       varchar(8)   not null comment '入机时间',
    modifydate     date         not null comment '最后一次修改日期',
    modifytime     varchar(8)   not null comment '最后一次修改时间',
    modifyoperator varchar(60)  not null comment '最后一次修改人'
) comment '中介机构银行信息表';

-- auto-generated definition
create table lacombankinfob
(
    edorno           bigint       not null comment '流水号'
        primary key,
    id               bigint       not null comment '主表主键',
    edortype         varchar(2)   not null comment '转储类型',
    managecom        varchar(8)   not null comment '管理机构',
    agentcom         varchar(20)  not null comment '中介机构',
    bankcode         varchar(20)  not null comment '银行名称',
    bankname         varchar(100) not null comment '账户名称',
    bankaccno        varchar(50)  not null comment '银行账号',
    zdcflag          varchar(2)   not null comment '中电财标识 Y-是，N-否',
    branchtype       varchar(2)   not null comment '渠道',
    t1               varchar(100) null comment '备用1',
    t2               varchar(100) null comment '备用2',
    t3               decimal(12, 2) null comment '备用3',
    t4               decimal(12, 2) null comment '备用4',
    t5               date null comment '备用5',
    t6               date null comment '备用6',
    operator         varchar(60)  not null comment '操作员',
    makedate         date         not null comment '入机日期',
    maketime         varchar(8)   not null comment '入机时间',
    modifydate       date         not null comment '最后一次修改日期',
    modifytime       varchar(8)   not null comment '最后一次修改时间',
    modifyoperator   varchar(60)  not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '中介机构银行信息备份表';


DROP TABLE IF EXISTS lacomvaluation;
/*==============================================================*/
/* Table: lacomvaluation                                             */
/*==============================================================*/
create table lacomvaluation
(
    id              bigint      not null comment '流水号'
        primary key,
    agentcom        varchar(20) not null comment '中介结构编码',
    valuationtype   varchar(2)  not null comment '评估类型',
    title           varchar(100) null comment '评估事项标题',
    valuationresult varchar(200) null comment '评估结果',
    description     varchar(300) null comment '整改完成情况/年度综合性评价',
    remark          varchar(200) null comment '其他',
    valuationperson varchar(60) null comment '评估人',
    valuationdate   date null comment '评估日期',
    path            varchar(100) null comment '附件路径',
    t1              varchar(100) null comment '备用1',
    t2              varchar(100) null comment '备用2',
    t3              decimal(12, 2) null comment '备用3',
    t4              decimal(12, 2) null comment '备用4',
    t5              date null comment '备用5',
    t6              date null comment '备用6',
    branchtype      varchar(2)  not null comment '渠道',
    operator        varchar(60) not null comment '操作员代码',
    makedate        date        not null comment '入机日期',
    maketime        varchar(8)  not null comment '入机时间',
    modifydate      date        not null comment '最后一次修改日期',
    modifytime      varchar(8)  not null comment '最后一次修改时间',
    modifyoperator  varchar(60) not null comment '最后一次修改人'
) comment '评估信息表';

DROP TABLE IF EXISTS lacomviolationinfo;
/*==============================================================*/
/* Table: lacomviolationinfo                                             */
/*==============================================================*/
create table lacomviolationinfo
(
    id             bigint      not null comment '流水号'
        primary key,
    managecom      varchar(8)  not null comment '管理机构',
    agentcom       varchar(20) not null comment '中介机构',
    punishtitle    varchar(150) null comment '处罚机构',
    punishdate     date null comment '处罚时间',
    punishreason   varchar(1500) null comment '处罚事项',
    branchtype     varchar(2)  not null comment '渠道',
    t1             varchar(100) null comment '备用1',
    t2             varchar(100) null comment '备用2',
    t3             decimal(12, 2) null comment '备用3',
    t4             decimal(12, 2) null comment '备用4',
    t5             date null comment '备用5',
    t6             date null comment '备用6',
    operator       varchar(60) not null comment '操作员',
    makedate       date        not null comment '入机日期',
    maketime       varchar(8)  not null comment '入机时间',
    modifydate     date        not null comment '最后一次修改日期',
    modifytime     varchar(8)  not null comment '最后一次修改时间',
    modifyoperator varchar(60) not null comment '最后一次修改人'
) comment '中介机构违规信息表';

DROP TABLE IF EXISTS lacomauthorize;
/*==============================================================*/
/* Table: lacomauthorize                                             */
/*==============================================================*/
create table lacomauthorize
(
    id              bigint      not null comment '主键'
        primary key,
    riskcode        varchar(10) not null comment '险种编码',
    agentcom        varchar(20) not null comment '中介机构编码',
    protocolno      varchar(20) null comment '协议书号',
    salestate       varchar(2) null comment '销售状态',
    riskstate       varchar(2) null comment '产品状态',
    chargerate      decimal(16, 4) null comment '约定中介手续费比例',
    noti            varchar(500) null comment '备注',
    authorstartdate date null comment '授权起期',
    authorenddate   date null comment '授权止期',
    branchtype      varchar(2) null comment '渠道',
    branchtype2     varchar(2) null comment '子渠道',
    businessident   varchar(20) not null comment '业务标识（健康险业务、团险业务）',
    f1              varchar(50) null comment '备用字段1',
    f2              varchar(50) null comment '备用字段2',
    f3              varchar(100) null comment '备用字段3',
    f4              varchar(100) null comment '备用字段4',
    f5              decimal(16, 4) null comment '备用字段5',
    f6              decimal(16, 4) null comment '备用字段6',
    f7              datetime null comment '备用字段7',
    f8              datetime null comment '备用字段8',
    operator        varchar(60) not null comment '操作员',
    makedate        date        not null comment '入机日期',
    maketime        varchar(8)  not null comment '入机时间',
    modifydate      date        not null comment '最后一次修改日期',
    modifytime      varchar(8)  not null comment '最后一次修改时间',
    modifyoperator  varchar(20) not null comment '最后一次修改人'
) comment '产品授权表';

DROP TABLE IF EXISTS lacomauthorizeb;
/*==============================================================*/
/* Table: lacomauthorizeb                                             */
/*==============================================================*/
create table lacomauthorizeb
(
    edorno           bigint      not null comment '流水号'
        primary key,
    id               bigint      not null comment '主表主键',
    edortype         varchar(2)  not null comment '转储类型',
    riskcode         varchar(10) not null comment '险种编码',
    agentcom         varchar(20) not null comment '中介机构编码',
    protocolno       varchar(20) null comment '协议书号',
    salestate        varchar(2) null comment '销售状态',
    riskstate        varchar(2) null comment '产品状态',
    chargerate       decimal(16, 4) null comment '约定中介手续费比例',
    noti             varchar(500) null comment '备注',
    authorstartdate  date null comment '授权起期',
    authorenddate    date null comment '授权止期',
    branchtype       varchar(2) null comment '渠道',
    branchtype2      varchar(2) null comment '子渠道',
    businessident    varchar(20) not null comment '业务标识（健康险业务、团险业务）',
    f1               varchar(50) null comment '备用字段1',
    f2               varchar(50) null comment '备用字段2',
    f3               varchar(100) null comment '备用字段3',
    f4               varchar(100) null comment '备用字段4',
    f5               decimal(16, 4) null comment '备用字段5',
    f6               decimal(16, 4) null comment '备用字段6',
    f7               datetime null comment '备用字段7',
    f8               datetime null comment '备用字段8',
    operator         varchar(60) not null comment '操作员',
    makedate         date        not null comment '入机日期',
    maketime         varchar(8)  not null comment '入机时间',
    modifydate       date        not null comment '最后一次修改日期',
    modifytime       varchar(8)  not null comment '最后一次修改时间',
    modifyoperator   varchar(20) not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) comment '产品授权备份表';



DROP TABLE IF EXISTS lacomfile;
/*==============================================================*/
/* Table: lacomfile                                             */
/*==============================================================*/
CREATE TABLE lacomfile
(
    id             BIGINT(20) NOT NULL COMMENT '流水号',
    filecode       VARCHAR(10) NOT NULL COMMENT '文件编码',
    agentcom       VARCHAR(20) NOT NULL COMMENT '中介机构代码',
    actype         VARCHAR(2)  NOT NULL COMMENT '中介机构类别',
    managecom      VARCHAR(8) COMMENT '管理机构编码',
    filetype       VARCHAR(2)  NOT NULL COMMENT '文件类型',
    filename       VARCHAR(50) COMMENT '文件名称',
    filepath       VARCHAR(100) COMMENT '文件存储路径',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '渠道',
    spare1         VARCHAR(50) COMMENT '备用1',
    spare2         VARCHAR(50) COMMENT '备用2',
    spare3         DATE COMMENT '备用3',
    spare4         DATE COMMENT '备用4',
    spare5         DECIMAL(12, 2) COMMENT '备用5',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_filecode_agentcom (filecode, agentcom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管中介机构文件存储信息表';


DROP TABLE IF EXISTS lacombank;
/*==============================================================*/
/* Table: lacombank                                             */
/*==============================================================*/
CREATE TABLE lacombank
(
    id             BIGINT(20) NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    agentcom       VARCHAR(20) NOT NULL COMMENT '代理机构',
    bankname       VARCHAR(60) NOT NULL COMMENT '收入户银行名称',
    bankaccno      VARCHAR(40) NOT NULL COMMENT '收入户银行账号',
    standbyflag1   VARCHAR(40) COMMENT '预留1',
    standbyflag2   VARCHAR(40) COMMENT '预留2',
    standbyflag3   VARCHAR(40) COMMENT '预留3',
    standbyflag4   VARCHAR(40) COMMENT '预留4',
    standbyflag5   VARCHAR(40) COMMENT '预留5',
    standbyflag6   VARCHAR(40) COMMENT '预留6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_managecom_agentcom_bankaccno (managecom, agentcom, bankaccno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='销管中介机构银行信息表';

DROP TABLE IF EXISTS lacomtoagent;
create table lacomtoagent
(
    id             BIGINT(19) not null primary key,
    agentcom       VARCHAR(20) not null COMMENT '中介机构编码',
    branchtype     VARCHAR(2)  not null COMMENT '渠道',
    agentcode      VARCHAR(10) not null COMMENT '业务员工号',
    agentgroup     VARCHAR(12) null COMMENT '销售机构',
    startdate      DATE null COMMENT '开始日期',
    enddate        DATE null COMMENT '终止日期',
    rate           DECIMAL(12, 6) null COMMENT '比例',
    optflag        VARCHAR(2) null COMMENT '标志',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    UNIQUE INDEX unique_lacomtoagent_agentcom_agentcode (agentcom, agentcode),
    KEY `idx_lacomtoagent_index1` (`branchtype`),
    KEY `idx_lacomtoagent_index2` (`agentcode`),
    KEY `idx_lacomtoagent_index3` (`agentcom`),
    KEY `idx_lacomtoagent_index4` (`startdate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='中介机构分配表';

DROP TABLE IF EXISTS lacomtoagentb;
create table lacomtoagentb
(
    edorno bigint not null comment '流水号' primary key,
    edortype         varchar(2)  not null comment '转储类型',
    id               bigint      not null comment '主表主键',
    agentcom       VARCHAR(20) not null COMMENT '中介机构编码',
    branchtype     VARCHAR(2)  not null COMMENT '渠道',
    agentcode      VARCHAR(10) not null COMMENT '业务员工号',
    agentgroup     VARCHAR(12) null COMMENT '销售机构',
    startdate      DATE null COMMENT '开始日期',
    enddate        DATE null COMMENT '终止日期',
    rate           DECIMAL(12, 6) null COMMENT '比例',
    optflag        VARCHAR(2) null COMMENT '标志',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='中介机构分配轨迹表';

