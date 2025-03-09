
drop table if exists base_law_version;
create TABLE `base_law_version`
(
    `id`              bigint    NOT NULL AUTO_INCREMENT comment '主键',
    name              varchar(100) null comment '基本说名称',
    `branch_type`     varchar(2)   DEFAULT NULL comment '渠道',
    `index_cal_type`  varchar(10)  DEFAULT NULL comment '佣金类型',
    `status`          tinyint(4)   DEFAULT NULL comment '启用状态',
    `calculate_class` varchar(200) DEFAULT NULL comment '计算人员实现类',
    `handle_queue`    varchar(100) DEFAULT NULL comment '计算完成处理队列',
    remark            varchar(255) null comment '说明',
    operator       VARCHAR(60)   NOT NULL COMMENT '操作员',
    makedate       DATE          NOT NULL COMMENT '入机日期',
    maketime       VARCHAR(8)    NOT NULL COMMENT '入机时间',
    modifyoperator VARCHAR(60)   NOT NULL COMMENT '最后一次修改人',
    modifydate     DATE          NOT NULL COMMENT '最后一次修改日期',
    modifytime     VARCHAR(8)    NOT NULL COMMENT '最后一次修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='基本法版本表';

drop table if exists wage_cal_elements_config;
create table wage_cal_elements_config
(
    series_no    int(10)       not null comment '序列号' primary key,
    base_law_id  int(8)        null comment '版本号',
    cal_elements varchar(30) not null comment '计算元素id',
    row_index    int(2)        null comment '存储序号',
    title        varchar(50) null comment '标题',
    col_width    int(8)        null comment '列宽度',
    data_type    int(2)        null comment '0 字符串 1数字 2比例',
    elem_order   decimal(4, 2) null comment '排序字段'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金计算过程定义配置表';


drop table if exists wage_cal_grade_relation;
create table wage_cal_grade_relation
(
    series_no   int(10)     not null comment '序列号' primary key,
    base_law_id int(8)      null comment '版本号',
    manage_com  varchar(10) null comment '管理机构',
    branch_type varchar(2) null comment '渠道',
    agent_grade varchar(8) null comment '职级',
    cal_code    varchar(10) null comment '计算代码',
    cal_flag    int(1)      null comment '计算标记 1计算 0不计算 0优化级高'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金与职级关系表';

drop table if exists wage_calculation_definition;
create table wage_calculation_definition
(
    series_no         int(10)       not null comment '序列号' primary key,
    base_law_id       int(8)        null comment '版本号',
    index_cal_type    varchar(3)   not null comment '计算类型 indexcaltypeenum',
    cal_code          varchar(10)  not null comment '计算代码',
    cal_name          varchar(50) null comment '工资项说明',
    table_name        varchar(30)  not null comment '工资结果表',
    table_colname     varchar(10)  not null comment '工资结果列',
    branch_type       varchar(2)   not null comment '渠道 branchtypeenum',
    wagecal_step      int(3)        default 0 comment '计算方式 步长',
    cal_group_name    varchar(200) null comment '计算分组名称 多个佣金项 保存一个过程id 工资单使用该名称展示title',
    cal_process_elem  varchar(200) null comment '计算过程存储元素 ,号分割多个过程id',
    data_type         int(2)        not null comment '0 字符串 1数字 2比例',
    wage_type         varchar(2)   not null comment '佣金类型 0佣金计算 1合并计税 wagetypeenum',
    cal_period        varchar(2)   not null comment '计算频率',
    cal_elements      varchar(50)  not null comment '特殊频率参数',
    cal_order         decimal(4, 2) null comment ' 排序字段',
    out_excel         int(1)        null comment '是否输出excel 工资单展示',
    out_order         decimal(4, 2) null comment ' 工资单排序字段',
    handler_class     varchar(100) not null comment '计算处理类',
    construction_parm varchar(80) null comment '实现类的构造参数,多个参数用,号分割,string类型参数',
    remark            varchar(255) null comment '规则说明'
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='佣金明细定义表';



drop table if exists lawageindexinfo;
create table lawageindexinfo
(
    aseriesno    bigint(20)   not null comment '主键' primary key,
    agentcode    varchar(20)  not null comment '业务员工号',
    indexcalno   varchar(10)  not null comment '佣金年月',
    calelements  varchar(30)  not null comment '计算过程类型 wagecalelementsenum结果',
    indexcaltype varchar(3)   not null comment '计算类型 indexcaltypeenum',
    calid        varchar(50)  not null comment '数据关联id',
    rowindex     int(6)       not null comment '序号',
    n1           varchar(100) not null comment '结果1',
    n2           varchar(100) null comment '结果2',
    n3           varchar(100) null comment '结果3',
    n4           varchar(100) null comment '结果4',
    n5           varchar(100) null comment '结果5'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金计算过程表数据';


drop table if exists la_rate_commision_config;
create table la_rate_commision_config
(
    id                int(20)       not null comment '序列号' primary key,
    code              varchar(50) null default '' comment '代码',
    branch_type       VARCHAR(2)  not null comment '渠道',
    query_fields      VARCHAR(3000) default '' null comment '查询字段',
    cal_type          int null comment '计算类型 0-费率匹配逻辑 1-自定义匹配逻辑',
    cal_handler       VARCHAR(50) null default '' comment '计算实现',
    data_source_table VARCHAR(50) null default '' comment '数据源计算表',
    sort              DECIMAL(4, 2) null default 0 comment '排序',
    remark            varchar(50) null default '' comment '说明',
    operator          VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate          DATE        NOT NULL COMMENT '入机日期',
    maketime          VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator    VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate        DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime        VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金费率配置表';

drop table if exists la_rate_commision_config_item;
create table la_rate_commision_config_item
(
    id               int(20)       not null comment '序列号' primary key,
    config_id        bigint(20)    not null comment '配置id',
    code             varchar(50) null     default '' comment '分组代码',
    title            varchar(100) null     default '' comment '标题',
    field            varchar(50) null     default '' comment '字段',
    field_condition  varchar(50) null     default '' comment '配置校验条件',
    field_required   bigint(1)     null     default 0 comment '字段必录',
    data_field       varchar(50) null     default '' comment '数据匹配字段',
    data_condition   varchar(50) null     default '' comment '数据匹配条件',
    weight           bigint(6)     not null default 1 comment '字段权重 质数权重不含2 避免出现重叠情况 可以从第2000位质数开始 然后1990位质数 开始递减 ',
    weight_handler   varchar(200) null     default '' comment '权重参数处理类',
    data_type        varchar(2) null     default '' comment '字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期',
    fill_sm          varchar(50) null     default '' comment '填写说明',
    width            int(16)       not null default 5000 comment '列宽',
    down_sel_handler varchar(50) null     default '' comment '下拉处理类',
    parameter        VARCHAR(50) default '' null comment '下拉处理类参数',
    cal_sort         DECIMAL(4, 2) null     default 1 comment '计算排序',
    sort             DECIMAL(4, 2) null     default 1 comment '显示排序',
    remark           varchar(50) null     default '' comment '备注',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate         DATE        NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate       DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金费率配置明细表';

drop table if exists la_rate_commision;
create table la_rate_commision
(
    seriesno         bigint(20)   not null primary key,
    configid         bigint(20)   not null comment '配置主键',
    managecom        VARCHAR(10) NOT NULL default '' comment '管理机构',
    agentcom         VARCHAR(20) comment '代理机构',
    branchtype       VARCHAR(2)  not null comment '渠道',
    branchtype2      VARCHAR(2) comment '子渠道',
    riskcode         VARCHAR(10) not null comment '险种',
    startdate        date        not null comment '有效起期',
    enddate          date        not null comment '有效止期',
    jjfs_jflx        VARCHAR(2) null     default '' comment '缴费类型',
    jjfs_jfjg        VARCHAR(2) null     default '' comment '缴费间隔',
    jjfs_jfqj_down   VARCHAR(5) null     default '' comment '缴费期间下限',
    jjfs_jfqj_up     VARCHAR(5) null     default '' comment '缴费期间上限',
    bzfs_bzlx        VARCHAR(2) null     default '' comment '保障类型',
    bzfs_bzqj_down   VARCHAR(5) null     default '' comment '保障期间下限',
    bzfs_bzqj_up     VARCHAR(5) null     default '' comment '保障期间上限',
    pay_count_down   VARCHAR(5) null     default '' comment '保单年度下限',
    pay_count_up     VARCHAR(5) null     default '' comment '保单年度上限',
    amnt_down        VARCHAR(18) null     default '' comment '保额下限',
    amnt_up          VARCHAR(18) null     default '' comment '保额上限',
    trans_money_down VARCHAR(18) null     default '' comment '保费下限',
    trans_money_up   VARCHAR(18) null     default '' comment '保费上限',
    plan_code        VARCHAR(18) null     default '' comment '方案代码',
    plan_name        VARCHAR(200) null     default '' comment '方案名称',
    f01              VARCHAR(20) null     default '' comment '备用字段一',
    f01_down         VARCHAR(50) null     default '' comment '备用字段一下限',
    f01_up           VARCHAR(50) null     default '' comment '备用字段一上限',
    f02              VARCHAR(20) null     default '' comment '备用字段二',
    f02_down         VARCHAR(50) null     default '' comment '备用字段二下限',
    f02_up           VARCHAR(50) null     default '' comment '备用字段二上限',
    f03              VARCHAR(20) null     default '' comment '备用字段三',
    f03_down         VARCHAR(50) null     default '' comment '备用字段三下限',
    f03_up           VARCHAR(50) null     default '' comment '备用字段三上限',
    f04              VARCHAR(20) null     default '' comment '备用字段四',
    f04_down         VARCHAR(50) null     default '' comment '备用字段四下限',
    f04_up           VARCHAR(50) null     default '' comment '备用字段四上限',
    f05              VARCHAR(20) null     default '' comment '备用字段五',
    f05_down         VARCHAR(50) null     default '' comment '备用字段五下限',
    f05_up           VARCHAR(50) null     default '' comment '备用字段五上限',
    f06              VARCHAR(20) null     default '' comment '备用字段六',
    f06_down         VARCHAR(50) null     default '' comment '备用字段六下限',
    f06_up           VARCHAR(50) null     default '' comment '备用字段六上限',
    f07              VARCHAR(20) null     default '' comment '备用字段七',
    f07_down         VARCHAR(50) null     default '' comment '备用字段七下限',
    f07_up           VARCHAR(50) null     default '' comment '备用字段七上限',
    f08              VARCHAR(20) null     default '' comment '备用字段八',
    f08_down         VARCHAR(50) null     default '' comment '备用字段八下限',
    f08_up           VARCHAR(50) null     default '' comment '备用字段八上限',
    f09              VARCHAR(50) null     default '' comment '备用字段九',
    f010             VARCHAR(50) null     default '' comment '备用字段十',
    f011             VARCHAR(50) null     default '' comment '备用字段十一',
    f012             VARCHAR(50) null     default '' comment '备用字段十二',
    f013             VARCHAR(50) null     default '' comment '备用字段十三',
    f014             VARCHAR(50) null     default '' comment '备用字段十四',
    f015             VARCHAR(50) null     default '' comment '备用字段十五',
    f016             VARCHAR(50) null     default '' comment '备用字段十六',
    f017             VARCHAR(50) null     default '' comment '备用字段十七',
    f018             VARCHAR(50) null     default '' comment '备用字段十八',
    f019             VARCHAR(50) null     default '' comment '备用字段十九',
    f020             VARCHAR(50) null     default '' comment '备用字段二十',
    weight           bigint(6)    not null default 1 comment '配置权重',
    remark           VARCHAR(500) null     default '' default '' comment '备注',
    rate             VARCHAR(20) not null default '' comment '费率',
    rate2            VARCHAR(20)          default '' comment '费率2',
    rate3            VARCHAR(20)          default '' comment '费率3',
    rate4            VARCHAR(20)          default '' comment '费率4',
    rate5            VARCHAR(20)          default '' comment '费率5',
    rate6            VARCHAR(20)          default '' comment '费率6',
    rate7            VARCHAR(20)          default '' comment '费率7',
    rate8            VARCHAR(20)          default '' comment '费率8',
    operator         VARCHAR(60) NOT NULL COMMENT '操作员',
    makedate         DATE        NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)  NOT NULL COMMENT '入机时间',
    modifyoperator   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate       DATE        NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    KEY              `la_rate_commision_index1` (`riskcode`),
    KEY              `la_rate_commision_index2` (`riskcode`, `managecom`, `agentcom`),
    KEY              `la_rate_commision_index3` (`riskcode`, `startdate`, `enddate`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金费率表';


drop table if exists la_rate_commision_b;
create table la_rate_commision_b
(
    edor_no          varchar(40) primary key not null comment '流水号',
    edor_type        varchar(2)              not null comment '操作类型',
    seriesno         bigint(20)              not null,
    configid         bigint(20)              not null comment '配置主键',
    managecom        VARCHAR(10)             NOT NULL default '' comment '管理机构',
    agentcom         VARCHAR(20) comment '代理机构',
    branchtype       VARCHAR(2)              not null comment '渠道',
    branchtype2      VARCHAR(2) comment '子渠道',
    riskcode         VARCHAR(10)             not null comment '险种',
    startdate        date                    not null comment '有效起期',
    enddate          date                    not null comment '有效止期',
    jjfs_jflx        VARCHAR(2) null     default '' comment '缴费类型',
    jjfs_jfjg        VARCHAR(2) null     default '' comment '缴费间隔',
    jjfs_jfqj_down   VARCHAR(5) null     default '' comment '缴费期间下限',
    jjfs_jfqj_up     VARCHAR(5) null     default '' comment '缴费期间上限',
    bzfs_bzlx        VARCHAR(2) null     default '' comment '保障类型',
    bzfs_bzqj_down   VARCHAR(5) null     default '' comment '保障期间下限',
    bzfs_bzqj_up     VARCHAR(5) null     default '' comment '保障期间上限',
    pay_count_down   VARCHAR(5) null     default '' comment '保单年度下限',
    pay_count_up     VARCHAR(5) null     default '' comment '保单年度上限',
    amnt_down        VARCHAR(18) null     default '' comment '保额下限',
    amnt_up          VARCHAR(18) null     default '' comment '保额上限',
    trans_money_down VARCHAR(18) null     default '' comment '保费下限',
    trans_money_up   VARCHAR(18) null     default '' comment '保费上限',
    plan_code        VARCHAR(18) null     default '' comment '方案代码',
    plan_name        VARCHAR(200) null     default '' comment '方案名称',
    f01              VARCHAR(20) null     default '' comment '备用字段一',
    f01_down         VARCHAR(50) null     default '' comment '备用字段一下限',
    f01_up           VARCHAR(50) null     default '' comment '备用字段一上限',
    f02              VARCHAR(20) null     default '' comment '备用字段二',
    f02_down         VARCHAR(50) null     default '' comment '备用字段二下限',
    f02_up           VARCHAR(50) null     default '' comment '备用字段二上限',
    f03              VARCHAR(20) null     default '' comment '备用字段三',
    f03_down         VARCHAR(50) null     default '' comment '备用字段三下限',
    f03_up           VARCHAR(50) null     default '' comment '备用字段三上限',
    f04              VARCHAR(20) null     default '' comment '备用字段四',
    f04_down         VARCHAR(50) null     default '' comment '备用字段四下限',
    f04_up           VARCHAR(50) null     default '' comment '备用字段四上限',
    f05              VARCHAR(20) null     default '' comment '备用字段五',
    f05_down         VARCHAR(50) null     default '' comment '备用字段五下限',
    f05_up           VARCHAR(50) null     default '' comment '备用字段五上限',
    f06              VARCHAR(20) null     default '' comment '备用字段六',
    f06_down         VARCHAR(50) null     default '' comment '备用字段六下限',
    f06_up           VARCHAR(50) null     default '' comment '备用字段六上限',
    f07              VARCHAR(20) null     default '' comment '备用字段七',
    f07_down         VARCHAR(50) null     default '' comment '备用字段七下限',
    f07_up           VARCHAR(50) null     default '' comment '备用字段七上限',
    f08              VARCHAR(20) null     default '' comment '备用字段八',
    f08_down         VARCHAR(50) null     default '' comment '备用字段八下限',
    f08_up           VARCHAR(50) null     default '' comment '备用字段八上限',
    f09              VARCHAR(50) null     default '' comment '备用字段九',
    f010             VARCHAR(50) null     default '' comment '备用字段十',
    f011             VARCHAR(50) null     default '' comment '备用字段十一',
    f012             VARCHAR(50) null     default '' comment '备用字段十二',
    f013             VARCHAR(50) null     default '' comment '备用字段十三',
    f014             VARCHAR(50) null     default '' comment '备用字段十四',
    f015             VARCHAR(50) null     default '' comment '备用字段十五',
    f016             VARCHAR(50) null     default '' comment '备用字段十六',
    f017             VARCHAR(50) null     default '' comment '备用字段十七',
    f018             VARCHAR(50) null     default '' comment '备用字段十八',
    f019             VARCHAR(50) null     default '' comment '备用字段十九',
    f020             VARCHAR(50) null     default '' comment '备用字段二十',
    weight           bigint(6)               not null default 1 comment '配置权重',
    remark           VARCHAR(500) null     default '' default '' comment '备注',
    rate             VARCHAR(20)             not null default '' comment '费率',
    rate2            VARCHAR(20)                      default '' comment '费率2',
    rate3            VARCHAR(20)                      default '' comment '费率3',
    rate4            VARCHAR(20)                      default '' comment '费率4',
    rate5            VARCHAR(20)                      default '' comment '费率5',
    rate6            VARCHAR(20)                      default '' comment '费率6',
    rate7            VARCHAR(20)                      default '' comment '费率7',
    rate8            VARCHAR(20)                      default '' comment '费率8',
    operator         VARCHAR(60)             NOT NULL COMMENT '操作员',
    makedate         DATE                    NOT NULL COMMENT '入机日期',
    maketime         VARCHAR(8)              NOT NULL COMMENT '入机时间',
    modifydate       DATE                    NOT NULL COMMENT '最后一次修改日期',
    modifytime       VARCHAR(8)              NOT NULL COMMENT '最后一次修改时间',
    modifyoperator   VARCHAR(60)             NOT NULL COMMENT '最后一次修改人',
    lastoperator     varchar(60) null comment '备份人',
    lastmakedatetime datetime null comment '备份时间',
    KEY              `la_rate_commision_b_index1` (`riskcode`),
    KEY              `la_rate_commision_b_index2` (`riskcode`, `managecom`, `agentcom`),
    KEY              `la_rate_commision_b_index3` (`riskcode`, `startdate`, `enddate`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='佣金费率表备份表';


drop table if exists lawelfaretax;
create table lawelfaretax
(
    id                     BIGINT(19)               not null primary key comment '',
    agentcode              varchar(10) not null comment '业务员代码',
    wageno                 varchar(8)  not null comment '薪资年月',
    managecom              varchar(10) not null comment '管理机构',
    branchtype             varchar(2)  not null comment '机构类型',
    branchtype2            varchar(2) null comment '机构类型2',
    oldinsuranceper        decimal(16, 4) default 0 null comment '个人养老保险',
    doctorinsuranceper     decimal(16, 4) default 0 null comment '个人医疗保险',
    workinsuranceper       decimal(16, 4) default 0 null comment '个人工伤保险',
    hurtinsuranceper       decimal(16, 4) default 0 null comment '个人生育保险',
    birthinsuranceper      decimal(16, 4) default 0 null comment '个人生育保险',
    houseinsuranceper      decimal(16, 4) default 0 null comment '个人住房保险',
    sumwelfareper          decimal(16, 4) default 0 null comment '个人福利合计',
    oldinsurancecom        decimal(16, 4) default 0 null comment '公司养老保险',
    doctorinsurancecom     decimal(16, 4) default 0 null comment '公司医疗保险',
    workinsurancecom       decimal(16, 4) default 0 null comment '公司工伤保险',
    hurtinsurancecom       decimal(16, 4) default 0 null comment '公司生育保险',
    birthinsurancecom      decimal(16, 4) default 0 null comment '公司生育保险',
    houseinsurancecom      decimal(16, 4) default 0 null comment '公司住房保险',
    sumwelfarecom          decimal(16, 4) default 0 null comment '公司福利合计',
    beforetax              decimal(16, 4) default 0 null comment '税前金额',
    sumtax                 decimal(16, 4) default 0 null comment '税额',
    summoney               decimal(16, 4) default 0 null comment '总金额',
    accchildrenedu         decimal(16, 4) default 0 null comment '子女教育累计',
    acccontinueedu         decimal(16, 4) default 0 null comment '继续教育累计',
    acchousingloaninterest decimal(16, 4) default 0 null comment '房贷利息累计',
    acchousingrent         decimal(16, 4) default 0 null comment '住房租金累计',
    accsupportold          decimal(16, 4) default 0 null comment '赡养老人累计',
    sumaccspecialdeduction decimal(16, 4) default 0 null comment '专项扣除合计',
    f01                    varchar(10) null comment '自定义字段1',
    f02                    varchar(10) null comment '自定义字段2',
    f03                    decimal(16, 4) null comment '自定义字段3',
    f04                    decimal(16, 4) null comment '自定义字段4',
    f05                    varchar(20) null comment '自定义字段5',
    f06                    varchar(20) null comment '自定义字段6',
    f07                    date null comment '自定义字段7',
    f08                    date null comment '自定义字段8',
    operator               VARCHAR(60) NOT NULL COMMENT '操作员代码',
    makedate               DATE           DEFAULT NULL COMMENT '入机日期',
    maketime               VARCHAR(8)     DEFAULT NULL COMMENT '入机时间',
    modifyoperator         VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    modifydate             DATE           DEFAULT NULL COMMENT '最后一次修改日期',
    modifytime             VARCHAR(8)     DEFAULT NULL COMMENT '最后一次修改时间',
    KEY                    `la_welfare_tax_index1` (`branchtype`),
    KEY                    `la_welfare_tax_index2` (`agentcode`),
    KEY                    `la_welfare_tax_index3` (`managecom`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci comment ='社会福利表';

-- 佣金计算批次表 - 记录整体批次信息和状态
drop table if exists wage_calculation_batch;
CREATE TABLE wage_calculation_batch
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    batch_id      VARCHAR(64) NOT NULL COMMENT '计算批次号(md5Val)',
    run_id        VARCHAR(64) NOT NULL COMMENT '运行ID(唯一标识每次运行)',
    is_current    TINYINT     NOT NULL DEFAULT 1 COMMENT '是否当前批次: 1-是, 0-否(历史记录)',
    manage_com    VARCHAR(32) NOT NULL COMMENT '管理机构代码',
    index_cal_no  VARCHAR(32) NOT NULL COMMENT '计算期间',
    branch_type   VARCHAR(32) NOT NULL COMMENT '渠道类型代码',
    wage_type     VARCHAR(32) NOT NULL COMMENT '佣金类型代码',
    current_step  INT         NOT NULL DEFAULT 1 COMMENT '当前计算步长',
    total_steps   INT         NOT NULL COMMENT '总步长数',
    total_agents  INT         NOT NULL COMMENT '总代理人数',
    pending_count INT         NOT NULL DEFAULT 0 COMMENT '待处理任务数',
    success_count INT         NOT NULL DEFAULT 0 COMMENT '成功任务数',
    fail_count    INT         NOT NULL DEFAULT 0 COMMENT '失败任务数',
    status        VARCHAR(20) NOT NULL COMMENT '批次状态: INIT-初始化, RUNNING-运行中, COMPLETED-完成, FAILED-失败, PAUSED-暂停',
    start_time    DATETIME COMMENT '开始时间',
    finish_time   DATETIME COMMENT '完成时间',
    remark        VARCHAR(500) COMMENT '备注',
    create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX         idx_wage_batch_batch_current (batch_id, is_current),
    INDEX         idx_wage_batch_manage_cal_no (manage_com, index_cal_no),
    INDEX         idx_wage_batch_status (status),
    INDEX         idx_wage_batch_create_time (create_time)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci  COMMENT='佣金计算批次表';
-- 佣金计算步长表 - 记录每个步长的计算情况
drop table if exists wage_calculation_step;
CREATE TABLE wage_calculation_step
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    run_id        VARCHAR(64) NOT NULL COMMENT '运行ID',
    batch_id      VARCHAR(64) NOT NULL COMMENT '计算批次号(md5Val)',
    is_current    TINYINT     NOT NULL DEFAULT 1 COMMENT '是否当前批次: 1-是, 0-否(历史记录)',
    wage_step     INT         NOT NULL COMMENT '计算步长',
    step_name     VARCHAR(100) COMMENT '步长名称',
    total_count   INT         NOT NULL DEFAULT 0 COMMENT '总任务数',
    pending_count INT         NOT NULL DEFAULT 0 COMMENT '待处理数',
    success_count INT         NOT NULL DEFAULT 0 COMMENT '成功数',
    fail_count    INT         NOT NULL DEFAULT 0 COMMENT '失败数',
    status        VARCHAR(20) NOT NULL COMMENT '状态: PENDING-等待中, RUNNING-运行中, COMPLETED-完成, FAILED-失败',
    start_time    DATETIME COMMENT '开始时间',
    finish_time   DATETIME COMMENT '完成时间',
    create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX         idx_wage_step_batch_current_step (batch_id, is_current, wage_step),
    INDEX         idx_wage_step_create_time (create_time)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci  COMMENT='佣金计算步长表';

-- 佣金计算错误明细表 - 记录所有计算错误的详细信息
drop table if exists wage_calculation_error;
CREATE TABLE wage_calculation_error
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    run_id        VARCHAR(64)   NOT NULL COMMENT '运行ID',
    batch_id      VARCHAR(64)   NOT NULL COMMENT '计算批次号(md5Val)',
    is_current    TINYINT       NOT NULL DEFAULT 1 COMMENT '是否当前批次: 1-是, 0-否(历史记录)',
    agent_code    VARCHAR(32)   NOT NULL COMMENT '代理人编码',
    wage_step     INT           NOT NULL COMMENT '计算步长',
    wage_code     VARCHAR(32) COMMENT '佣金项代码',
    manage_com    VARCHAR(32) COMMENT '管理机构代码',
    agent_grade   VARCHAR(32) COMMENT '代理人职级',
    error_message VARCHAR(2000) NOT NULL COMMENT '错误信息',
    error_stack   TEXT COMMENT '错误堆栈',
    occur_time    DATETIME      NOT NULL COMMENT '发生时间',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX         idx_wage_error_run_id (run_id),
    INDEX         idx_wage_error_batch_current (batch_id, is_current),
    INDEX         idx_wage_error_agent_code (agent_code),
    INDEX         idx_wage_error_occur_time (occur_time)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci  COMMENT='佣金计算错误明细表';

-- 佣金计算任务表 - 记录每个代理人在每个步长的计算状态
drop table if exists wage_calculation_task;
CREATE TABLE wage_calculation_task
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    run_id        VARCHAR(64) NOT NULL COMMENT '运行ID',
    batch_id      VARCHAR(64) NOT NULL COMMENT '计算批次号(md5Val)',
    is_current    TINYINT     NOT NULL DEFAULT 1 COMMENT '是否当前批次: 1-是, 0-否(历史记录)',
    agent_code    VARCHAR(32) NOT NULL COMMENT '代理人编码',
    wage_step     INT         NOT NULL COMMENT '计算步长',
    manage_com    VARCHAR(32) COMMENT '管理机构代码',
    agent_grade   VARCHAR(32) COMMENT '代理人职级',
    status        VARCHAR(10) NOT NULL COMMENT '状态: PENDING-待处理, SUCCESS-成功, FAILED-失败',
    process_count INT                  DEFAULT 0 COMMENT '处理次数',
    start_time    DATETIME COMMENT '开始计算时间',
    finish_time   DATETIME COMMENT '计算完成时间',
    last_error_id BIGINT COMMENT '最后一次错误ID',
    create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX         idx_wage_task_batch_current (batch_id, is_current),
    INDEX         idx_wage_task_batch_current_step (batch_id, is_current, wage_step),
    INDEX         idx_wage_task_status (status),
    INDEX         idx_wage_task_create_time (create_time)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci  COMMENT='佣金计算任务表';





