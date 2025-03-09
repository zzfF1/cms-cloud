-- 人员相关
DROP TABLE IF EXISTS laagenttemp;
/*==============================================================*/
/* Table: LAAGENTTEMP                                         */
/*==============================================================*/
CREATE TABLE laagenttemp
(
    agentcode                VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup               VARCHAR(12) COMMENT '代理人展业机构代码',
    managecom                VARCHAR(8) COMMENT '管理机构',
    password                 VARCHAR(40) COMMENT '密码',
    entryno                  VARCHAR(20) COMMENT '推荐报名编号',
    name                     VARCHAR(20) NOT NULL COMMENT '姓名',
    sex                      VARCHAR(1)  NOT NULL COMMENT '性别',
    birthday                 DATE        NOT NULL COMMENT '出生日期',
    nativeplace              VARCHAR(3) COMMENT '籍贯',
    nationality              VARCHAR(3) COMMENT '民族',
    marriage                 VARCHAR(1) COMMENT '婚姻状况',
    creditgrade              VARCHAR(1) COMMENT '信用等级',
    homeaddresscode          VARCHAR(10) COMMENT '家庭地址编码',
    homeaddress              VARCHAR(80) COMMENT '家庭地址',
    postaladdress            VARCHAR(80) COMMENT '通讯地址',
    zipcode                  VARCHAR(6) COMMENT '邮政编码',
    phone                    VARCHAR(18) COMMENT '电话',
    bp                       VARCHAR(20) COMMENT '传呼',
    mobile                   VARCHAR(15) COMMENT '手机',
    email                    VARCHAR(50) COMMENT '邮箱',
    marriagedate             DATE COMMENT '结婚日期',
    idno                     VARCHAR(20) COMMENT '身份证号码',
    source                   VARCHAR(100) COMMENT '来源地',
    bloodtype                VARCHAR(2) COMMENT '血型',
    polityvisage             VARCHAR(4) COMMENT '政治面貌',
    degree                   VARCHAR(1) COMMENT '学历',
    graduateschool           VARCHAR(40) COMMENT '毕业院校',
    speciality               VARCHAR(40) COMMENT '专业',
    posttitle                VARCHAR(2) COMMENT '职称',
    foreignlevel             VARCHAR(2) COMMENT '外语水平',
    workage                  INT COMMENT '从业年限',
    oldcom                   VARCHAR(40) COMMENT '原工作单位',
    oldoccupation            VARCHAR(20) COMMENT '原职业',
    headship                 VARCHAR(30) COMMENT '工作职务',
    recommendagent           VARCHAR(10) COMMENT '推荐代理人',
    business                 VARCHAR(20) COMMENT '工种/行业',
    salequaf                 VARCHAR(1) COMMENT '销售资格',
    quafno                   VARCHAR(30) COMMENT '代理人资格证号码',
    quafstartdate            DATE COMMENT '证书开始日期',
    quafenddate              DATE COMMENT '证书结束日期',
    devno1                   VARCHAR(30) COMMENT '展业证号码1',
    devno2                   VARCHAR(20) COMMENT '展业证号码2',
    retaincontno             VARCHAR(20) COMMENT '聘用合同号码',
    agentkind                VARCHAR(6) COMMENT '代理人类别',
    devgrade                 VARCHAR(2) COMMENT '业务拓展级别',
    insideflag               VARCHAR(1) COMMENT '内勤标志',
    fulltimeflag             VARCHAR(1) COMMENT '是否专职标志',
    noworkflag               VARCHAR(1) COMMENT '是否有待业证标志',
    traindate                DATE COMMENT '档案调入日期',
    employdate               DATE COMMENT '录用日期',
    indueformdate            DATE COMMENT '转正日期',
    outworkdate              DATE COMMENT '离司日期',
    recommendno              VARCHAR(20) COMMENT '推荐名编号2',
    cautionername            VARCHAR(20) COMMENT '担保人名称',
    cautionersex             VARCHAR(2) COMMENT '担保人性别',
    cautionerid              VARCHAR(20) COMMENT '担保人身份证',
    cautionerbirthday        DATE COMMENT '担保人出生日期',
    approver                 VARCHAR(10) COMMENT '复核员',
    approvedate              DATE COMMENT '复核日期',
    assumoney                DECIMAL(12, 2) COMMENT '保证金',
    remark                   VARCHAR(80) COMMENT '备注',
    agentstate               VARCHAR(2) COMMENT '代理人状态',
    qualipassflag            VARCHAR(1) COMMENT '档案标志位',
    smokeflag                VARCHAR(1) COMMENT '打折标志',
    rgtaddress               VARCHAR(20) COMMENT '户口所在地',
    bankcode                 VARCHAR(20) COMMENT '银行编码',
    bankaccno                VARCHAR(20) COMMENT '银行账户',
    branchtype               VARCHAR(2)  NOT NULL COMMENT '展业类型',
    trainperiods             VARCHAR(8) COMMENT '培训期数',
    branchcode               VARCHAR(12) COMMENT '代理人组别',
    age                      INT COMMENT '代理人年龄',
    channelname              VARCHAR(100) COMMENT '所属渠道',
    receiptno                VARCHAR(30) COMMENT '保证金收据号',
    idnotype                 VARCHAR(2) COMMENT '证件号码类型',
    branchtype2              VARCHAR(2) COMMENT '子渠道',
    trainpassflag            VARCHAR(1) COMMENT '培训通过标记',
    negreason                VARCHAR(2) COMMENT '未进入系统原因',
    emergentlink             VARCHAR(10) COMMENT '紧急联系人',
    emergentphone            VARCHAR(20) COMMENT '紧急联系人电话',
    retainstartdate          DATE COMMENT '劳动合同开始日期',
    retainenddate            DATE COMMENT '劳动合同截止日期',
    togaeflag                VARCHAR(1) COMMENT '司服是否领取标志',
    archievecode             VARCHAR(20) COMMENT '档案编码',
    comage                   INT COMMENT '司龄',
    busiage                  INT COMMENT '职龄',
    serviceage               INT COMMENT '工龄',
    fortes                   VARCHAR(20) COMMENT '特长',
    hobbies                  VARCHAR(20) COMMENT '爱好',
    agentsource              VARCHAR(2) COMMENT '来源',
    violation                VARCHAR(1) COMMENT '是否有违规违纪记录',
    foreven                  VARCHAR(1) COMMENT '是否有投连销售资格',
    acctype                  VARCHAR(1) COMMENT '对公对私标识',
    bankaccprovince          VARCHAR(120) COMMENT '开户行省',
    bankacccity              VARCHAR(120) COMMENT '开户市',
    accattributes            VARCHAR(1) COMMENT '卡折类型',
    bankaccsub               VARCHAR(200) COMMENT '开户行细分',
    worknature               VARCHAR(2) COMMENT '工作性质',
    employmentnature         VARCHAR(2)  DEFAULT NULL COMMENT '用工性质',
    supervisiontel           VARCHAR(50) DEFAULT NULL COMMENT '保险行业协会监督电话',
    practiceregistrationmark VARCHAR(1)  DEFAULT NULL COMMENT '执业登记标志',
    breedbenefit             VARCHAR(1) COMMENT '是否享受育成利益',
    addbenefit               VARCHAR(1) COMMENT '是否享受增员利益',
    insurancecertificate     VARCHAR(1) COMMENT '是否有分红保险及万能保险销售资质证书',
    investment               VARCHAR(1) COMMENT '是否有投资连结保险及变额年金保险销售资质证书',
    operator                 VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate                 DATE        NOT NULL COMMENT '入机日期',
    maketime                 VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator           VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    modifydate               DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime               VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (agentcode),
    KEY `idx_laagenttemp_index1` (`branchtype`),
    KEY `idx_laagenttemp_index2` (`managecom`),
    KEY `idx_laagenttemp_index3` (`agentgroup`),
    KEY `idx_laagenttemp_index4` (`idno`),
    KEY `idx_laagenttemp_index5` (`mobile`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管代理人入职申请信息表';

DROP TABLE IF EXISTS laagenttempb;
/*==============================================================*/
/* Table: LAAGENTTEMPB                                       */
/*==============================================================*/
CREATE TABLE laagenttempb
(
    edorno                   VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype                 VARCHAR(2)  NOT NULL COMMENT '转储类型',
    edordate                 DATE COMMENT '转储日期',
    agentcode                VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup               VARCHAR(12) COMMENT '代理人展业机构代码',
    managecom                VARCHAR(8) COMMENT '管理机构',
    password                 VARCHAR(40) COMMENT '密码',
    entryno                  VARCHAR(20) COMMENT '推荐报名编号',
    name                     VARCHAR(20) NOT NULL COMMENT '姓名',
    sex                      VARCHAR(1)  NOT NULL COMMENT '性别',
    birthday                 DATE        NOT NULL COMMENT '出生日期',
    nativeplace              VARCHAR(3) COMMENT '籍贯',
    nationality              VARCHAR(3) COMMENT '民族',
    marriage                 VARCHAR(1) COMMENT '婚姻状况',
    creditgrade              VARCHAR(1) COMMENT '信用等级',
    homeaddresscode          VARCHAR(10) COMMENT '家庭地址编码',
    homeaddress              VARCHAR(80) COMMENT '家庭地址',
    postaladdress            VARCHAR(80) COMMENT '通讯地址',
    zipcode                  VARCHAR(6) COMMENT '邮政编码',
    phone                    VARCHAR(18) COMMENT '电话',
    bp                       VARCHAR(20) COMMENT '传呼',
    mobile                   VARCHAR(15) COMMENT '手机',
    email                    VARCHAR(50) COMMENT '邮箱',
    marriagedate             DATE COMMENT '结婚日期',
    idno                     VARCHAR(20) COMMENT '身份证号码',
    source                   VARCHAR(100) COMMENT '来源地',
    bloodtype                VARCHAR(2) COMMENT '血型',
    polityvisage             VARCHAR(4) COMMENT '政治面貌',
    degree                   VARCHAR(1) COMMENT '学历',
    graduateschool           VARCHAR(40) COMMENT '毕业院校',
    speciality               VARCHAR(40) COMMENT '专业',
    posttitle                VARCHAR(2) COMMENT '职称',
    foreignlevel             VARCHAR(2) COMMENT '外语水平',
    workage                  INT COMMENT '从业年限',
    oldcom                   VARCHAR(40) COMMENT '原工作单位',
    oldoccupation            VARCHAR(20) COMMENT '原职业',
    headship                 VARCHAR(30) COMMENT '工作职务',
    recommendagent           VARCHAR(10) COMMENT '推荐代理人',
    business                 VARCHAR(20) COMMENT '工种/行业',
    salequaf                 VARCHAR(1) COMMENT '销售资格',
    quafno                   VARCHAR(30) COMMENT '代理人资格证号码',
    quafstartdate            DATE COMMENT '证书开始日期',
    quafenddate              DATE COMMENT '证书结束日期',
    devno1                   VARCHAR(30) COMMENT '展业证号码1',
    devno2                   VARCHAR(20) COMMENT '展业证号码2',
    retaincontno             VARCHAR(20) COMMENT '聘用合同号码',
    agentkind                VARCHAR(6) COMMENT '代理人类别',
    devgrade                 VARCHAR(2) COMMENT '业务拓展级别',
    insideflag               VARCHAR(1) COMMENT '内勤标志',
    fulltimeflag             VARCHAR(1) COMMENT '是否专职标志',
    noworkflag               VARCHAR(1) COMMENT '是否有待业证标志',
    traindate                DATE COMMENT '档案调入日期',
    employdate               DATE COMMENT '录用日期',
    indueformdate            DATE COMMENT '转正日期',
    outworkdate              DATE COMMENT '离司日期',
    recommendno              VARCHAR(20) COMMENT '推荐名编号2',
    cautionername            VARCHAR(20) COMMENT '担保人名称',
    cautionersex             VARCHAR(2) COMMENT '担保人性别',
    cautionerid              VARCHAR(20) COMMENT '担保人身份证',
    cautionerbirthday        DATE COMMENT '担保人出生日期',
    approver                 VARCHAR(10) COMMENT '复核员',
    approvedate              DATE COMMENT '复核日期',
    assumoney                DECIMAL(12, 2) COMMENT '保证金',
    remark                   VARCHAR(80) COMMENT '备注',
    agentstate               VARCHAR(2) COMMENT '代理人状态',
    qualipassflag            VARCHAR(1) COMMENT '档案标志位',
    smokeflag                VARCHAR(1) COMMENT '打折标志',
    rgtaddress               VARCHAR(20) COMMENT '户口所在地',
    bankcode                 VARCHAR(20) COMMENT '银行编码',
    bankaccno                VARCHAR(20) COMMENT '银行账户',
    branchtype               VARCHAR(2)  NOT NULL COMMENT '展业类型',
    trainperiods             VARCHAR(8) COMMENT '培训期数',
    branchcode               VARCHAR(12) COMMENT '代理人组别',
    age                      INT COMMENT '代理人年龄',
    channelname              VARCHAR(100) COMMENT '所属渠道',
    receiptno                VARCHAR(30) COMMENT '保证金收据号',
    idnotype                 VARCHAR(2) COMMENT '证件号码类型',
    branchtype2              VARCHAR(2) COMMENT '渠道',
    trainpassflag            VARCHAR(1) COMMENT '培训通过标记',
    negreason                VARCHAR(2) COMMENT '未进入系统原因',
    emergentlink             VARCHAR(10) COMMENT '紧急联系人',
    emergentphone            VARCHAR(20) COMMENT '紧急联系人电话',
    retainstartdate          DATE COMMENT '劳动合同开始日期',
    retainenddate            DATE COMMENT '劳动合同截止日期',
    togaeflag                VARCHAR(1) COMMENT '司服是否领取标志',
    archievecode             VARCHAR(20) COMMENT '档案编码',
    comage                   INT COMMENT '司龄',
    busiage                  INT COMMENT '职龄',
    serviceage               INT COMMENT '工龄',
    fortes                   VARCHAR(20) COMMENT '特长',
    hobbies                  VARCHAR(20) COMMENT '爱好',
    agentsource              VARCHAR(2) COMMENT '来源',
    violation                VARCHAR(1) COMMENT '是否有违规违纪记录',
    foreven                  VARCHAR(1) COMMENT '是否有投连销售资格',
    acctype                  VARCHAR(1) COMMENT '对公对私标识',
    bankaccprovince          VARCHAR(120) COMMENT '开户行省',
    bankacccity              VARCHAR(120) COMMENT '开户市',
    accattributes            VARCHAR(1) COMMENT '卡折类型',
    bankaccsub               VARCHAR(200) COMMENT '开户行细分',
    worknature               VARCHAR(2) COMMENT '工作性质',
    employmentnature         VARCHAR(2)  DEFAULT NULL COMMENT '用工性质',
    supervisiontel           VARCHAR(50) DEFAULT NULL COMMENT '保险行业协会监督电话',
    practiceregistrationmark VARCHAR(1)  DEFAULT NULL COMMENT '执业登记标志',
    breedbenefit             VARCHAR(1) COMMENT '是否享受育成利益',
    addbenefit               VARCHAR(1) COMMENT '是否享受增员利益',
    insurancecertificate     VARCHAR(1) COMMENT '是否有分红保险及万能保险销售资质证书',
    investment               VARCHAR(1) COMMENT '是否有投资连结保险及变额年金保险销售资质证书',
    operator                 VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate                 DATE COMMENT '入机日期',
    maketime                 VARCHAR(8) COMMENT '入机时间',
    modifyoperator           VARCHAR(60) not null comment '最后一次修改人',
    modifydate               DATE COMMENT '最后一次修改日期',
    modifytime               VARCHAR(8) COMMENT '最后一次修改时间',
    lastoperator             VARCHAR(60) COMMENT '备份人',
    lastmakedatetime         DATETIME    DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管代理人入职申请信息备份表';

-- ----------------------------
-- Table structure for latreetemp-销管行政信息申请表
-- ----------------------------
DROP TABLE IF EXISTS latreetemp;
CREATE TABLE IF NOT EXISTS latreetemp
(
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup       VARCHAR(12) NOT NULL COMMENT '销售机构',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentseries      VARCHAR(2)     DEFAULT NULL COMMENT '代理人职级序列',
    agentgrade       VARCHAR(4)     DEFAULT NULL COMMENT '代理人职级',
    agentlastseries  VARCHAR(2)     DEFAULT NULL COMMENT '代理人上次职级序列',
    agentlastgrade   VARCHAR(4)     DEFAULT NULL COMMENT '代理人上次职级',
    introagency      VARCHAR(10)    DEFAULT NULL COMMENT '推荐人',
    upagent          VARCHAR(10)    DEFAULT NULL COMMENT '上级代理人',
    othupagent       VARCHAR(10)    DEFAULT NULL COMMENT '其他上级代理人',
    introbreakflag   VARCHAR(1)     DEFAULT NULL COMMENT '增员关系标识',
    introcommstart   DATE           DEFAULT NULL COMMENT '增员关系起期',
    introcommend     DATE           DEFAULT NULL COMMENT '增员关系止期',
    edumanager       VARCHAR(10)    DEFAULT NULL COMMENT '育成主管代理人',
    rearbreakflag    VARCHAR(1)     DEFAULT NULL COMMENT '育成信息标识',
    rearcommstart    DATE           DEFAULT NULL COMMENT '育成关系起期',
    rearcommend      DATE           DEFAULT NULL COMMENT '育成关系止期',
    ascriptseries    VARCHAR(200)   DEFAULT NULL COMMENT '归属顺序',
    oldstartdate     DATE           DEFAULT NULL COMMENT '前职级起期',
    oldenddate       DATE           DEFAULT NULL COMMENT '前职级止期',
    startdate        DATE           DEFAULT NULL COMMENT '现职级起期',
    astartdate       DATE           DEFAULT NULL COMMENT '考核起始日期',
    assesstype       VARCHAR(1)     DEFAULT NULL COMMENT '考核类型',
    state            VARCHAR(2)     DEFAULT NULL COMMENT '代理人状态',
    branchcode       VARCHAR(12)    DEFAULT NULL COMMENT '代理人直辖组',
    agentkind        VARCHAR(6)     DEFAULT NULL COMMENT '代理人类型',
    branchtype       VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    isconnman        VARCHAR(1)     DEFAULT NULL COMMENT '同业衔接人员标志',
    initgrade        VARCHAR(4)     DEFAULT NULL COMMENT '入司职级',
    agentline        VARCHAR(2)     DEFAULT NULL COMMENT '代理人发展路线标志',
    insideflag       VARCHAR(2)     DEFAULT NULL COMMENT '内外勤标识',
    speciflag        VARCHAR(2)     DEFAULT NULL COMMENT '特殊人员标记',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '子渠道',
    vipproperty      VARCHAR(20)    DEFAULT NULL COMMENT '首次考核时长',
    connmanagerstate VARCHAR(1)     DEFAULT NULL COMMENT '衔接人员状态标识',
    tollflag         VARCHAR(1)     DEFAULT NULL COMMENT '兼专职收费员标志',
    difficulty       DECIMAL(12, 2) DEFAULT NULL COMMENT '收费员难度系数',
    connsuccdate     DATE           DEFAULT NULL COMMENT '定级完成日期',
    tollscope        VARCHAR(1)     DEFAULT NULL COMMENT '首次考核状态',
    agentgradersn    VARCHAR(1)     DEFAULT NULL COMMENT '当前职级起聘原因',
    oldmstartdate    DATE           DEFAULT NULL COMMENT '前管理职级起聘日期',
    oldmenddate      DATE           DEFAULT NULL COMMENT '前管理职级起聘止期',
    mstartdate       DATE           DEFAULT NULL COMMENT '现管理职级起聘日期',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifyoperator   VARCHAR(60) not null comment '最后一次修改人',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    PRIMARY KEY (agentcode),
    KEY `idx_latreetemp_index1` (`branchtype`),
    KEY `idx_latreetemp_index2` (`managecom`),
    KEY `idx_latreetemp_index3` (`agentgroup`)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管行政信息申请表';


-- ----------------------------
-- Table structure for latreetempb-销管行政信息申请备份表
-- ----------------------------
DROP TABLE IF EXISTS latreetempb;
CREATE TABLE IF NOT EXISTS latreetempb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号',
    edortype         VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup       VARCHAR(12) NOT NULL COMMENT '销售机构',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentseries      VARCHAR(2)     DEFAULT NULL COMMENT '代理人职级序列',
    agentgrade       VARCHAR(4)     DEFAULT NULL COMMENT '代理人职级',
    agentlastseries  VARCHAR(2)     DEFAULT NULL COMMENT '代理人上次职级序列',
    agentlastgrade   VARCHAR(4)     DEFAULT NULL COMMENT '代理人上次职级',
    introagency      VARCHAR(10)    DEFAULT NULL COMMENT '推荐人',
    upagent          VARCHAR(10)    DEFAULT NULL COMMENT '上级代理人',
    othupagent       VARCHAR(10)    DEFAULT NULL COMMENT '其他上级代理人',
    introbreakflag   VARCHAR(1)     DEFAULT NULL COMMENT '增员关系标识',
    introcommstart   DATE           DEFAULT NULL COMMENT '增员关系起期',
    introcommend     DATE           DEFAULT NULL COMMENT '增员关系止期',
    edumanager       VARCHAR(10)    DEFAULT NULL COMMENT '育成主管代理人',
    rearbreakflag    VARCHAR(1)     DEFAULT NULL COMMENT '育成信息标识',
    rearcommstart    DATE           DEFAULT NULL COMMENT '育成关系起期',
    rearcommend      DATE           DEFAULT NULL COMMENT '育成关系止期',
    ascriptseries    VARCHAR(200)   DEFAULT NULL COMMENT '归属顺序',
    oldstartdate     DATE           DEFAULT NULL COMMENT '前职级起期',
    oldenddate       DATE           DEFAULT NULL COMMENT '前职级止期',
    startdate        DATE           DEFAULT NULL COMMENT '现职级起期',
    astartdate       DATE           DEFAULT NULL COMMENT '考核起始日期',
    assesstype       VARCHAR(1)     DEFAULT NULL COMMENT '考核类型',
    state            VARCHAR(2)     DEFAULT NULL COMMENT '代理人状态',
    edordate         DATE           DEFAULT NULL COMMENT '转储日期',
    branchcode       VARCHAR(12)    DEFAULT NULL COMMENT '代理人直辖组',
    agentkind        VARCHAR(6)     DEFAULT NULL COMMENT '代理人类型',
    branchtpe        VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    isconnman        VARCHAR(1)     DEFAULT NULL COMMENT '同业衔接人员标志',
    initgrade        VARCHAR(4)     DEFAULT NULL COMMENT '入司职级',
    agentline        VARCHAR(2)     DEFAULT NULL COMMENT '代理人发展路线标志',
    insideflag       VARCHAR(2)     DEFAULT NULL COMMENT '内外勤标识',
    speciflag        VARCHAR(2)     DEFAULT NULL COMMENT '特殊人员标记',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '子渠道',
    vipproperty      VARCHAR(20)    DEFAULT NULL COMMENT '首次考核时长',
    connmanagerstate VARCHAR(1)     DEFAULT NULL COMMENT '衔接人员状态标识',
    tollflag         VARCHAR(1)     DEFAULT NULL COMMENT '兼专职收费员标志',
    difficulty       DECIMAL(12, 2) DEFAULT NULL COMMENT '收费员难度系数',
    connsuccdate     DATE           DEFAULT NULL COMMENT '定级完成日期',
    tollscope        VARCHAR(1)     DEFAULT NULL COMMENT '首次考核状态',
    agentgradersn    VARCHAR(1)     DEFAULT NULL COMMENT '当前职级起聘原因',
    oldmstartdate    DATE           DEFAULT NULL COMMENT '前管理职级起聘日期',
    oldmenddate      DATE           DEFAULT NULL COMMENT '前管理职级起聘止期',
    mstartdate       DATE           DEFAULT NULL COMMENT '现管理职级起聘日期',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifyoperator   VARCHAR(60) not null comment '最后一次修改人',
    modifydate       DATE COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME       DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管行政信息申请备份表';

drop table if exists lafamilyinfotemp;
create table lafamilyinfotemp
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    relation       VARCHAR(100) null comment '关系',
    name           VARCHAR(100) null comment '姓名',
    idnotype       VARCHAR(2)   null comment '证件类型',
    idno           VARCHAR(100) null comment '证件号',
    company        VARCHAR(100) null comment '工作单位公司',
    post           VARCHAR(100) null comment '岗位',
    occupation     VARCHAR(100) null comment '职业',
    phone          VARCHAR(100) null comment '电话',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_lafamilyinfotemp_index1` (`agentcode`),
    KEY `idx_lafamilyinfotemp_index2` (`idno`),
    KEY `idx_lafamilyinfotemp_index3` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='家庭信息申请表';

drop table if exists laworkinfotemp;
create table laworkinfotemp
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    company        VARCHAR(100) null comment '公司名称',
    type           VARCHAR(100) null comment '行业类别',
    post           VARCHAR(100) null comment '岗位',
    annualsalary   VARCHAR(100) null comment '年薪',
    startdate      DATE         null comment '起始时间',
    enddate        DATE         null comment '结束时间',
    leavereason    VARCHAR(100) null comment '离职理由',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_laworkinfotemp_index1` (`agentcode`),
    KEY `idx_laworkinfotemp_index2` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='工作信息表申请表';

drop table if exists laeducationinfotemp;
create table laeducationinfotemp
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    school         VARCHAR(100) null comment '学校名称',
    type           VARCHAR(100) null comment '学历',
    major          VARCHAR(100) null comment '专业',
    beginyear      VARCHAR(100) null comment '入学年度',
    endyear        VARCHAR(100) null comment '毕业年度',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_laeducationinfotemp_index1` (`agentcode`),
    KEY `idx_laeducationinfotemp_index2` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='学历信息申请表';

drop table if exists lafamilyinfotempb;
create table lafamilyinfotempb
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    relation         VARCHAR(100) null comment '关系',
    name             VARCHAR(100) null comment '姓名',
    idnotype         VARCHAR(2)   null comment '证件类型',
    idno             VARCHAR(100) null comment '证件号',
    company          VARCHAR(100) null comment '工作单位公司',
    post             VARCHAR(100) null comment '岗位',
    occupation       VARCHAR(100) null comment '职业',
    phone            VARCHAR(100) null comment '电话',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_lafamilyinfotempb_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='家庭信息申请备份表';

drop table if exists laworkinfotempb;
create table laworkinfotempb
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    company          VARCHAR(100) null comment '公司名称',
    type             VARCHAR(100) null comment '行业类别',
    post             VARCHAR(100) null comment '岗位',
    annualsalary     VARCHAR(100) null comment '年薪',
    startdate        DATE         null comment '起始时间',
    enddate          DATE         null comment '结束时间',
    leavereason      VARCHAR(100) null comment '离职理由',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_laworkinfotempb_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='工作信息表申请备份表';

drop table if exists laeducationinfotempb;
create table laeducationinfotempb
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    school           VARCHAR(100) null comment '学校名称',
    type             VARCHAR(100) null comment '学历',
    major            VARCHAR(100) null comment '专业',
    beginyear        VARCHAR(100) null comment '入学年度',
    endyear          VARCHAR(100) null comment '毕业年度',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_laeducationinfotempb_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='学历信息申请备份表';


DROP TABLE IF EXISTS laagent;
-- ----------------------------
-- Table structure for laagent-代理人信息表
-- ----------------------------
CREATE TABLE laagent
(
    agentcode                VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup               VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom                VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    password                 VARCHAR(40)    DEFAULT NULL COMMENT '密码',
    entryno                  VARCHAR(20)    DEFAULT NULL COMMENT '推荐报名编号',
    name                     VARCHAR(20) NOT NULL COMMENT '姓名',
    sex                      VARCHAR(1)  NOT NULL COMMENT '性别',
    birthday                 DATE        NOT NULL COMMENT '出生日期',
    nativeplace              VARCHAR(3)     DEFAULT NULL COMMENT '籍贯',
    nationality              VARCHAR(3)     DEFAULT NULL COMMENT '民族',
    marriage                 VARCHAR(1)     DEFAULT NULL COMMENT '婚姻状况',
    creditgrade              VARCHAR(1)     DEFAULT NULL COMMENT '信用等级',
    homeaddresscode          VARCHAR(10)    DEFAULT NULL COMMENT '家庭地址编码',
    homeaddress              VARCHAR(80)    DEFAULT NULL COMMENT '家庭地址',
    postaladdress            VARCHAR(80)    DEFAULT NULL COMMENT '通讯地址',
    zipcode                  VARCHAR(6)     DEFAULT NULL COMMENT '邮政编码',
    phone                    VARCHAR(18)    DEFAULT NULL COMMENT '电话',
    bp                       VARCHAR(20)    DEFAULT NULL COMMENT '传呼',
    mobile                   VARCHAR(15)    DEFAULT NULL COMMENT '手机',
    email                    VARCHAR(50)    DEFAULT NULL COMMENT '邮箱',
    marriagedate             DATE           DEFAULT NULL COMMENT '结婚日期',
    idno                     VARCHAR(20)    DEFAULT NULL COMMENT '身份证号码',
    source                   VARCHAR(100)   DEFAULT NULL COMMENT '来源地',
    bloodtype                VARCHAR(2)     DEFAULT NULL COMMENT '血型',
    polityvisage             VARCHAR(4)     DEFAULT NULL COMMENT '政治面貌',
    degree                   VARCHAR(1)     DEFAULT NULL COMMENT '学历',
    graduateschool           VARCHAR(40)    DEFAULT NULL COMMENT '毕业院校',
    speciality               VARCHAR(40)    DEFAULT NULL COMMENT '专业',
    posttitle                VARCHAR(2)     DEFAULT NULL COMMENT '职称',
    foreignlevel             VARCHAR(2)     DEFAULT NULL COMMENT '外语水平',
    workage                  DECIMAL        DEFAULT NULL COMMENT '从业年限',
    oldcom                   VARCHAR(40)    DEFAULT NULL COMMENT '原工作单位',
    oldoccupation            VARCHAR(20)    DEFAULT NULL COMMENT '原职业',
    headship                 VARCHAR(30)    DEFAULT NULL COMMENT '工作职务体制',
    recommendagent           VARCHAR(10)    DEFAULT NULL COMMENT '推荐代理人',
    business                 VARCHAR(20)    DEFAULT NULL COMMENT '工种/行业',
    salequaf                 VARCHAR(1)     DEFAULT NULL COMMENT '销售资格',
    quafno                   VARCHAR(30)    DEFAULT NULL COMMENT '代理人资格证号码',
    quafstartdate            DATE           DEFAULT NULL COMMENT '证书开始日期',
    quafenddate              DATE           DEFAULT NULL COMMENT '证书结束日期',
    devno1                   VARCHAR(30)    DEFAULT NULL COMMENT '展业证号码1',
    devno2                   VARCHAR(30)    DEFAULT NULL COMMENT '展业证号码2',
    retaincontno             VARCHAR(20)    DEFAULT NULL COMMENT '聘用合同号码',
    agentkind                VARCHAR(6)     DEFAULT NULL COMMENT '代理人类别',
    devgrade                 VARCHAR(2)     DEFAULT NULL COMMENT '业务拓展级别',
    insideflag               VARCHAR(1)     DEFAULT NULL COMMENT '内勤标志',
    fulltimeflag             VARCHAR(1)     DEFAULT NULL COMMENT '是否专职标志',
    noworkflag               VARCHAR(1)     DEFAULT NULL COMMENT '是否有待业证标志',
    traindate                DATE           DEFAULT NULL COMMENT '培训日期',
    employdate               DATE           DEFAULT NULL COMMENT '录用日期',
    indueformdate            DATE           DEFAULT NULL COMMENT '转正日期',
    outworkdate              DATE           DEFAULT NULL COMMENT '离司日期',
    recommendno              VARCHAR(20)    DEFAULT NULL COMMENT '推荐名编号2',
    cautionername            VARCHAR(20)    DEFAULT NULL COMMENT '担保人名称',
    cautionersex             VARCHAR(2)     DEFAULT NULL COMMENT '担保人性别',
    cautionerid              VARCHAR(20)    DEFAULT NULL COMMENT '担保人身份证',
    cautionerbirthday        DATE           DEFAULT NULL COMMENT '担保人出生日期',
    approver                 VARCHAR(10)    DEFAULT NULL COMMENT '复核员',
    approvedate              DATE           DEFAULT NULL COMMENT '复核日期',
    assumoney                DECIMAL(12, 2) DEFAULT NULL COMMENT '保证金',
    remark                   VARCHAR(80)    DEFAULT NULL COMMENT '备注',
    agentstate               VARCHAR(2)     DEFAULT NULL COMMENT '代理人状态',
    qualipassflag            VARCHAR(1)     DEFAULT NULL COMMENT '档案标志位',
    smokeflag                VARCHAR(1)     DEFAULT NULL COMMENT '销售资格标记',
    rgtaddress               VARCHAR(20)    DEFAULT NULL COMMENT '户口所在地',
    bankcode                 VARCHAR(20)    DEFAULT NULL COMMENT '银行编码',
    bankaccno                VARCHAR(30)    DEFAULT NULL COMMENT '银行账户',
    branchtype               VARCHAR(2)  NOT NULL COMMENT '展业类型',
    trainperiods             VARCHAR(8)     DEFAULT NULL COMMENT '培训期数',
    branchcode               VARCHAR(12)    DEFAULT NULL COMMENT '代理人组别',
    age                      DECIMAL        DEFAULT NULL COMMENT '代理人年龄',
    channelname              VARCHAR(100)   DEFAULT NULL COMMENT '所属渠道',
    receiptno                VARCHAR(30)    DEFAULT NULL COMMENT '保证金收据号',
    idnotype                 VARCHAR(2)     DEFAULT NULL COMMENT '证件号码类型',
    branchtype2              VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    trainpassflag            VARCHAR(1)     DEFAULT NULL COMMENT '培训通过标记',
    emergentlink             VARCHAR(10)    DEFAULT NULL COMMENT '紧急联系人',
    emergentphone            VARCHAR(20)    DEFAULT NULL COMMENT '紧急联系人电话',
    retainstartdate          DATE           DEFAULT NULL COMMENT '劳动合同开始日期',
    retainenddate            DATE           DEFAULT NULL COMMENT '劳动合同截至日期',
    togaeflag                VARCHAR(1)     DEFAULT NULL COMMENT '司服是否领取标志',
    archievecode             VARCHAR(20)    DEFAULT NULL COMMENT '档案编码',
    accnocrdate              DATE           DEFAULT NULL COMMENT '账户建立日期',
    comage                   DECIMAL        DEFAULT NULL COMMENT '司龄',
    busiage                  DECIMAL        DEFAULT NULL COMMENT '职龄',
    serviceage               DECIMAL        DEFAULT NULL COMMENT '工龄',
    fortes                   VARCHAR(20)    DEFAULT NULL COMMENT '特长',
    hobbies                  VARCHAR(20)    DEFAULT NULL COMMENT '爱好',
    officeflag               VARCHAR(1)     DEFAULT NULL COMMENT '是否驻厅',
    elecassigndate           DATE           DEFAULT NULL COMMENT '开始时间',
    elecbackdate             DATE           DEFAULT NULL COMMENT '结束时间',
    statef                   VARCHAR(1)     DEFAULT '0' COMMENT '审核标志',
    agentsource              VARCHAR(2)     DEFAULT NULL COMMENT '来源',
    violation                VARCHAR(1)     DEFAULT NULL COMMENT '是否有违规违纪记录',
    foreven                  VARCHAR(1)     DEFAULT NULL COMMENT '是否有投连销售资格',
    acctype                  VARCHAR(1)     DEFAULT NULL COMMENT '对公对私标识',
    bankaccprovince          VARCHAR(120)   DEFAULT NULL COMMENT '开户行省',
    bankacccity              VARCHAR(120)   DEFAULT NULL COMMENT '开户市',
    accattributes            VARCHAR(1)     DEFAULT NULL COMMENT '卡折类型',
    bankaccsub               VARCHAR(200)   DEFAULT NULL COMMENT '开户行细分',
    worknature               VARCHAR(2)     DEFAULT NULL COMMENT '工作性质',
    employmentnature         VARCHAR(2)     DEFAULT NULL COMMENT '用工性质',
    supervisiontel           VARCHAR(50)    DEFAULT NULL COMMENT '保险行业协会监督电话',
    practiceregistrationmark VARCHAR(1)     DEFAULT NULL COMMENT '执业登记标志',
    breedbenefit             VARCHAR(1)     DEFAULT NULL COMMENT '是否享受育成利益',
    addbenefit               VARCHAR(1)     DEFAULT NULL COMMENT '是否享受增员利益',
    insurancecertificate     VARCHAR(1)     DEFAULT NULL COMMENT '是否有分红保险及万能保险销售资质证书',
    investment               VARCHAR(1)     DEFAULT NULL COMMENT '是否有投资连结保险及变额年金保险销售资质证书',
    operator                 VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate                 DATE           DEFAULT NULL COMMENT '入机日期',
    maketime                 VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifyoperator           VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate               DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime               VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (agentcode),
    KEY `idx_laagent_index1` (`branchtype`),
    KEY `idx_laagent_index2` (`managecom`),
    KEY `idx_laagent_index3` (`agentgroup`),
    KEY `idx_laagent_index4` (`idno`),
    KEY `idx_laagent_index5` (`mobile`)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='代理人信息表';

DROP TABLE IF EXISTS laagentb;
/*==============================================================*/
/* Table: laagentb                                 */
/*==============================================================*/
CREATE TABLE laagentb
(
    edorno                   VARCHAR(20) NOT NULL COMMENT '转储号码',
    edortype                 VARCHAR(2)  NOT NULL COMMENT '转储类型',
    branchtype               VARCHAR(2)  NOT NULL COMMENT '展业类型',
    agentcode                VARCHAR(10) NOT NULL COMMENT '代理人编码',
    newagentcode             VARCHAR(10) COMMENT '新代理人编码',
    agentgroup               VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom                VARCHAR(8) COMMENT '管理机构',
    password                 VARCHAR(40) COMMENT '密码',
    entryno                  VARCHAR(20) COMMENT '推荐报名编号',
    name                     VARCHAR(20) NOT NULL COMMENT '姓名',
    sex                      VARCHAR(1)  NOT NULL COMMENT '性别',
    birthday                 DATE        NOT NULL COMMENT '出生日期',
    nativeplace              VARCHAR(3) COMMENT '籍贯',
    nationality              VARCHAR(3) COMMENT '民族',
    marriage                 VARCHAR(1) COMMENT '婚姻状况',
    creditgrade              VARCHAR(1) COMMENT '信用等级',
    homeaddresscode          VARCHAR(10) COMMENT '家庭地址编码',
    homeaddress              VARCHAR(80) COMMENT '家庭地址',
    postaladdress            VARCHAR(80) COMMENT '通讯地址',
    zipcode                  VARCHAR(6) COMMENT '邮政编码',
    phone                    VARCHAR(18) COMMENT '电话',
    bp                       VARCHAR(20) COMMENT '传呼',
    mobile                   VARCHAR(50) COMMENT '手机',
    email                    VARCHAR(50) COMMENT '邮箱',
    marriagedate             DATE COMMENT '结婚日期',
    idno                     VARCHAR(20) COMMENT '身份证号码',
    source                   VARCHAR(100) COMMENT '来源地',
    bloodtype                VARCHAR(2) COMMENT '血型',
    polityvisage             VARCHAR(4) COMMENT '政治面貌',
    degree                   VARCHAR(1) COMMENT '学历',
    graduateschool           VARCHAR(40) COMMENT '毕业院校',
    speciality               VARCHAR(40) COMMENT '专业',
    posttitle                VARCHAR(2) COMMENT '职称',
    foreignlevel             VARCHAR(2) COMMENT '外语水平',
    workage                  INT COMMENT '从业年限',
    oldcom                   VARCHAR(40) COMMENT '原工作单位',
    oldoccupation            VARCHAR(20) COMMENT '原职业',
    headship                 VARCHAR(30) COMMENT '工作职务',
    recommendagent           VARCHAR(10) COMMENT '推荐代理人',
    business                 VARCHAR(20) COMMENT '工种/行业',
    salequaf                 VARCHAR(1) COMMENT '销售资格',
    quafno                   VARCHAR(30) COMMENT '代理人资格证号码',
    quafstartdate            DATE COMMENT '证书开始日期',
    quafenddate              DATE COMMENT '证书结束日期',
    devno1                   VARCHAR(30) COMMENT '展业证号码1',
    devno2                   VARCHAR(30) COMMENT '展业证号码2',
    retaincontno             VARCHAR(20) COMMENT '聘用合同号码',
    agentkind                VARCHAR(6) COMMENT '代理人类别',
    devgrade                 VARCHAR(2) COMMENT '业务拓展级别',
    insideflag               VARCHAR(1) COMMENT '内勤标志',
    fulltimeflag             VARCHAR(1) COMMENT '是否专职标志',
    noworkflag               VARCHAR(1) COMMENT '是否有待业证标志',
    traindate                DATE COMMENT '档案调入日期',
    employdate               DATE COMMENT '录用日期',
    indueformdate            DATE COMMENT '转正日期',
    outworkdate              DATE COMMENT '离司日期',
    recommendno              VARCHAR(20) COMMENT '推荐名编号2',
    cautionername            VARCHAR(20) COMMENT '担保人名称',
    cautionersex             VARCHAR(2) COMMENT '担保人性别',
    cautionerid              VARCHAR(20) COMMENT '担保人身份证',
    cautionerbirthday        DATE COMMENT '准离职日期',
    approver                 VARCHAR(10) COMMENT '复核员',
    approvedate              DATE COMMENT '复核日期',
    assumoney                DECIMAL(12, 2) COMMENT '保证金',
    remark                   VARCHAR(80) COMMENT '备注',
    agentstate               VARCHAR(2) COMMENT '代理人状态',
    qualipassflag            VARCHAR(1) COMMENT '档案标志位',
    smokeflag                VARCHAR(1) COMMENT '打折标志',
    rgtaddress               VARCHAR(20) COMMENT '户口所在地',
    bankcode                 VARCHAR(20) COMMENT '银行编码',
    bankaccno                VARCHAR(30) COMMENT '银行帐户',
    branchcode               VARCHAR(12) COMMENT '代理人组别',
    trainperiods             VARCHAR(8) COMMENT '培训期数',
    age                      INT COMMENT '代理人年龄',
    channelname              VARCHAR(100) COMMENT '所属渠道',
    indexcalno               VARCHAR(8) COMMENT '指标计算编码',
    receiptno                VARCHAR(30) COMMENT '保证金收据号',
    idnotype                 VARCHAR(2) COMMENT '证件号码类型',
    branchtype2              VARCHAR(2) COMMENT '渠道',
    trainpassflag            VARCHAR(1) COMMENT '培训通过标记',
    emergentlink             VARCHAR(10) COMMENT '紧急联系人',
    emergentphone            VARCHAR(20) COMMENT '紧急联系人电话',
    retainstartdate          DATE COMMENT '劳动合同开始日期',
    retainenddate            DATE COMMENT '劳动合同截至日期',
    togaeflag                VARCHAR(1) COMMENT '司服是否领取标志',
    archievecode             VARCHAR(20) COMMENT '档案编码',
    comage                   INT COMMENT '司龄',
    busiage                  INT COMMENT '职龄',
    serviceage               INT COMMENT '工龄',
    fortes                   VARCHAR(20) COMMENT '特长',
    hobbies                  VARCHAR(20) COMMENT '爱好',
    officeflag               VARCHAR(1) COMMENT '是否驻厅',
    elecassigndate           DATE COMMENT '供电机构分配时间',
    elecbackdate             DATE COMMENT '供电机构还原时间',
    statef                   VARCHAR(1) COMMENT '审核状态',
    agentsource              VARCHAR(2) COMMENT '来源',
    violation                VARCHAR(1) COMMENT '是否有违规违纪记录',
    foreven                  VARCHAR(1) COMMENT '是否有投连销售资格',
    acctype                  VARCHAR(1) COMMENT '对公对私标识',
    bankaccprovince          VARCHAR(120) COMMENT '开户行省',
    bankacccity              VARCHAR(120) COMMENT '开户市',
    accattributes            VARCHAR(1) COMMENT '卡折类型',
    bankaccsub               VARCHAR(200) COMMENT '开户行细分',
    worknature               VARCHAR(2) COMMENT '工作性质',
    employmentnature         VARCHAR(2)  DEFAULT NULL COMMENT '用工性质',
    supervisiontel           VARCHAR(50) DEFAULT NULL COMMENT '保险行业协会监督电话',
    practiceregistrationmark VARCHAR(1)  DEFAULT NULL COMMENT '执业登记标志',
    breedbenefit             VARCHAR(1) COMMENT '是否享受育成利益',
    addbenefit               VARCHAR(1) COMMENT '是否享受增员利益',
    insurancecertificate     VARCHAR(1) COMMENT '是否有分红保险及万能保险销售资质证书',
    investment               VARCHAR(1) COMMENT '是否有投资连结保险及变额年金保险销售资质证书',
    operator                 VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate                 DATE COMMENT '入机日期',
    maketime                 VARCHAR(8) COMMENT '入机时间',
    modifyoperator           VARCHAR(60) not null comment '最后一次修改人',
    modifydate               DATE COMMENT '最后一次修改日期',
    modifytime               VARCHAR(8) COMMENT '最后一次修改时间',
    lastoperator             VARCHAR(60) COMMENT '备份人',
    lastmakedatetime         DATETIME    DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='代理人信息备份表';

-- ----------------------------
-- Table structure for latree-销管行政信息表
-- ----------------------------
DROP TABLE IF EXISTS latree;
CREATE TABLE IF NOT EXISTS latree
(
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup       VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentseries      VARCHAR(2)     DEFAULT NULL COMMENT '代理人系列',
    agentgrade       VARCHAR(4)     DEFAULT NULL COMMENT '代理人级别',
    agentlastseries  VARCHAR(2)     DEFAULT NULL COMMENT '代理人上次系列',
    agentlastgrade   VARCHAR(4)     DEFAULT NULL COMMENT '代理人上次级别',
    introagency      VARCHAR(10)    DEFAULT NULL COMMENT '增员代理人',
    upagent          VARCHAR(10)    DEFAULT NULL COMMENT '上级代理人',
    othupagent       VARCHAR(10)    DEFAULT NULL COMMENT '其他上级代理人',
    introbreakflag   VARCHAR(1)     DEFAULT NULL COMMENT '增员链断裂标记',
    introcommstart   DATE           DEFAULT NULL COMMENT '增员抽佣起期',
    introcommend     DATE           DEFAULT NULL COMMENT '增员抽佣止期',
    edumanager       VARCHAR(10)    DEFAULT NULL COMMENT '育成主管代理人',
    rearbreakflag    VARCHAR(1)     DEFAULT NULL COMMENT '育成链断裂标记',
    rearcommstart    DATE           DEFAULT NULL COMMENT '育成抽佣起期',
    rearcommend      DATE           DEFAULT NULL COMMENT '育成抽佣止期',
    ascriptsseries   VARCHAR(200)   DEFAULT NULL COMMENT '归属顺序',
    oldstartdate     DATE           DEFAULT NULL COMMENT '前职级起聘日期',
    oldenddate       DATE           DEFAULT NULL COMMENT '前职级解聘日期',
    startdate        DATE           DEFAULT NULL COMMENT '现职级起聘日期',
    astartdate       DATE           DEFAULT NULL COMMENT '考核开始日期',
    assesstype       VARCHAR(1)     DEFAULT NULL COMMENT '考核类型',
    state            VARCHAR(2)     DEFAULT NULL COMMENT '代理人状态',
    branchcode       VARCHAR(12)    DEFAULT NULL COMMENT '代理人组别',
    agentkind        VARCHAR(6)     DEFAULT NULL COMMENT '代理人类别',
    branchtype       VARCHAR(2)     DEFAULT NULL COMMENT '展业类型',
    isconnman        VARCHAR(2)     DEFAULT NULL COMMENT '同业衔接人员标记',
    initgrade        VARCHAR(4)     DEFAULT NULL COMMENT '入司职级',
    agentline        VARCHAR(2)     DEFAULT NULL COMMENT '代理人发展路线',
    insideflag       VARCHAR(2)     DEFAULT NULL COMMENT '内外勤标记',
    speciflag        VARCHAR(2)     DEFAULT NULL COMMENT '特殊人员标记',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    vipproperty      VARCHAR(20)    DEFAULT NULL COMMENT '首次考核时长',
    connmanagerstate VARCHAR(1)     DEFAULT NULL COMMENT '衔接人员状态标志',
    tollflag         VARCHAR(1)     DEFAULT NULL COMMENT '兼专职收费员标志',
    difficulty       DECIMAL(12, 2) DEFAULT NULL COMMENT '收费员难度系数',
    connsuccdate     DATE           DEFAULT NULL COMMENT '定级完成日期',
    tollscope        VARCHAR(1)     DEFAULT NULL COMMENT '首次考核状态',
    agentgradersn    VARCHAR(1)     DEFAULT NULL COMMENT '当前职级起聘原因',
    oldmstartdate    DATE           DEFAULT NULL COMMENT '标准组分配起期',
    oldmenddate      DATE           DEFAULT NULL COMMENT '标准组分配止期',
    mstartdate       DATE           DEFAULT NULL COMMENT '现管理职级起聘日期',
    specimonths      VARCHAR(2)     DEFAULT NULL COMMENT '系统聘才月数',
    specistartdate   DATE           DEFAULT NULL COMMENT '聘才起期',
    specienddate     DATE           DEFAULT NULL COMMENT '聘才止期',
    specistate       VARCHAR(2)     DEFAULT NULL COMMENT '方案状态',
    gdinno           VARCHAR(30)    DEFAULT NULL COMMENT '农电工工作证号',
    f1               VARCHAR(20)    DEFAULT NULL COMMENT 'F1',
    f2               VARCHAR(20)    DEFAULT NULL COMMENT 'F2',
    f3               DECIMAL(12, 2) DEFAULT NULL COMMENT 'F3',
    b1               VARCHAR(50)    DEFAULT NULL COMMENT 'B1',
    b2               VARCHAR(50)    DEFAULT NULL COMMENT 'B2',
    b3               VARCHAR(50)    DEFAULT NULL COMMENT 'B3',
    b4               VARCHAR(50)    DEFAULT NULL COMMENT 'B4',
    b5               VARCHAR(50)    DEFAULT NULL COMMENT 'B5',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE           DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate       DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (agentcode),
    KEY `idx_latree_index1` (`branchtype`),
    KEY `idx_latree_index2` (`managecom`),
    KEY `idx_latree_index3` (`agentgroup`)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管行政信息表';


-- ----------------------------
-- Table structure for latreeb-销管行政信息备份表
-- ----------------------------
DROP TABLE IF EXISTS latreeb;
CREATE TABLE IF NOT EXISTS latreeb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup       VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    agentseries      VARCHAR(2)     DEFAULT NULL COMMENT '代理人系列',
    agentgrade       VARCHAR(4)     DEFAULT NULL COMMENT '代理人级别',
    agentlastseries  VARCHAR(2)     DEFAULT NULL COMMENT '代理人上次系列',
    agentlastgrade   VARCHAR(4)     DEFAULT NULL COMMENT '代理人上次级别',
    introagency      VARCHAR(10)    DEFAULT NULL COMMENT '增员代理人',
    upagent          VARCHAR(10)    DEFAULT NULL COMMENT '上级代理人',
    othupagent       VARCHAR(10)    DEFAULT NULL COMMENT '其他上级代理人',
    introbreakflag   VARCHAR(1)     DEFAULT NULL COMMENT '增员链断裂标记',
    introcommstart   DATE           DEFAULT NULL COMMENT '增员抽佣起期',
    introcommend     DATE           DEFAULT NULL COMMENT '增员抽佣止期',
    edumanager       VARCHAR(10)    DEFAULT NULL COMMENT '育成主管代理人',
    rearbreakflag    VARCHAR(1)     DEFAULT NULL COMMENT '育成链断裂标记',
    rearcommstart    DATE           DEFAULT NULL COMMENT '育成抽佣起期',
    rearcommend      DATE           DEFAULT NULL COMMENT '育成抽佣止期',
    ascriptsseries   VARCHAR(200)   DEFAULT NULL COMMENT '归属顺序',
    oldstartdate     DATE           DEFAULT NULL COMMENT '前职级起聘日期',
    oldenddate       DATE           DEFAULT NULL COMMENT '前职级解聘日期',
    startdate        DATE           DEFAULT NULL COMMENT '现职级起聘日期',
    astartdate       DATE           DEFAULT NULL COMMENT '考核开始日期',
    assesstype       VARCHAR(1)     DEFAULT NULL COMMENT '考核类型',
    state            VARCHAR(10)    DEFAULT NULL COMMENT '考核状态',
    removetype       VARCHAR(10)    DEFAULT NULL COMMENT '转储类别',
    branchcode       VARCHAR(12)    DEFAULT NULL COMMENT '员工属性',
    indexcalno       VARCHAR(8)     DEFAULT NULL COMMENT '指标计算编码',
    agentkind        VARCHAR(6)     DEFAULT NULL COMMENT '代理人类别',
    branchtpe        VARCHAR(2)     DEFAULT NULL COMMENT '展业类型',
    isconnman        VARCHAR(2)     DEFAULT NULL COMMENT '同业衔接人员标记',
    initgrade        VARCHAR(4)     DEFAULT NULL COMMENT '入司职级',
    agentline        VARCHAR(2)     DEFAULT NULL COMMENT '代理人发展路线',
    insideflag       VARCHAR(2)     DEFAULT NULL COMMENT '内外勤标记',
    speciflag        VARCHAR(2)     DEFAULT NULL COMMENT '特殊人员标记',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    vipproperty      VARCHAR(20)    DEFAULT NULL COMMENT '首次考核时长',
    connmanagerstate VARCHAR(1)     DEFAULT NULL COMMENT '衔接人员状态标志',
    tollflag         VARCHAR(1)     DEFAULT NULL COMMENT '收费员标志',
    difficulty       DECIMAL(12, 2) DEFAULT NULL COMMENT '收费员难度系数',
    connsuccdate     DATE           DEFAULT NULL COMMENT '定级完成日期',
    tollscope        VARCHAR(1)     DEFAULT NULL COMMENT '首次考核状态',
    agentgradersn    VARCHAR(1)     DEFAULT NULL COMMENT '当前职级起聘原因',
    oldmstartdate    DATE           DEFAULT NULL COMMENT '前管理职级起聘日期',
    oldmenddate      DATE           DEFAULT NULL COMMENT '首次考核日期',
    mstartdate       DATE           DEFAULT NULL COMMENT '现管理职级起聘日期',
    specimonths      INT(2)         DEFAULT NULL COMMENT '系统聘才月数',
    specistartdate   DATE           DEFAULT NULL COMMENT '聘才起期',
    specienddate     DATE           DEFAULT NULL COMMENT '聘才止期',
    specistate       VARCHAR(2)     DEFAULT NULL COMMENT '方案状态',
    gdinno           VARCHAR(30)    DEFAULT NULL COMMENT '农电工工作证号',
    f1               VARCHAR(20)    DEFAULT NULL COMMENT 'F1',
    f2               VARCHAR(20)    DEFAULT NULL COMMENT 'F2',
    f3               DECIMAL(12, 2) DEFAULT NULL COMMENT 'F3',
    b1               VARCHAR(50)    DEFAULT NULL COMMENT 'B1',
    b2               VARCHAR(50)    DEFAULT NULL COMMENT 'B2',
    b3               VARCHAR(50)    DEFAULT NULL COMMENT 'B3',
    b4               VARCHAR(50)    DEFAULT NULL COMMENT 'B4',
    b5               VARCHAR(50)    DEFAULT NULL COMMENT 'B5',
    operator2        VARCHAR(20)    DEFAULT NULL COMMENT '原操作员代码',
    makedate2        DATE           DEFAULT NULL COMMENT '原入机日期',
    maketime2        VARCHAR(8)     DEFAULT NULL COMMENT '原入机时间',
    modifydate2      DATE           DEFAULT NULL COMMENT '原最后一次修改日期',
    modifytime2      VARCHAR(8)     DEFAULT NULL COMMENT '原最后一次修改时间',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE           DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME       DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管行政信息备份表';

drop table if exists lafamilyinfo;
create table lafamilyinfo
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    relation       VARCHAR(100) null comment '关系',
    name           VARCHAR(100) null comment '姓名',
    idnotype       VARCHAR(2)   null comment '证件类型',
    idno           VARCHAR(100) null comment '证件号',
    company        VARCHAR(100) null comment '工作单位公司',
    post           VARCHAR(100) null comment '岗位',
    occupation     VARCHAR(100) null comment '职业',
    phone          VARCHAR(100) null comment '电话',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_lafamilyinfo_index1` (`agentcode`),
    KEY `idx_lafamilyinfo_index2` (`idno`),
    KEY `idx_lafamilyinfo_index3` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='家庭信息';

drop table if exists laworkinfo;
create table laworkinfo
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    company        VARCHAR(100) null comment '公司名称',
    type           VARCHAR(100) null comment '行业类别',
    post           VARCHAR(100) null comment '岗位',
    annualsalary   VARCHAR(100) null comment '年薪',
    startdate      DATE         null comment '起始时间',
    enddate        DATE         null comment '结束时间',
    leavereason    VARCHAR(100) null comment '离职理由',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_laworkinfo_index1` (`agentcode`),
    KEY `idx_laworkinfo_index2` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='工作信息表';

drop table if exists laeducationinfo;
create table laeducationinfo
(
    id             bigint(20) auto_increment primary key comment '主键',
    agentcode      VARCHAR(100) null comment '工号',
    branchtype     CHAR(2)      not null comment '渠道',
    school         VARCHAR(100) null comment '学校名称',
    type           VARCHAR(100) null comment '学历',
    major          VARCHAR(100) null comment '专业',
    beginyear      VARCHAR(100) null comment '入学年度',
    endyear        VARCHAR(100) null comment '毕业年度',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_laeducationinfo_index1` (`agentcode`),
    KEY `idx_laeducationinfo_index2` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='学历信息';


drop table if exists lafamilyinfob;
create table lafamilyinfob
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    relation         VARCHAR(100) null comment '关系',
    name             VARCHAR(100) null comment '姓名',
    idnotype         VARCHAR(2)   null comment '证件类型',
    idno             VARCHAR(100) null comment '证件号',
    company          VARCHAR(100) null comment '工作单位公司',
    post             VARCHAR(100) null comment '岗位',
    occupation       VARCHAR(100) null comment '职业',
    phone            VARCHAR(100) null comment '电话',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_lafamilyinfob_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='家庭信息备份表';

drop table if exists laworkinfob;
create table laworkinfob
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    company          VARCHAR(100) null comment '公司名称',
    type             VARCHAR(100) null comment '行业类别',
    post             VARCHAR(100) null comment '岗位',
    annualsalary     VARCHAR(100) null comment '年薪',
    startdate        DATE         null comment '起始时间',
    enddate          DATE         null comment '结束时间',
    leavereason      VARCHAR(100) null comment '离职理由',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_laworkinfob_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='工作信息备份表';

drop table if exists laeducationinfob;
create table laeducationinfob
(
    edorno           VARCHAR(20)  NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)   NOT NULL COMMENT '转储类型',
    id               bigint(20)   not null comment '主键',
    agentcode        VARCHAR(100) null comment '工号',
    branchtype       CHAR(2)      not null comment '渠道',
    school           VARCHAR(100) null comment '学校名称',
    type             VARCHAR(100) null comment '学历',
    major            VARCHAR(100) null comment '专业',
    beginyear        VARCHAR(100) null comment '入学年度',
    endyear          VARCHAR(100) null comment '毕业年度',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    KEY `idx_laeducationinfob_index1` (`agentcode`),
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='学历信息备份表';

drop table if exists ldcom;
create table ldcom
(
    comcode            varchar(11)  not null comment '机构编码' primary key,
    outcomcode         varchar(11)  null comment '对外公布的机构代码',
    name               varchar(60)  null comment '机构名称',
    shortname          varchar(60)  null comment '短名称',
    address            varchar(80)  null comment '机构地址',
    zipcode            varchar(6)   null comment '机构邮编',
    phone              varchar(18)  null comment '机构电话',
    fax                varchar(18)  null comment '机构传真',
    email              varchar(60)  null comment '电子邮件',
    webaddress         varchar(100) null comment '机构网址',
    satrapname         varchar(20)  null comment '主管人姓名',
    insumonitorcode    varchar(10)  null comment '对应保监办代码',
    insureid           varchar(12)  null comment '保险公司id',
    signid             varchar(6)   null comment '标识码',
    regionalismcode    varchar(6)   null comment '行政区划代码',
    comnature          varchar(1)   null comment '公司性质',
    validcode          varchar(2)   null comment '校验码',
    sign               varchar(10)  null comment '标志',
    comcitysize        varchar(1)   null comment '机构地区规模',
    servicename        varchar(120) null comment '客户服务机构名称',
    serviceno          varchar(60)  null comment '客户服务机构编码',
    servicephone       varchar(18)  null comment '客户服务电话',
    servicepostaddress varchar(120) null comment '客户服务投递地址',
    comgrade           varchar(2)   null comment '机构级别',
    comareatype        varchar(2)   null comment '机构地区类型',
    upcomcode          varchar(10)  null comment '上级管理机构代码',
    isdirunder         varchar(1)   null comment '直属属性',
    opendate           date         null comment '成立日期',
    firstsigndate      date         null comment '首次签约日期',
    firstsigntime      varchar(8)   null comment '首次签约时间',
    headno             varchar(20)  null comment '总部编号',
    companycode1       varchar(20)  null comment '公司编码1',
    companycode2       varchar(16)  null comment '公司编码2',
    operator           varchar(60)  null comment '操作员',
    makedate           date         null comment '入机日期',
    maketime           varchar(8)   null comment '入机时间',
    modifyoperator     varchar(20)  null comment '最后一次修改人',
    modifydate         date         null comment '最后一次修改日期',
    modifytime         varchar(8)   null comment '最后一次修改时间'
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_general_ci comment ='管理机构表';



drop table if exists laagentrelatives;
create table laagentrelatives
(
    id              bigint(20) primary key not null comment '流水号',
    agentcode       varchar(20)            not null comment '工号',
    branchtype      varchar(2)             not null comment '渠道',
    relativestype   varchar(2)             null comment '亲属关系类型',
    relativesstate  varchar(10) default '' not null comment '状态',
    relativesname   varchar(20)            null comment '亲属姓名',
    relativesidtype varchar(10)            not null comment '亲属证件类型',
    relativesidno   varchar(30)            not null comment '亲属证件号',
    relativesmoblie varchar(20)            null comment '亲属手机',
    remark          varchar(100)           null comment '备注',
    relativeswork   varchar(2)             not null comment '职业',
    f01             varchar(10)            null comment '',
    f02             varchar(20)            null comment '',
    f03             decimal(12, 6)         null comment '',
    operator        varchar(60)            null comment '操作员',
    makedate        date                   null comment '入机日期',
    maketime        varchar(8)             null comment '入机时间',
    modifyoperator  varchar(20)            null comment '最后一次修改人',
    modifydate      date                   null comment '最后一次修改日期',
    modifytime      varchar(8)             null comment '最后一次修改时间',
    KEY `idx_laagentrelatives_index` (`agentcode`, `branchtype`),
    KEY `idx_laagentrelatives_index1` (`relativesidno`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='亲属关系表';

drop table if exists laagentinformation;
create table laagentinformation
(
    id                  bigint(20) auto_increment comment '主键',
    yearmonth           varchar(8)   default '' not null comment '快照年月',
    agentcode           varchar(20)  default '' not null comment '工号',
    agentname           varchar(30)  default '' null comment '姓名',
    managecom           varchar(10)  default '' null comment '管理机构',
    agentseries         varchar(5)   default '' null comment '销售序列',
    agentgrade          varchar(20)  default '' null comment '职级',
    agentgroup          varchar(20)  default '' null comment '销售机构',
    branchattr          varchar(20)  default '' null comment '销售机构外部代码',
    branchname          varchar(100) default '' null comment '销售机构名称',
    branchmanager       varchar(30)  default '' null comment '主管',
    branchmanagername   varchar(30)  default '' null comment '主管',
    quagentgroup        varchar(20)  default '' null comment '区销售机构',
    qubranchname        varchar(100) default '' null comment '区销售机构',
    qubranchattr        varchar(70)  default '' null comment '区代码',
    qubranchmanager     varchar(30)  default '' null comment '区主管',
    qubranchmanagername varchar(30)  default '' null comment '区主管',
    buagentgroup        varchar(20)  default '' null comment '部销售机构',
    bubranchname        varchar(100) default '' null comment '部销售机构',
    bubranchattr        varchar(70)  default '' null comment '部代码',
    bubranchmanager     varchar(30)  default '' null comment '部主管',
    bubranchmanagername varchar(30)  default '' null comment '部主管',
    zuagentgroup        varchar(20)  default '' null comment '组销售机构',
    zubranchname        varchar(100) default '' null comment '区销售机构',
    zubranchattr        varchar(70)  default '' null comment '组代码',
    zubranchmanager     varchar(30)  default '' null comment '组主管',
    zubranchmanagername varchar(30)  default '' null comment '组主管',
    persontype          varchar(20)  default '' null comment '人员性质',
    branchtype          varchar(10)  default '' null comment '渠道',
    f1                  varchar(20)             null,
    f2                  varchar(30)             null,
    f3                  varchar(20)             null,
    f4                  decimal(20, 4)          null,
    f5                  date                    null,
    operator            varchar(60)             null comment '操作员',
    makedate            date                    null comment '入机日期',
    maketime            varchar(8)              null comment '入机时间',
    modifyoperator      varchar(20)             null comment '最后一次修改人',
    modifydate          date                    null comment '最后一次修改日期',
    modifytime          varchar(8)              null comment '最后一次修改时间',
    primary key (id),
    KEY `idx_laagentinformation_index1` (`agentcode`, `yearmonth`),
    KEY `idx_laagentinformation_index2` (`branchtype`,`yearmonth`,`managecom`,`agentgroup`),
    KEY `idx_laagentinformation_index3` (`quagentgroup`, `buagentgroup`, `zuagentgroup`)
) engine = innodb
  auto_increment = 1
  default charset = utf8mb4
  collate = utf8mb4_general_ci comment ='人员信息快照';

drop table if exists laagentblacklist;
create table laagentblacklist
(
    id               bigint(20) auto_increment primary key comment '主键',
    idno             varchar(20)  not null comment '证件号',
    agentname        varchar(100) not null comment '姓名',
    agentcode        varchar(10)  null comment '工号',
    insurancecompany varchar(50)  null comment '保险公司',
    source           varchar(2)   not null comment '来源',
    handlingmethod   varchar(2)   not null comment '处理办法',
    reason           varchar(500) null comment '原因',
    branchtype       CHAR(2)      not null comment '渠道',
    effectivedate    date         not null comment '生效日期',
    expirydate       date         null comment '失效日期',
    operator         varchar(60)  null comment '操作员',
    makedate         date         null comment '入机日期',
    maketime         varchar(8)   null comment '入机时间',
    modifyoperator   varchar(20)  null comment '最后一次修改人',
    modifydate       date         null comment '最后一次修改日期',
    modifytime       varchar(8)   null comment '最后一次修改时间',
    KEY `idx_laagentblacklist_index` (`idno`),
    KEY `idx_laagentblacklist_index1` (`agentcode`)
) engine = innodb
  auto_increment = 1
  default charset = utf8mb4
  collate = utf8mb4_general_ci comment ='人员黑名单';

drop table if exists labranchtoagent;
create table labranchtoagent
(
    id             bigint(20) auto_increment primary key comment '主键',
    code           varchar(20)            not null comment '业务类型',
    agentcode      varchar(10)            not null comment '工号',
    agentgroup     varchar(12) default '' not null comment '销售机构',
    branchtype     CHAR(2)                not null comment '渠道',
    startdate      date                   null comment '分配起期',
    enddate        date                   null comment '止期',
    f1             varchar(20)            null comment '备用字段',
    f2             varchar(20)            null comment '备用字段',
    f3             decimal(20, 4)         null comment '备用字段',
    f4             decimal(20, 4)         null comment '备用字段',
    f5             date                   null comment '备用字段',
    operator       VARCHAR(60)            not null COMMENT '操作员',
    makedate       DATE                   not null COMMENT '入机日期',
    maketime       VARCHAR(8)             not null COMMENT '入机时间',
    modifyoperator VARCHAR(60) comment '最后一次修改人',
    modifydate     DATE COMMENT '最近一次修改日期',
    modifytime     VARCHAR(8) COMMENT '最近一次修改时间',
    KEY `idx_labranchtoagent_agentgroup` (`agentgroup`),
    KEY `idx_labranchtoagent_agentcode` (`agentcode`),
    KEY `idx_labranchtoagent_code` (`code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='机构与代理人关系表';

drop table if exists lapersonblack;
create table lapersonblack
(
    id             bigint(20) auto_increment primary key comment '主键',
    idnotype       VARCHAR(100) not null comment '证件类型',
    idno           VARCHAR(100) not null comment '工号',
    name           VARCHAR(100) not null comment '姓名',
    branchtype     CHAR(2)      not null comment '渠道',
    isblack        VARCHAR(10)  null comment '是否黑名单',
    blackreason    VARCHAR(100) null comment '黑名单原因',
    branchattr     VARCHAR(100) null comment '团队代码',
    agentgroup     VARCHAR(100) null comment '团队',
    operator       varchar(60)  null comment '操作员',
    makedate       date         null comment '入机日期',
    maketime       varchar(8)   null comment '入机时间',
    modifyoperator varchar(20)  null comment '最后一次修改人',
    modifydate     date         null comment '最后一次修改日期',
    modifytime     varchar(8)   null comment '最后一次修改时间',
    KEY `idx_lapersonblack_index1` (`idno`),
    KEY `idx_lapersonblack_index2` (`branchtype`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='人员黑名单表';

DROP TABLE IF EXISTS lamanoeuvre;
/*==============================================================*/
/* Table: lamanoeuvre                                 */
/*==============================================================*/
CREATE TABLE lamanoeuvre
(
    edorno          varchar(20)  not null comment '转储号码' primary key,
    agentcode       varchar(10)  not null comment '业务人员工号',
    edortype        varchar(2)   not null comment '转储类型',
    manoeuvredate   date         not null comment '调动日期',
    branchtype      varchar(1)   null comment '展业类型',
    branchtype2     varchar(2)   null comment '渠道类型',
    premanagecom    varchar(10)  null comment '调动前所在管理机构',
    curmanagecom    varchar(10)  null comment '调动后所在管理机构',
    prebranchcode   varchar(20)  null comment '调动前所在机构内部编码',
    curbranchcode   varchar(20)  null comment '调动后所在机构内部编码',
    prebranchattr   varchar(20)  null comment '调动前所在机构',
    curbranchattr   varchar(20)  null comment '调动后所在机构',
    prebranchseries varchar(40)  null comment '调动前所在机构序列号',
    curbranchseries varchar(40)  null comment '调动后所在机构序列号',
    preagentgrade   varchar(10)  null comment '调动前职级',
    curagentgrade   varchar(10)  null comment '调动后职级',
    predistict      varchar(12)  default '' comment '调动前所在区',
    curdistict      varchar(12)  default '' comment '调动后所在区',
    predepartment   varchar(12)  default '' comment '调动前所在部',
    curdepartment   varchar(12)  default '' comment '调动后所在部',
    preunit         varchar(12)  default '' comment '调动前所在组',
    curunit         varchar(12)  default '' comment '调动后所在组',
    prequmanager    varchar(20)  default '' comment '调动前区主管',
    prebumanager    varchar(20)  default '' comment '调动前部主管',
    prezumanager    varchar(20)  default '' comment '调动前组主管',
    curqumanager    varchar(20)  default '' comment '调动后区主管',
    curbumanager    varchar(20)  default '' comment '调动后部主管',
    curzumanager    varchar(20)  default '' comment '调动后组主管',
    prequname       varchar(100) default '' comment '调动前区名称',
    prebunmae       varchar(100) default '' comment '调动前部名称',
    prezuname       varchar(100) default '' comment '调动前组名称',
    curquname       varchar(100) default '' comment '调动后区名称',
    curbuname       varchar(100) default '' comment '调动后部名称',
    curzuname       varchar(100) default '' comment '调动后组名称',
    remark          varchar(100) null comment '调动原因',
    operator        varchar(60)  null comment '操作员',
    makedate        date         null comment '入机日期',
    maketime        varchar(8)   null comment '入机时间',
    modifyoperator  varchar(60)  not null comment '最后一次修改人',
    modifydate      date         null comment '最近一次修改日期',
    modifytime      varchar(8)   null comment '最近一次修改时间',
    KEY `idx_lamanoeuvre_index1` (`branchtype`),
    KEY `idx_lamanoeuvre_index2` (`premanagecom`),
    KEY `idx_lamanoeuvre_index3` (`prebranchcode`),
    KEY `idx_lamanoeuvre_index4` (`agentcode`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='人员移动轨迹表';


DROP TABLE IF EXISTS lapresence;
/*==============================================================*/
/* Table: lapresence                                            */
/*==============================================================*/
CREATE TABLE lapresence
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    agentgroup     VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom      VARCHAR(8) COMMENT '管理机构',
    idx            INT         NOT NULL COMMENT '纪录顺序号',
    indexcalno     VARCHAR(8) COMMENT '指标计算编码',
    times          INT COMMENT '考勤执行次数',
    aclass         VARCHAR(2) COMMENT '类别',
    donedate       DATE COMMENT '执行日期',
    summoney       DECIMAL(12, 2) COMMENT '金额',
    noti           VARCHAR(40) COMMENT '批注',
    doneflag       VARCHAR(20) COMMENT '处理标志',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchattr     VARCHAR(20) COMMENT '展业机构外部编码',
    branchtype2    VARCHAR(2) COMMENT '渠道',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_agentcode_idx (agentcode, idx),
    KEY `idx_lapresence_index1` (`branchtype`),
    KEY `idx_lapresence_index2` (`managecom`),
    KEY `idx_lapresence_index3` (`agentgroup`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管出勤信息表';

DROP TABLE IF EXISTS lapresencemain;
/*==============================================================*/
/* Table: lapresencemain                                        */
/*==============================================================*/
CREATE TABLE lapresencemain
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    indexcalno     VARCHAR(8)  NOT NULL COMMENT '薪资年月',
    donedate       DATE        NOT NULL COMMENT '出勤日期',
    managecom      VARCHAR(8)  NOT NULL COMMENT '营销服务部',
    state          VARCHAR(2) COMMENT '状态',
    assesscount    INT COMMENT '应出勤人数',
    confirmcount   INT COMMENT '实际出勤人数',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    branchtype2    VARCHAR(2) COMMENT '01 or null -直销 02-中介 03-交叉销售',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY unique_indexcalno_donedate_managecom (indexcalno, donedate, managecom)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管出勤信息计算状态表';


DROP TABLE IF EXISTS lapresencerateb;
/*==============================================================*/
/* Table: lapresencerateb                                       */
/*==============================================================*/
CREATE TABLE lapresencerateb
(
    edorno           VARCHAR(10)    NOT NULL COMMENT '转储号',
    edortype         VARCHAR(2)     NOT NULL COMMENT '转储类型',
    agentcode        VARCHAR(10)    NOT NULL COMMENT '代理人编码',
    agentgroup       VARCHAR(12)    NOT NULL COMMENT '销售机构',
    managecom        VARCHAR(8) COMMENT '管理机构',
    indexcalno       VARCHAR(8)     NOT NULL COMMENT '薪资计算年月',
    dutycount        INT            NOT NULL COMMENT '应出勤天数',
    factcount        INT            NOT NULL COMMENT '实际出勤天数',
    presencerate     DECIMAL(10, 2) NOT NULL COMMENT '保留2位小数',
    noti             VARCHAR(100) COMMENT '批注',
    branchtype       VARCHAR(2)     NOT NULL COMMENT '1、个险渠道 2、团险渠道 3、银保渠道 5、电销渠道',
    branchtype2      VARCHAR(2) COMMENT '01 or null -直销 02-中介 03-交叉销售',
    f1               VARCHAR(20) COMMENT 'F1',
    f2               VARCHAR(20) COMMENT 'F2',
    f3               VARCHAR(20) COMMENT 'F3',
    f4               DECIMAL(10, 2) COMMENT 'F4',
    f5               DECIMAL(10, 2) COMMENT 'F5',
    operator         VARCHAR(60)    NOT NULL COMMENT '操作员',
    makedate         DATE           NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     NOT NULL COMMENT '入机时间',
    modifyoperator   VARCHAR(20)    NOT NULL COMMENT '最后一次修改人',
    modifydate       DATE           NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     NOT NULL COMMENT '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管出勤率信息备份表';

DROP TABLE IF EXISTS lapresencerate;
/*==============================================================*/
/* Table: lapresencerate                                        */
/*==============================================================*/
CREATE TABLE lapresencerate
(
    id             BIGINT(20)     NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10)    NOT NULL COMMENT '代理人编码',
    agentgroup     VARCHAR(12)    NOT NULL COMMENT '销售机构',
    managecom      VARCHAR(8) COMMENT '管理机构',
    indexcalno     VARCHAR(8)     NOT NULL COMMENT '考勤的年月',
    dutycount      INT            NOT NULL COMMENT '考勤的年月',
    factcount      INT            NOT NULL COMMENT '考勤的年月',
    presencerate   DECIMAL(14, 6) NOT NULL COMMENT '考勤的年月',
    noti           VARCHAR(100) COMMENT '批注',
    branchtype     VARCHAR(2)     NOT NULL COMMENT '展业类型(1-个人营销，2-团险，3－银行保险，9－其他)',
    branchtype2    VARCHAR(2) COMMENT '01 or null -直销 02-中介 03-交叉销售',
    f1             VARCHAR(20) COMMENT 'F1',
    f2             VARCHAR(20) COMMENT 'F2',
    f3             VARCHAR(20) COMMENT 'F3',
    f4             DECIMAL(10, 2) COMMENT '团险。应出勤天数',
    f5             DECIMAL(10, 2) COMMENT '团险。缺勤天数',
    operator       VARCHAR(60)    NOT NULL COMMENT '操作员',
    makedate       DATE           NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     NOT NULL COMMENT '入机时间',
    modifydate     DATE           NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20)    NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_agentcode_indexcalno (agentcode, indexcalno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管出勤率信息表';

DROP TABLE IF EXISTS lagrppresence;
/*==============================================================*/
/* Table: lagrppresence                                         */
/*==============================================================*/
CREATE TABLE lagrppresence
(
    id             BIGINT(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    agentgroup     VARCHAR(12) COMMENT '代理人组别',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    leavetype      VARCHAR(2)  NOT NULL COMMENT '请假类型',
    leavestartdate DATE        NOT NULL COMMENT '请假起期',
    leaveenddate   DATE        NOT NULL COMMENT '请假止期',
    leavedays      VARCHAR(10) NOT NULL COMMENT '请假天数',
    wageno         VARCHAR(20) NOT NULL COMMENT '佣金计算年月代码',
    f1             VARCHAR(20) COMMENT '备用字段1',
    f2             VARCHAR(20) COMMENT '备用字段2',
    f3             DECIMAL(12, 4) COMMENT '备用字段3',
    f4             DECIMAL(12, 4) COMMENT '备用字段4',
    f5             DATE COMMENT '备用字段5',
    f6             DATE COMMENT '备用字段6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(20) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_composite (agentcode, leavestartdate, leaveenddate)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管团险考勤请假记录信息表';

DROP TABLE IF EXISTS lagrpgradevalid;
CREATE TABLE lagrpgradevalid
(
    id             bigint(20)   NOT NULL COMMENT '主键',
    agentcode      VARCHAR(10)  NOT NULL COMMENT '代理人编码',
    oldagentgrade  VARCHAR(4)   NOT NULL COMMENT '原代理人级别',
    newagentgrade  VARCHAR(4)   NOT NULL COMMENT '现代理人级别',
    validstartdate DATE         NOT NULL COMMENT '有效起期',
    validenddate   DATE COMMENT '有效止期',
    operatepath    VARCHAR(200) NOT NULL COMMENT '操作路径',
    f1             VARCHAR(20) COMMENT '备用字段1',
    f2             VARCHAR(20) COMMENT '备用字段2',
    f3             DECIMAL(12, 4) COMMENT '备用字段3',
    f4             DECIMAL(12, 4) COMMENT '备用字段4',
    f5             DATE COMMENT '备用字段5',
    f6             DATE COMMENT '备用字段6',
    operator       VARCHAR(60)  NOT NULL COMMENT '操作员',
    makedate       DATE         NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)   NOT NULL COMMENT '入机时间',
    modifydate     DATE         NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)   NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60)  NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_lagrpggradevalid (agentcode, newagentgrade, validstartdate)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COLLATE = utf8mb4_general_ci COMMENT ='销管团险职级轨迹信息表';


DROP TABLE IF EXISTS larecord;

-- 创建销管学习工作经历信息表
CREATE TABLE larecord
(
    id             bigint(20)  NOT NULL COMMENT '主键',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    idx            INT         NOT NULL COMMENT '主键',
    exptype        VARCHAR(1) COMMENT '经历类型',
    school         VARCHAR(20) COMMENT '学校/工作单位',
    speciality     VARCHAR(20) COMMENT '专业/部门',
    degree         VARCHAR(10) COMMENT '学历/职务',
    startdate      DATE COMMENT '起期',
    enddate        DATE COMMENT '止期',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2    VARCHAR(2)  NOT NULL COMMENT '渠道',
    remark         VARCHAR(20) COMMENT '备注',
    standbyflag1   VARCHAR(10) COMMENT '备用1',
    standbyflag2   VARCHAR(20) COMMENT '备用2',
    standbyflag3   VARCHAR(10) COMMENT '备用3',
    standbyflag4   VARCHAR(12) COMMENT '备用4',
    standbyflag5   VARCHAR(12) COMMENT '备用5',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_agentcode_idx (agentcode, idx)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管学习工作经历信息表';

DROP TABLE IF EXISTS lacustomersconfig;
-- 创建销管客户指标信息表
CREATE TABLE lacustomersconfig
(
    id             bigint(20)  NOT NULL COMMENT '主键',
    agentgrade     VARCHAR(4)  NOT NULL COMMENT '代理人级别',
    customers      DECIMAL(12, 1) COMMENT '客户数',
    f1             VARCHAR(20) NOT NULL COMMENT '备用字段1',
    f2             VARCHAR(20) COMMENT '备用字段2',
    f3             VARCHAR(12) COMMENT '备用字段3',
    f4             VARCHAR(12) COMMENT '备用字段4',
    f5             DATE COMMENT '备用字段5',
    f6             DATE COMMENT '备用字段6',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE INDEX unique_agentgrade_f1 (agentgrade, f1)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管客户指标信息表';

DROP TABLE IF EXISTS lazzrsfilepath;

-- 创建销管文件上传信息表
CREATE TABLE `lazzrsfilepath`
(
    `series`       VARCHAR(20) NOT NULL COMMENT '主键',
    `agentcode`    VARCHAR(10) COMMENT '人员工号',
    `filetype`     VARCHAR(20) COMMENT '文件类型',
    `filesrc`      VARCHAR(100) COMMENT '文件路径',
    `filename`     VARCHAR(50) COMMENT '文件名称',
    `branchtype`   VARCHAR(2)  NOT NULL COMMENT '渠道',
    `f1`           VARCHAR(20) COMMENT 'F1',
    `f2`           VARCHAR(20) COMMENT 'F2',
    `f3`           VARCHAR(20) COMMENT 'F3',
    `f4`           VARCHAR(20) COMMENT 'F4',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate       DATE        NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifydate     DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (`series`) -- 使用原有的主键字段
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='销管文件上传信息表';



-- ----------------------------
-- Table structure for ladimissionb-销管离职信息备份表
-- ----------------------------
DROP TABLE IF EXISTS ladimissionb;
CREATE TABLE IF NOT EXISTS ladimissionb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号码',
    id               bigint(20)  NOT NULL COMMENT '流水号',                      -- 新增主键
    agentcode        VARCHAR(10) NOT NULL COMMENT '代理人编码',
    departtimes      INT         NOT NULL COMMENT '离职次数',                    -- 采用 INT 代替 Oracle 的 NUMBER 类型
    departdate       DATE         DEFAULT NULL COMMENT '离职时间',
    departrsn        VARCHAR(50)  DEFAULT NULL COMMENT '离职原因',
    contractflag     VARCHAR(1)   DEFAULT NULL COMMENT '《保险代理合同书》回收标志',
    workflag         VARCHAR(1)   DEFAULT NULL COMMENT '工作证回收标志',
    pbcflag          VARCHAR(1)   DEFAULT NULL COMMENT '展业证回收标志',
    receiptflag      VARCHAR(1)   DEFAULT NULL COMMENT '保险费暂收据回收标志',
    lostflag         VARCHAR(1)   DEFAULT NULL COMMENT '丢失标志',
    checkflag        VARCHAR(1)   DEFAULT NULL COMMENT '结清标志',
    wageflag         VARCHAR(1)   DEFAULT NULL COMMENT '停止佣金发放标志',
    turnflag         VARCHAR(1)   DEFAULT NULL COMMENT '业务人员转任内勤人员标志',
    noti             VARCHAR(100) DEFAULT NULL COMMENT '备注',
    branchtype       VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchattr       VARCHAR(20)  DEFAULT NULL COMMENT '展业机构外部编码',
    departstate      VARCHAR(2)  NOT NULL COMMENT '离职状态',
    returnflag       VARCHAR(2)   DEFAULT NULL COMMENT '押金回退标记',
    destoryflag      VARCHAR(2)   DEFAULT NULL COMMENT '有价单证回销标记',
    arriveflag       VARCHAR(2)   DEFAULT NULL COMMENT '保单送达标记',
    applydate        DATE         DEFAULT NULL COMMENT '离职申请日期',
    branchtype2      VARCHAR(2)   DEFAULT NULL COMMENT '渠道',
    officeflag       VARCHAR(1)   DEFAULT NULL COMMENT '办公用品提交标志',
    handoverflag     VARCHAR(1)   DEFAULT NULL COMMENT '工作移交标记',
    activeflag       VARCHAR(1)   DEFAULT NULL COMMENT '活动手册提交标记',
    clientflag       VARCHAR(1)   DEFAULT NULL COMMENT '客户资料提交标记',
    userinvalidflag  VARCHAR(1)   DEFAULT NULL COMMENT '取消用户标志',
    returnbookflag   VARCHAR(1)   DEFAULT NULL COMMENT '所借图书归还标志',
    informflag       VARCHAR(1)   DEFAULT NULL COMMENT '劳动合同解除通知发放标志',
    edortype         VARCHAR(2)   DEFAULT NULL COMMENT '备份类型',
    makedate2        DATE         DEFAULT NULL COMMENT '原入机日期',
    maketime2        VARCHAR(8)   DEFAULT NULL COMMENT '原入机时间',
    modifydate2      DATE         DEFAULT NULL COMMENT '原最近一次修改日期',
    modifytime2      VARCHAR(8)   DEFAULT NULL COMMENT '原最近一次修改时间',
    operator2        VARCHAR(20)  DEFAULT NULL COMMENT '原操作人员代码',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE         DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)   DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE         DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)   DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME     DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno),                                                        -- 设置 id 为主键
    UNIQUE KEY idx_edorno_agentcode_departtimes (edorno, agentcode, departtimes) -- 创建联合唯一索引
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管离职信息备份表';

-- ----------------------------
-- Table structure for ladimission-销管离职信息表
-- ----------------------------
DROP TABLE IF EXISTS ladimission;
CREATE TABLE IF NOT EXISTS ladimission
(
    id              bigint(20)  NOT NULL COMMENT '流水号',
    agentcode       VARCHAR(10) NOT NULL COMMENT '代理人编码',
    departtimes     INT         NOT NULL COMMENT '离职次数',
    departdate      DATE         DEFAULT NULL COMMENT '离职时间',
    departrsn       VARCHAR(50)  DEFAULT NULL COMMENT '离职原因',
    contractflag    VARCHAR(1)   DEFAULT NULL COMMENT '《保险代理合同书》回收标志',
    workflag        VARCHAR(1)   DEFAULT NULL COMMENT '工作证回收标志',
    pbcflag         VARCHAR(1)   DEFAULT NULL COMMENT '展业证回收标志',
    receiptflag     VARCHAR(1)   DEFAULT NULL COMMENT '保险费暂收据回收标志',
    lostflag        VARCHAR(1)   DEFAULT NULL COMMENT '丢失标志',
    checkflag       VARCHAR(1)   DEFAULT NULL COMMENT '结清标志',
    wageflag        VARCHAR(1)   DEFAULT NULL COMMENT '停止佣金发放标志',
    turnflag        VARCHAR(1)   DEFAULT NULL COMMENT '业务人员转任内勤人员标志',
    noti            VARCHAR(100) DEFAULT NULL COMMENT '备注',
    branchtype      VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchattr      VARCHAR(20)  DEFAULT NULL COMMENT '展业机构外部编码',
    departstate     VARCHAR(2)  NOT NULL COMMENT '离职状态',
    returnflag      VARCHAR(2)   DEFAULT NULL COMMENT '押金回退标记',
    destoryflag     VARCHAR(2)   DEFAULT NULL COMMENT '有价单证回销标记',
    arriveflag      VARCHAR(2)   DEFAULT NULL COMMENT '保单送达标记',
    applydate       DATE         DEFAULT NULL COMMENT '离职申请日期',
    branchtype2     VARCHAR(2)   DEFAULT NULL COMMENT '渠道',
    officeflag      VARCHAR(1)   DEFAULT NULL COMMENT '办公用品提交标志',
    handoverflag    VARCHAR(1)   DEFAULT NULL COMMENT '工作移交标记',
    activeflag      VARCHAR(1)   DEFAULT NULL COMMENT '活动手册提交标记',
    clientflag      VARCHAR(1)   DEFAULT NULL COMMENT '客户资料提交标记',
    userinvalidflag VARCHAR(1)   DEFAULT NULL COMMENT '取消用户标志',
    returnbookflag  VARCHAR(1)   DEFAULT NULL COMMENT '所借图书归还标志',
    informflag      VARCHAR(1)   DEFAULT NULL COMMENT '劳动合同解除通知发放标志',
    sincerityflag   VARCHAR(1)   DEFAULT NULL COMMENT '诚信记录录入完成标志',
    operator        VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate        DATE         DEFAULT NULL COMMENT '入机日期',
    maketime        VARCHAR(8)   DEFAULT NULL COMMENT '入机时间',
    modifydate      DATE         DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime      VARCHAR(8)   DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator  VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uq_ladimission (agentcode, departtimes)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管离职信息表';

-- ----------------------------
-- Table structure for lalinkassess-销管筹备期信息表
-- ----------------------------
DROP TABLE IF EXISTS lalinkassess;
CREATE TABLE IF NOT EXISTS lalinkassess
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '地区编码',
    agentgrade     VARCHAR(4)  NOT NULL COMMENT '代理人职级',
    areatype       VARCHAR(2)  NOT NULL COMMENT '版本号',
    arrangeperiod  DECIMAL(10, 2) DEFAULT NULL COMMENT '筹备期间',
    assessrate     DECIMAL(12, 2) DEFAULT NULL COMMENT '考核比率',
    assessrate1    DECIMAL(12, 2) DEFAULT NULL COMMENT '考核比率1',
    linkenddate    DATE           DEFAULT NULL COMMENT '衔接止期',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_index (managecom, agentgrade, areatype)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管筹备期信息表';

-- ----------------------------
-- Table structure for lavacationset-销管考勤假期信息表
-- ----------------------------
DROP TABLE IF EXISTS lavacationset;
CREATE TABLE IF NOT EXISTS lavacationset
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    managecom      VARCHAR(8)  NOT NULL COMMENT '管理机构',
    indexcalno     VARCHAR(8)  NOT NULL COMMENT '薪资年月',
    vacation       DATE        NOT NULL COMMENT '假期',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2    VARCHAR(2) DEFAULT NULL COMMENT '渠道',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE       DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8) DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE       DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8) DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_index (managecom, indexcalno, vacation, branchtype)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考勤假期信息表';


-- ----------------------------
-- Table structure for lanewpresenceb-销管考勤请假信息备份表
-- ----------------------------
DROP TABLE IF EXISTS lanewpresenceb;
CREATE TABLE IF NOT EXISTS lanewpresenceb
(
    id               bigint(20)  NOT NULL COMMENT '流水号',
    edorno           VARCHAR(10) NOT NULL COMMENT '转储号码',
    edortype         VARCHAR(2)     DEFAULT NULL COMMENT '备份类型',
    agentcode        VARCHAR(10) NOT NULL COMMENT '业务员编码',
    indexcalno       VARCHAR(8)  NOT NULL COMMENT '薪资年月',
    donedate         DATE        NOT NULL COMMENT '出勤日期',
    agentgroup       VARCHAR(12) NOT NULL COMMENT '销售机构编码',
    managecom        VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    doneflag         VARCHAR(1)  NOT NULL COMMENT '出勤情况',
    noti             VARCHAR(100)   DEFAULT NULL COMMENT '批注',
    branchtype       VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2      VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    state            VARCHAR(1)     DEFAULT NULL COMMENT '出勤状态',
    f1               VARCHAR(20)    DEFAULT NULL COMMENT 'F1',
    f2               VARCHAR(20)    DEFAULT NULL COMMENT 'F2',
    f3               VARCHAR(20)    DEFAULT NULL COMMENT 'F3',
    f4               DECIMAL(10, 2) DEFAULT NULL COMMENT 'F4',
    f5               DECIMAL(10, 2) DEFAULT NULL COMMENT 'F5',
    operator         VARCHAR(60) NOT NULL COMMENT '操作人员代码',
    makedate         DATE           DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME       DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (id),
    UNIQUE KEY unique_index (edorno, agentcode, indexcalno, donedate)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考勤请假信息备份表';



-- ----------------------------
-- Table structure for lanewpresence-销管考勤请假信息表
-- ----------------------------
DROP TABLE IF EXISTS lanewpresence;
CREATE TABLE IF NOT EXISTS lanewpresence
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    indexcalno     VARCHAR(8)  NOT NULL COMMENT '薪资年月',
    donedate       DATE        NOT NULL COMMENT '出勤日期',
    agentgroup     VARCHAR(12) NOT NULL COMMENT '销售机构编码',
    managecom      VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    doneflag       VARCHAR(1)  NOT NULL COMMENT '出勤情况',
    noti           VARCHAR(100)   DEFAULT NULL COMMENT '批注',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    state          VARCHAR(1)     DEFAULT NULL COMMENT '出勤状态',
    f1             VARCHAR(20)    DEFAULT NULL COMMENT 'F1',
    f2             VARCHAR(20)    DEFAULT NULL COMMENT 'F2',
    f3             VARCHAR(20)    DEFAULT NULL COMMENT 'F3',
    f4             DECIMAL(10, 2) DEFAULT NULL COMMENT 'F4',
    f5             DECIMAL(10, 2) DEFAULT NULL COMMENT 'F5',
    operator       VARCHAR(60) NOT NULL COMMENT '操作人员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (id),
    UNIQUE KEY unique_index (agentcode, indexcalno, donedate)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管考勤请假信息表';


-- ----------------------------
-- Table structure for laagentgrade-销管职级信息表
-- ----------------------------
DROP TABLE IF EXISTS laagentgrade;
CREATE TABLE IF NOT EXISTS laagentgrade
(
    gradecode       VARCHAR(6)  NOT NULL COMMENT '级别编码',
    gradeid         INT         NOT NULL COMMENT '级别id',
    gradename       VARCHAR(50) NOT NULL COMMENT '职级名称',
    branchtype      VARCHAR(2)  NOT NULL COMMENT '渠道类型',
    gradeproperty1  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性1',
    gradeproperty2  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性2',
    gradeproperty3  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性3',
    gradeproperty4  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性4',
    gradeproperty5  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性5',
    gradeproperty6  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性6',
    gradeproperty7  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性7',
    gradeproperty8  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性8',
    gradeproperty9  VARCHAR(1)   DEFAULT NULL COMMENT '级别属性9',
    gradeproperty10 VARCHAR(1)   DEFAULT NULL COMMENT '级别属性10',
    noti            VARCHAR(100) DEFAULT NULL COMMENT '备注',
    branchtype2     VARCHAR(2)   DEFAULT NULL COMMENT '子渠道类型',
    operator        VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate        DATE         DEFAULT NULL COMMENT '入机日期',
    maketime        VARCHAR(8)   DEFAULT NULL COMMENT '入机时间',
    modifydate      DATE         DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime      VARCHAR(8)   DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator  VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    PRIMARY KEY (gradecode)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管职级信息表';


-- ----------------------------
-- Table structure for lahols - 销管请假销假记录信息表
-- ----------------------------
DROP TABLE IF EXISTS lahols;
CREATE TABLE IF NOT EXISTS lahols
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    idx            int         NOT NULL COMMENT '纪录顺序号',
    agentgroup     VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom      VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    indexcalno     VARCHAR(8)     DEFAULT NULL COMMENT '指标计算编码',
    aclass         VARCHAR(2)     DEFAULT NULL COMMENT '请假类型',
    vacdays        int            DEFAULT NULL COMMENT '假期天数',
    leavedate      DATE        NOT NULL COMMENT '请假日期',
    enddate        DATE           DEFAULT NULL COMMENT '销假日期',
    absdays        VARCHAR(20)    DEFAULT NULL COMMENT '缺勤天数',
    fillflag       VARCHAR(1)     DEFAULT NULL COMMENT '请假单填写标志',
    confidenflag   VARCHAR(1)     DEFAULT NULL COMMENT '诊断证明书标志',
    addvacflag     VARCHAR(1)     DEFAULT NULL COMMENT '产假增加标志',
    summoney       decimal(12, 2) DEFAULT NULL COMMENT '金额',
    approvecode    VARCHAR(20)    DEFAULT NULL COMMENT '核准人',
    noti           VARCHAR(100)   DEFAULT NULL COMMENT '批注',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    shouldenddate  DATE           DEFAULT NULL COMMENT '应销假日期',
    leavestate     VARCHAR(1)     DEFAULT NULL COMMENT '请假状态',
    branchattr     VARCHAR(20)    DEFAULT NULL COMMENT '展业机构外部编码',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    primary key (id),
    unique key uq_agentcode_idx (agentcode, idx)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管请假销假记录信息表';

-- ----------------------------
-- Table structure for lahols2 - 销管请假销假记录信息表2
-- ----------------------------
DROP TABLE IF EXISTS lahols2;
CREATE TABLE IF NOT EXISTS lahols2
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '代理人编码',
    idx            int         NOT NULL COMMENT '纪录顺序号',
    agentgroup     VARCHAR(12) NOT NULL COMMENT '代理人展业机构代码',
    managecom      VARCHAR(8)     DEFAULT NULL COMMENT '管理机构',
    indexcalno     VARCHAR(8)     DEFAULT NULL COMMENT '指标计算编码',
    aclass         VARCHAR(2)     DEFAULT NULL COMMENT '请假类型',
    vacdays        int            DEFAULT NULL COMMENT '假期天数',
    leavedate      DATE        NOT NULL COMMENT '请假日期',
    enddate        DATE           DEFAULT NULL COMMENT '销假日期',
    absdays        VARCHAR(20)    DEFAULT NULL COMMENT '缺勤天数',
    fillflag       VARCHAR(1)     DEFAULT NULL COMMENT '请假单填写标志',
    confidenflag   VARCHAR(1)     DEFAULT NULL COMMENT '诊断证明书标志',
    addvacflag     VARCHAR(1)     DEFAULT NULL COMMENT '产假增加标志',
    summoney       decimal(12, 2) DEFAULT NULL COMMENT '金额',
    approvecode    VARCHAR(20)    DEFAULT NULL COMMENT '核准人',
    noti           VARCHAR(100)   DEFAULT NULL COMMENT '批注',
    branchtype     VARCHAR(2)  NOT NULL COMMENT '展业类型',
    shouldenddate  DATE           DEFAULT NULL COMMENT '应销假日期',
    leavestate     VARCHAR(1)     DEFAULT NULL COMMENT '请假状态',
    branchattr     VARCHAR(20)    DEFAULT NULL COMMENT '展业机构外部编码',
    branchtype2    VARCHAR(2)     DEFAULT NULL COMMENT '渠道',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE           DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    primary key (id),
    unique key uq_agentcode_idx (agentcode, idx)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管请假销假记录信息表2';

-- ----------------------------
-- Table structure for laqualification - 销管资格证信息表
-- ----------------------------
DROP TABLE IF EXISTS laqualification;
CREATE TABLE IF NOT EXISTS laqualification
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    qualifno       VARCHAR(30) NOT NULL COMMENT '资格证书号',
    idx            int         NOT NULL COMMENT '资格证类型',
    grantunit      VARCHAR(60) DEFAULT NULL COMMENT '批准单位',
    grantdate      DATE        DEFAULT NULL COMMENT '发放日期',
    invaliddate    DATE        DEFAULT NULL COMMENT '失效日期',
    invalidrsn     VARCHAR(60) DEFAULT NULL COMMENT '失效原因',
    reissuedate    DATE        DEFAULT NULL COMMENT '补发日期',
    reissuersn     VARCHAR(60) DEFAULT NULL COMMENT '补发原因',
    validperiod    int         DEFAULT NULL COMMENT '有效日期',
    state          VARCHAR(1)  DEFAULT NULL COMMENT '资格证书状态',
    pasexamdate    DATE        DEFAULT NULL COMMENT '通过考试日期',
    examyear       VARCHAR(8)  DEFAULT NULL COMMENT '考试年度',
    examtimes      VARCHAR(30) DEFAULT NULL COMMENT '考试次数',
    validstart     DATE        DEFAULT NULL COMMENT '有效起期',
    validend       DATE        DEFAULT NULL COMMENT '有效止期',
    oldqualifno    VARCHAR(30) DEFAULT NULL COMMENT '旧资格证号码',
    qualifname     VARCHAR(80) DEFAULT NULL COMMENT '资格证名称',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE        DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate     DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    primary key (id),
    unique key uq_agentcode_qualifno_idx (agentcode, qualifno, idx)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管资格证信息表';

CREATE TABLE laqualificationb
(
    edorno           varchar(20) NOT NULL PRIMARY KEY COMMENT '转储号',
    id               BIGINT(20)  NOT NULL COMMENT '流水号',
    edortype         VARCHAR(2)  NOT NULL COMMENT '转储类型',
    agentcode        VARCHAR(10) NOT NULL COMMENT '业务员编码',
    qualifno         VARCHAR(30) NOT NULL COMMENT '资格证书号',
    idx              INT         NOT NULL COMMENT '资格证类型',
    grantunit        VARCHAR(60) NULL COMMENT '批准单位',
    grantdate        DATE        NULL COMMENT '发放日期',
    invaliddate      DATE        NULL COMMENT '失效日期',
    invalidrsn       VARCHAR(60) NULL COMMENT '失效原因',
    reissuedate      DATE        NULL COMMENT '补发日期',
    reissuersn       VARCHAR(60) NULL COMMENT '补发原因',
    validperiod      INT         NULL COMMENT '有效日期',
    state            VARCHAR(1)  NULL COMMENT '资格证书状态',
    pasexamdate      DATE        NULL COMMENT '通过考试日期',
    examyear         VARCHAR(8)  NULL COMMENT '考试年度',
    examtimes        VARCHAR(30) NULL COMMENT '考试次数',
    validstart       DATE        NULL COMMENT '有效起期',
    validend         DATE        NULL COMMENT '有效止期',
    oldqualifno      VARCHAR(30) NULL COMMENT '旧资格证号码',
    qualifname       VARCHAR(80) NULL COMMENT '资格证名称',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE        NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  NULL COMMENT '入机时间',
    modifydate       DATE        NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) NULL COMMENT '备份人',
    lastmakedatetime DATETIME    NULL COMMENT '备份时间'
) COMMENT ='销管资格证信息备份表';

-- ----------------------------
-- Table structure for laqualificationtempb - 销管资格证申请信息备份表
-- ----------------------------
DROP TABLE IF EXISTS laqualificationtempb;
CREATE TABLE IF NOT EXISTS laqualificationtempb
(
    edorno           VARCHAR(20) NOT NULL COMMENT '转储号码',
    id               bigint(20)  NOT NULL COMMENT '流水号',
    agentcode        VARCHAR(10) NOT NULL COMMENT '业务员编码',
    qualifno         VARCHAR(30) NOT NULL COMMENT '资格证书号',
    idx              int         NOT NULL COMMENT '资格证类型',
    grantunit        VARCHAR(60) DEFAULT NULL COMMENT '批准单位',
    grantdate        DATE        DEFAULT NULL COMMENT '发放日期',
    invaliddate      DATE        DEFAULT NULL COMMENT '失效日期',
    invalidrsn       VARCHAR(60) DEFAULT NULL COMMENT '失效原因',
    reissuedate      DATE        DEFAULT NULL COMMENT '补发日期',
    reissuersn       VARCHAR(60) DEFAULT NULL COMMENT '补发原因',
    validperiod      int         DEFAULT NULL COMMENT '有效日期',
    state            VARCHAR(1)  DEFAULT NULL COMMENT '资格证书状态',
    pasexamdate      DATE        DEFAULT NULL COMMENT '通过考试日期',
    examyear         VARCHAR(8)  DEFAULT NULL COMMENT '考试年度',
    examtimes        VARCHAR(30) DEFAULT NULL COMMENT '考试次数',
    edordate         DATE        DEFAULT NULL COMMENT '转储日期',
    validstart       DATE        DEFAULT NULL COMMENT '有效起期',
    validend         DATE        DEFAULT NULL COMMENT '有效止期',
    oldqualifno      VARCHAR(30) DEFAULT NULL COMMENT '旧资格证号码',
    qualifname       VARCHAR(40) DEFAULT NULL COMMENT '资格证名称',
    edortype         VARCHAR(2)  DEFAULT NULL COMMENT '备份类型',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate         DATE        DEFAULT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifydate       DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME    DEFAULT NULL COMMENT '备份时间',
    primary key (edorno)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管资格证申请信息备份表';

-- ----------------------------
-- Table structure for laqualificationtemp - 销管资格证申请信息表
-- ----------------------------
DROP TABLE IF EXISTS laqualificationtemp;
CREATE TABLE IF NOT EXISTS laqualificationtemp
(
    id             bigint(20)  NOT NULL COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编号',
    qualifno       VARCHAR(30) NOT NULL COMMENT '资格证书号',
    idx            int         NOT NULL COMMENT '资格证类型',
    grantunit      VARCHAR(60) DEFAULT NULL COMMENT '批准单位',
    grantdate      DATE        DEFAULT NULL COMMENT '发放日期',
    invaliddate    DATE        DEFAULT NULL COMMENT '失效日期',
    invalidrsn     VARCHAR(60) DEFAULT NULL COMMENT '失效原因',
    reissuedate    DATE        DEFAULT NULL COMMENT '补发日期',
    reissuersn     VARCHAR(60) DEFAULT NULL COMMENT '补发原因',
    validperiod    int         DEFAULT NULL COMMENT '有效日期',
    state          VARCHAR(1)  DEFAULT NULL COMMENT '资格证书状态',
    pasexamdate    DATE        DEFAULT NULL COMMENT '通过考试日期',
    examyear       VARCHAR(8)  DEFAULT NULL COMMENT '考试年度',
    examtimes      VARCHAR(30) DEFAULT NULL COMMENT '考试次数',
    validstart     DATE        DEFAULT NULL COMMENT '有效起期',
    validend       DATE        DEFAULT NULL COMMENT '有效止期',
    oldqualifno    VARCHAR(30) DEFAULT NULL COMMENT '旧资格证号码',
    qualifname     VARCHAR(40) DEFAULT NULL COMMENT '资格证名称',
    operator       VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate       DATE        DEFAULT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)  DEFAULT NULL COMMENT '入机时间',
    modifydate     DATE        DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)  DEFAULT NULL COMMENT '最后一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    primary key (id),
    unique key uq_agentcode_qualifno_idx (agentcode, qualifno, idx)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管资格证申请信息表';

DROP TABLE IF EXISTS lahealthinsauthinfo;
CREATE TABLE IF NOT EXISTS lahealthinsauthinfo
(
    id             BIGINT PRIMARY KEY COMMENT '流水号',
    agentcode      VARCHAR(10) NOT NULL COMMENT '业务员编码',
    authstatus     CHAR(1)     NOT NULL COMMENT '授权状态 (Y/N)',
    authstartdate  DATE        NULL COMMENT '授权开始日期',
    authenddate    DATE        NULL COMMENT '授权结束日期',
    authoutreason  VARCHAR(100) COMMENT '授权断开原因',
    operator       VARCHAR(60) COMMENT '操作员',
    makedate       DATE COMMENT '入机日期',
    maketime       VARCHAR(8) COMMENT '入机时间',
    modifydate     DATE COMMENT '最近一次修改日期',
    modifytime     VARCHAR(8) COMMENT '最近一次修改时间',
    modifyoperator VARCHAR(60) NOT NULL COMMENT '最后一次修改人'
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='健康险人员授权信息表';

DROP TABLE IF EXISTS lahealthinsauthinfob;
CREATE TABLE IF NOT EXISTS lahealthinsauthinfob
(
    edorno           varchar(20) NOT NULL PRIMARY KEY COMMENT '转储号',
    id               BIGINT(20)  NOT NULL COMMENT '流水号',
    edortype         VARCHAR(2)  NOT NULL COMMENT '转储类型',
    agentcode        VARCHAR(10) NOT NULL COMMENT '业务员编码',
    authstatus       CHAR(1)     NOT NULL COMMENT '授权状态 (Y/N)',
    authstartdate    DATE        NULL COMMENT '授权开始日期',
    authenddate      DATE        NULL COMMENT '授权结束日期',
    authoutreason    VARCHAR(100) COMMENT '授权断开原因',
    operator         VARCHAR(60) COMMENT '操作员',
    makedate         DATE COMMENT '入机日期',
    maketime         VARCHAR(8) COMMENT '入机时间',
    modifydate       DATE COMMENT '最近一次修改日期',
    modifytime       VARCHAR(8) COMMENT '最近一次修改时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    lastoperator     VARCHAR(60) NULL COMMENT '备份人',
    lastmakedatetime DATETIME    NULL COMMENT '备份时间'
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='健康险人员授权信息备份表';

DROP TABLE IF EXISTS lasmudgefoundationinfo;
create table lasmudgefoundationinfo
(
    agentcode      varchar(20)   not null comment '工号'
        primary key,
    processingdate date          null comment '处理日期',
    violationfact  varchar(2000) null comment '违规事件',
    noti           varchar(2000) null comment '注释',
    branchtype     varchar(5)    null comment '渠道',
    branchtype2    varchar(5)    null comment '子渠道',
    s1             varchar(100)  null,
    s2             varchar(100)  null,
    s3             varchar(100)  null,
    operator       varchar(60)   not null comment '操作员',
    makedate       date          null comment '入机日期',
    maketime       varchar(8)    null comment '入机时间',
    modifydate     date          null comment '最后一次修改日期',
    modifytime     varchar(8)    null comment '最后一次修改时间',
    modifyoperator varchar(60)   not null comment '最后一次修改人'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '污点信息';

DROP TABLE IF EXISTS lasmudgedetailinfo;
create table lasmudgedetailinfo
(
    id               varchar(20)   not null
        primary key,
    agentcode        varchar(20)   null comment '工号',
    behaviorcategory varchar(2)    null comment '行为类别',
    behaviorentry    varchar(3)    null comment '行为条目',
    cause            varchar(4000) null comment '原因',
    penaltyvalue     bigint        null comment '罚值',
    branchtype       varchar(5)    null comment '渠道',
    branchtype2      varchar(5)    null comment '子渠道',
    operator         varchar(60)   not null comment '操作员',
    makedate         date          null comment '入机日期',
    maketime         varchar(8)    null comment '入机时间',
    modifydate       date          null comment '最后一次修改日期',
    modifytime       varchar(8)    null comment '最后一次修改时间',
    modifyoperator   varchar(60)   not null comment '最后一次修改人'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '污点细节表';

DROP TABLE IF EXISTS lasmudgefoundationinfob;
create table lasmudgefoundationinfob
(
    edorno           bigint        not null comment '流水号'
        primary key,
    edortype         varchar(2)    not null comment '转储类型',
    agentcode        varchar(20)   not null comment '工号',
    processingdate   date          null comment '处理日期',
    violationfact    varchar(2000) null comment '违规事件',
    noti             varchar(2000) null comment '注释',
    branchtype       varchar(5)    null comment '渠道',
    branchtype2      varchar(5)    null comment '子渠道',
    s1               varchar(100)  null,
    s2               varchar(100)  null,
    s3               varchar(100)  null,
    operator         varchar(60)   not null comment '操作员',
    makedate         date          null comment '入机日期',
    maketime         varchar(8)    null comment '入机时间',
    modifydate       date          null comment '最后一次修改日期',
    modifytime       varchar(8)    null comment '最后一次修改时间',
    modifyoperator   varchar(60)   not null comment '最后一次修改人',
    lastoperator     varchar(60)   null comment '备份人',
    lastmakedatetime datetime      null comment '备份时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '污点信息备份表';

DROP TABLE IF EXISTS lasmudgedetailinfob;
create table lasmudgedetailinfob
(
    edorno           bigint        not null comment '流水号'
        primary key,
    edortype         varchar(2)    not null comment '转储类型',
    id               varchar(20)   not null,
    agentcode        varchar(20)   null comment '工号',
    behaviorcategory varchar(2)    null comment '行为类别',
    behaviorentry    varchar(3)    null comment '行为条目',
    cause            varchar(4000) null comment '原因',
    penaltyvalue     bigint        null comment '罚值',
    branchtype       varchar(5)    null comment '渠道',
    branchtype2      varchar(5)    null comment '子渠道',
    operator         varchar(60)   not null comment '操作员',
    makedate         date          null comment '入机日期',
    maketime         varchar(8)    null comment '入机时间',
    modifydate       date          null comment '最后一次修改日期',
    modifytime       varchar(8)    null comment '最后一次修改时间',
    modifyoperator   varchar(60)   not null comment '最后一次修改人',
    lastoperator     varchar(60)   null comment '备份人',
    lastmakedatetime datetime      null comment '备份时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '污点细节备份表';

DROP TABLE IF EXISTS agentmovechange;
create table agentmovechange
(
    id               bigint auto_increment comment '主键' primary key,
    agentcode        varchar(10)             not null comment '工号',
    branchtype       varchar(2)              not null comment '渠道',
    oldmanagecom     varchar(20)  default '' null comment '旧管理机构',
    newmanagecom     varchar(20)  default '' null comment '新管理机构',
    oldagentgroup    varchar(20)  default '' null comment '旧销售机构',
    newagentgroup    varchar(20)  default '' null comment '新销售机构',
    oldbranchcode    varchar(20)  default '' null comment '旧机构码',
    newbranchcode    varchar(20)  default '' null comment '新机构码',
    oldbranchmanager varchar(20)  default '' null comment '旧主管',
    newbranchmanager varchar(20)  default '' null comment '新主管',
    adjustdate       date                    not null comment '调整日期',
    effectdate       date                    null comment '生效日期',
    reason           varchar(255) default '' null,
    remarks          varchar(255) default '' null,
    f01              varchar(100) default '' null comment '备用字段1',
    f02              varchar(100) default '' null comment '备用字段2',
    f03              varchar(100) default '' null comment '备用字段3',
    f04              decimal(12)  default 0  null comment '备用字段4',
    f05              int          default 0  null comment '备用字段5',
    operator         varchar(60)             not null comment '操作员',
    makedate         date                    null comment '入机日期',
    maketime         varchar(8)              null comment '入机时间',
    modifyoperator   varchar(60)             not null comment '最后一次修改人',
    modifydate       date                    null comment '最后一次修改日期',
    modifytime       varchar(8)              null comment '最后一次修改时间',
    KEY `idx_agentmovechange_index1` (`branchtype`),
    KEY `idx_agentmovechange_index2` (`agentcode`),
    KEY `idx_agentmovechange_index3` (`oldmanagecom`),
    KEY `idx_agentmovechange_index4` (`oldagentgroup`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '人员异动表';

DROP TABLE IF EXISTS agentmovechangeb;
create table agentmovechangeb
(
    edorno           VARCHAR(20)             NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2)   DEFAULT NULL COMMENT '备份类型',
    id               bigint comment '主键',
    agentcode        varchar(10)             not null comment '工号',
    branchtype       varchar(2)              not null comment '渠道',
    oldmanagecom     varchar(20)  default '' null comment '旧管理机构',
    newmanagecom     varchar(20)  default '' null comment '新管理机构',
    oldagentgroup    varchar(20)  default '' null comment '旧销售机构',
    newagentgroup    varchar(20)  default '' null comment '新销售机构',
    oldbranchcode    varchar(20)  default '' null comment '旧机构码',
    newbranchcode    varchar(20)  default '' null comment '新机构码',
    oldbranchmanager varchar(20)  default '' null comment '旧主管',
    newbranchmanager varchar(20)  default '' null comment '新主管',
    adjustdate       date                    not null comment '调整日期',
    effectdate       date                    null comment '生效日期',
    reason           varchar(255) default '' null,
    remarks          varchar(255) default '' null,
    f01              varchar(100) default '' null comment '备用字段1',
    f02              varchar(100) default '' null comment '备用字段2',
    f03              varchar(100) default '' null comment '备用字段3',
    f04              decimal(12)  default 0  null comment '备用字段4',
    f05              int          default 0  null comment '备用字段5',
    operator         varchar(60)             not null comment '操作员',
    makedate         date                    null comment '入机日期',
    maketime         varchar(8)              null comment '入机时间',
    modifyoperator   varchar(60)             not null comment '最后一次修改人',
    modifydate       date                    null comment '最后一次修改日期',
    modifytime       varchar(8)              null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME     DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '人员异动备份表';

DROP TABLE IF EXISTS larankadjust;
create table larankadjust
(
    id               bigint auto_increment comment '主键' primary key,
    agentcode        varchar(10)                 not null comment '工号',
    branchtype       varchar(1)                  null comment '渠道',
    branchtype2      varchar(2)                  null comment '子渠道',
    oldmanagecom     varchar(20)  default ''     null comment '旧管理机构',
    newmanagecom     varchar(20)  default ''     null comment '新管理机构',
    oldagentgroup    varchar(20)  default ''     null comment '旧销售机构',
    newagentgroup    varchar(20)  default ''     null comment '新销售机构',
    oldbranchcode    varchar(20)  default ''     null comment '旧机构码',
    newbranchcode    varchar(20)  default ''     null comment '新机构码',
    oldbranchmanager varchar(20)  default ''     null comment '旧主管',
    newbranchmanager varchar(20)  default ''     null comment '新主管',
    oldgrade         varchar(20)  default ''     null comment '旧职级',
    newgrade         varchar(20)  default ''     null comment '新职级',
    adjustdate       date                        not null comment '调整日期',
    effectdate       date                        null comment '生效日期',
    reason           varchar(255) default '原因' null,
    remarks          varchar(255) default '备注' null,
    f01              varchar(100) default ''     null comment '备用字段1',
    f02              varchar(100) default ''     null comment '备用字段2',
    f03              date                        null comment '备用字段2',
    f04              decimal(12)  default 0      null comment '备用字段2',
    f05              varchar(50)                 null comment '备用字段2',
    f06              varchar(50)                 null comment '备用字段2',
    operator         varchar(60)                 not null comment '操作员',
    makedate         date                        null comment '入机日期',
    maketime         varchar(8)                  null comment '入机时间',
    modifyoperator   varchar(60)                 not null comment '最后一次修改人',
    modifydate       date                        null comment '最后一次修改日期',
    modifytime       varchar(8)                  null comment '最后一次修改时间',
    KEY `idx_larankadjust_index1` (`branchtype`),
    KEY `idx_larankadjust_index2` (`agentcode`),
    KEY `idx_larankadjust_index3` (`oldmanagecom`),
    KEY `idx_larankadjust_index4` (`oldagentgroup`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '人员职级调整表';


DROP TABLE IF EXISTS larankadjustb;
create table larankadjustb
(
    edorno           VARCHAR(20)                 NOT NULL COMMENT '批改号',
    edortype         VARCHAR(2)   DEFAULT NULL COMMENT '备份类型',
    id               bigint comment '主键',
    agentcode        varchar(10)                 not null comment '工号',
    branchtype       varchar(1)                  null comment '渠道',
    branchtype2      varchar(2)                  null comment '子渠道',
    oldmanagecom     varchar(20)  default ''     null comment '旧管理机构',
    newmanagecom     varchar(20)  default ''     null comment '新管理机构',
    oldagentgroup    varchar(20)  default ''     null comment '旧销售机构',
    newagentgroup    varchar(20)  default ''     null comment '新销售机构',
    oldbranchcode    varchar(20)  default ''     null comment '旧机构码',
    newbranchcode    varchar(20)  default ''     null comment '新机构码',
    oldbranchmanager varchar(20)  default ''     null comment '旧主管',
    newbranchmanager varchar(20)  default ''     null comment '新主管',
    oldgrade         varchar(20)  default ''     null comment '旧职级',
    newgrade         varchar(20)  default ''     null comment '新职级',
    adjustdate       date                        not null comment '调整日期',
    effectdate       date                        null comment '生效日期',
    reason           varchar(255) default '原因' null,
    remarks          varchar(255) default '备注' null,
    f01              varchar(100) default ''     null comment '备用字段1',
    f02              varchar(100) default ''     null comment '备用字段2',
    f03              date                        null comment '备用字段2',
    f04              decimal(12)  default 0      null comment '备用字段2',
    f05              varchar(50)                 null comment '备用字段2',
    f06              varchar(50)                 null comment '备用字段2',
    operator         varchar(60)                 not null comment '操作员',
    makedate         date                        null comment '入机日期',
    maketime         varchar(8)                  null comment '入机时间',
    modifyoperator   varchar(60)                 not null comment '最后一次修改人',
    modifydate       date                        null comment '最后一次修改日期',
    modifytime       varchar(8)                  null comment '最后一次修改时间',
    lastoperator     VARCHAR(60) COMMENT '备份人',
    lastmakedatetime DATETIME     DEFAULT NULL COMMENT '备份时间',
    PRIMARY KEY (edorno)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci COMMENT = '人员职级调整备份表';

DROP TABLE IF EXISTS Latrainregister;
create table Latrainregister
(
    id             bigint      not null
        primary key,
    credentialno   varchar(50) null comment '证件号码',
    name           varchar(10) null comment '姓名',
    managecom      varchar(20) null comment '管理机构',
    traintype      varchar(10) null comment '培训类型',
    traincourses   varchar(30) null comment '培训课程',
    traincomcode   varchar(50) null comment '培训机构',
    trainmethod    char(2)     null comment '培训方式',
    traindate      date        null comment '培训日期',
    trainstartdate date        null comment '培训开始时间',
    trainenddate   date        null comment '培训结束时间',
    traincategory  char(2)     null comment '培训类别',
    startdate      date        null,
    enddate        date        null,
    branchtype     char(2)     null comment '渠道',
    spare1         varchar(50) null,
    spare2         varchar(50) null,
    spare3         varchar(50) null,
    operator       varchar(60) not null comment '操作员',
    makedate       date        not null comment '入机日期',
    maketime       varchar(8)  not null comment '入机时间',
    modifydate     date        not null comment '最后一次修改日期',
    modifytime     varchar(8)  not null comment '最后一次修改时间',
    modifyoperator varchar(20) not null comment '最后一次修改人'
)
    comment '培训信息表';

DROP TABLE IF EXISTS Latrainregisterb;
create table Latrainregisterb
(
    edorno           bigint      not null comment '流水号'
        primary key,
    edortype         varchar(2)  not null comment '转储类型',
    id               bigint      not null,
    credentialno     varchar(50) null comment '证件号码',
    name             varchar(10) null comment '姓名',
    managecom        varchar(20) null comment '管理机构',
    traintype        varchar(10) null comment '培训类型',
    traincourses     varchar(30) null comment '培训课程',
    traincomcode     varchar(50) null comment '培训机构',
    trainmethod      char(2)     null comment '培训方式',
    traindate        date        null comment '培训日期',
    trainstartdate   date        null comment '培训开始时间',
    trainenddate     date        null comment '培训结束时间',
    traincategory    char(2)     null comment '培训类别',
    startdate        date        null,
    enddate          date        null,
    branchtype       char(2)     null comment '渠道',
    spare1           varchar(50) null,
    spare2           varchar(50) null,
    spare3           varchar(50) null,
    operator         varchar(60) not null comment '操作员',
    makedate         date        not null comment '入机日期',
    maketime         varchar(8)  not null comment '入机时间',
    modifydate       date        not null comment '最后一次修改日期',
    modifytime       varchar(8)  not null comment '最后一次修改时间',
    modifyoperator   varchar(20) not null comment '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime    null comment '备份时间'
)
    comment '培训信息备份表';

DROP TABLE IF EXISTS larewardinfo;
create table larewardinfo
(
    id                 bigint        not null
        primary key,
    agentcode          varchar(20)   null comment '工号',
    incentivelevel     varchar(2)    null comment '奖励等级',
    recognitiondate    date          null comment '表彰时间',
    recognition        varchar(600)  null comment '表彰单位',
    incentivetype      varchar(2)    null comment '奖励类别',
    recognitioncontent varchar(4000) null comment '奖励内容',
    incentivenoti      varchar(4000) null comment '奖励说明',
    branchtype         varchar(5)    null comment '渠道',
    branchtype2        varchar(5)    null comment '子渠道',
    operator           varchar(60)   not null comment '操作员',
    makedate           date          not null comment '入机日期',
    maketime           varchar(8)    not null comment '入机时间',
    modifydate         date          not null comment '最后一次修改日期',
    modifytime         varchar(8)    not null comment '最后一次修改时间',
    modifyoperator     varchar(60)   not null comment '最后一次修改人'
)
    comment '奖励信息';

DROP TABLE IF EXISTS larewardinfob;
create table larewardinfob
(
    edorno             bigint        not null comment '流水号'
        primary key,
    edortype           varchar(2)    not null comment '转储类型',
    id                 bigint        not null,
    agentcode          varchar(20)   null comment '工号',
    incentivelevel     varchar(2)    null comment '奖励等级',
    recognitiondate    date          null comment '表彰时间',
    recognition        varchar(600)  null comment '表彰单位',
    incentivetype      varchar(2)    null comment '奖励类别',
    recognitioncontent varchar(4000) null comment '奖励内容',
    incentivenoti      varchar(4000) null comment '奖励说明',
    branchtype         varchar(5)    null comment '渠道',
    branchtype2        varchar(5)    null comment '子渠道',
    operator           varchar(60)   not null comment '操作员',
    makedate           date          not null comment '入机日期',
    maketime           varchar(8)    not null comment '入机时间',
    modifydate         date          not null comment '最后一次修改日期',
    modifytime         varchar(8)    not null comment '最后一次修改时间',
    modifyoperator     varchar(60)   not null comment '最后一次修改人',
    lastoperator       varchar(60)   null comment '备份人',
    lastmakedatetime   datetime      null comment '备份时间'
)
    comment '奖励信息备份表';

