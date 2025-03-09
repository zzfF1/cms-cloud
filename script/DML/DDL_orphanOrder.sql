DROP TABLE IF EXISTS laorphanpolicy;
-- 创建销管孤儿单分配信息表
CREATE TABLE laorphanpolicy
(
    contno         VARCHAR(20) NOT NULL COMMENT '保单号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    appntno        VARCHAR(24) NOT NULL COMMENT '投保人客户号',
    zipcode        VARCHAR(6) COMMENT '邮政编码',
    reasontype     VARCHAR(1) COMMENT '产生原因类型',
    t1             VARCHAR(10) COMMENT '备用1',
    t2             VARCHAR(10) COMMENT '备用2',
    branchtype     VARCHAR(2) COMMENT '展业类型',
    branchtype2    VARCHAR(2) COMMENT '渠道',
    agentgroup     VARCHAR(12) COMMENT '展业机构',
    flag           VARCHAR(1) COMMENT '孤儿单状态',
    saleflag       VARCHAR(1) COMMENT '个销团标志',
    healthflag          varchar(2) not null comment '健康险标识 0-健康险，1-非健康险',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (contno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配信息表';

DROP TABLE IF EXISTS laorphanpolicyb;
-- 创建销管孤儿单分配信息备份表
CREATE TABLE laorphanpolicyb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)  NOT NULL COMMENT '转储类型',
    contno           VARCHAR(20) NOT NULL COMMENT '保单号',
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    managecom        VARCHAR(8)  NOT NULL COMMENT '管理机构',
    appntno          VARCHAR(24) NOT NULL COMMENT '投保人客户号',
    zipcode          VARCHAR(6) COMMENT '邮政编码',
    reasontype       VARCHAR(1) COMMENT '产生原因类型',
    t1               VARCHAR(10) COMMENT '备用1',
    t2               VARCHAR(10) COMMENT '备用2',
    branchtype       VARCHAR(2) COMMENT '展业类型',
    branchtype2      VARCHAR(2) COMMENT '渠道',
    agentgroup       VARCHAR(12) COMMENT '展业机构',
    flag             VARCHAR(1) COMMENT '孤儿单状态',
    saleflag         VARCHAR(1) COMMENT '个销团标志',
    healthflag          varchar(2) not null comment '健康险标识 0-健康险，1-非健康险',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate         DATE        NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate       DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配信息备份表';

DROP TABLE IF EXISTS lapretollpolb;
-- 创建销管孤儿单分配备份表
CREATE TABLE lapretollpolb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)  NOT NULL COMMENT '01-手工分配调整，02-已分配调整',
    idno             VARCHAR(10) NOT NULL COMMENT '备份表',
    contno           VARCHAR(20) NOT NULL COMMENT '保单号',
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    newagentcode     VARCHAR(10) COMMENT '新代理人编码',
    managecom        VARCHAR(8)  NOT NULL COMMENT '管理机构',
    branchseries     VARCHAR(50) COMMENT '原服务人员销售机构系列',
    newbranchseries  VARCHAR(50) COMMENT '新服务人员销售机构系列',
    state            VARCHAR(1)  NOT NULL COMMENT '0--导出数据，1--导入数据或自动分配确认前状态（预览），2--分配完成，3--终止，4--未分配新收费员',
    poltype          VARCHAR(1) COMMENT '0--未分配孤儿单，1--已分配孤儿单',
    opertype         VARCHAR(1) COMMENT '0--自动分配确认处理，1--导入数据处理',
    t                VARCHAR(10) COMMENT '备用',
    tollconfirmdate  DATE COMMENT '分配确认日期',
    autodisdate      DATE COMMENT '自动分配日期',
    calagentcode     VARCHAR(10) COMMENT '自动分配代理',
    oldmanagecom     VARCHAR(8) COMMENT '原管理机构',
    branchtype       VARCHAR(2) COMMENT '渠道类型',
    branchtype2      VARCHAR(2) COMMENT '渠道',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               DECIMAL(12, 6) COMMENT 'F3',
    f4               VARCHAR(20) COMMENT 'F4',
    f5               VARCHAR(20) COMMENT 'F5',
    f6               VARCHAR(20) COMMENT 'F6',
    f7               VARCHAR(20) COMMENT 'F7',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate         DATE        NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate       DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间',
    PRIMARY KEY (edorno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配备份表';

DROP TABLE IF EXISTS lapretollmain;

-- 创建销管孤儿单分配标识表
CREATE TABLE lapretollmain
(
    idno           VARCHAR(20) NOT NULL COMMENT '待分配管理标识',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    state          VARCHAR(1) COMMENT '状态',
    polcount       INT COMMENT '保单件数',
    branchattr     VARCHAR(20) COMMENT '展业机构外部编码',
    opertype       VARCHAR(1)  NOT NULL COMMENT '待分配操作类型',
    noti           VARCHAR(20) COMMENT '备注',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (idno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配标识表';

DROP TABLE IF EXISTS lapretollpol;
-- 创建销管孤儿单分配表
CREATE TABLE lapretollpol
(
    id              bigint(20) NOT NULL COMMENT '主键',
    idno            VARCHAR(10) NOT NULL COMMENT '标识号',
    contno          VARCHAR(20) NOT NULL COMMENT '保单号',
    agentcode       VARCHAR(10) NOT NULL COMMENT '代理人编码',
    newagentcode    VARCHAR(10) COMMENT '新代理人编码',
    managecom       VARCHAR(8)  NOT NULL COMMENT '管理机构',
    branchseries    VARCHAR(50) COMMENT '原服务人员销售机构系列',
    newbranchseries VARCHAR(50) COMMENT '新服务人员销售机构系列',
    state           VARCHAR(1)  NOT NULL COMMENT '状态',
    poltype         VARCHAR(1) COMMENT '保单类型',
    opertype        VARCHAR(1) COMMENT '待分配操作类型',
    t               VARCHAR(10) COMMENT '备用',
    tollconfirmdate DATE COMMENT '分配确认日期',
    autodisdate     DATE COMMENT '自动分配日期',
    calagentcode    VARCHAR(10) COMMENT '自动分配代理',
    oldmanagecom    VARCHAR(8) COMMENT '原管理机构',
    branchtype      VARCHAR(2) COMMENT '渠道类型',
    branchtype2     VARCHAR(2) COMMENT '渠道',
    f1              VARCHAR(20) COMMENT 'F1',
    f2              VARCHAR(20) COMMENT 'F2',
    f3              DECIMAL(12, 6) COMMENT 'F3',
    f4              VARCHAR(20) COMMENT 'F4',
    f5              VARCHAR(20) COMMENT 'F5',
    f6              VARCHAR(20) COMMENT 'F6',
    f7              VARCHAR(20) COMMENT 'F7',
    operator        VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate        DATE        NOT NULL COMMENT '入机日期',
    maketime        VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate      DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime      VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator  VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_idno_contno (idno, contno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配表';


DROP TABLE IF EXISTS lapoldisfaillog;
-- 创建销管孤儿单分配轨迹信息表
CREATE TABLE lapoldisfaillog
(
    id             bigint(20) NOT NULL COMMENT '主键',
    idno           VARCHAR(20) NOT NULL COMMENT '主键',
    contno         VARCHAR(20) NOT NULL COMMENT '保单号',
    oldmanagecom   VARCHAR(8) COMMENT '原管理机构',
    newmanagecom   VARCHAR(8) COMMENT '新管理机构',
    oldagentgroup  VARCHAR(50) COMMENT '原销售机构',
    newagentgroup  VARCHAR(50) COMMENT '新销售机构',
    oldagentcode   VARCHAR(10) COMMENT '原代理人编码',
    newagentcode   VARCHAR(10) COMMENT '新代理人编码',
    distributeflag VARCHAR(2) COMMENT '0--自动分配 1--手工分配 2--高权限分配',
    failreason     VARCHAR(2) COMMENT '失败原因：1、上级主管继续率不达标；2、无上级主管；3、其他',
    branchtype     VARCHAR(2) COMMENT '展业类型',
    branchtype2    VARCHAR(2) COMMENT '渠道',
    f1             VARCHAR(20) COMMENT 'F1',
    f2             VARCHAR(20) COMMENT 'F2',
    f3             DECIMAL(12, 6) COMMENT 'F3',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_idno_contno (idno, contno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='销管孤儿单分配轨迹信息表';
