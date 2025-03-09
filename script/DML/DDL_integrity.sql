DROP TABLE IF EXISTS laagentpenalty;

/*==============================================================*/
/* Table: laagentpenalty                                        */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS laagentpenalty
(
    id               BIGINT      NOT NULL COMMENT '流水号',
    occmanagcom      VARCHAR(10) NOT NULL COMMENT '管理机构',
    agentcode        VARCHAR(10) NOT NULL COMMENT '销售人员',
    penaltycode      VARCHAR(50) NOT NULL COMMENT '处罚编号',
    penaltynumber    VARCHAR(50) COMMENT '处罚文号',
    penaltycom       VARCHAR(100) COMMENT '处罚机构',
    penaltyreason    VARCHAR(1000) COMMENT '处罚原因',
    penaltybasis     VARCHAR(1000) COMMENT '处罚依据',
    penaltymethod    VARCHAR(50) COMMENT '处罚方式：1-正常 2-迟到 3-缺卡 5-请假',
    penaltydecision  VARCHAR(1000) COMMENT '处罚决定',
    confproceeds     DECIMAL(20, 2) COMMENT '没收违法所得金额',
    penaltymoney     DECIMAL(20, 2) COMMENT '处罚金额',
    fundscode        VARCHAR(50) COMMENT '资金编码',
    penaltydate      DATE COMMENT '处罚日期',
    penaltystartdate DATE COMMENT '处罚开始日期',
    penaltyenddate   DATE COMMENT '处罚结束日期',
    branchtype       VARCHAR(2)  NOT NULL COMMENT '业务类型：1-个险，2-团险，3-银保，4-收展，9-精准',
    a1               VARCHAR(1000) COMMENT '预留字段1',
    a2               VARCHAR(1000) COMMENT '预留字段2',
    a3               DECIMAL(12, 2) COMMENT '预留字段3',
    a4               DECIMAL(12, 2) COMMENT '预留字段4',
    a5               DATE COMMENT '预留字段5',
    a6               DATE COMMENT '预留字段6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_agent_penalty (agentcode, penaltycode)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_general_ci COMMENT ='代理人处罚信息表';
