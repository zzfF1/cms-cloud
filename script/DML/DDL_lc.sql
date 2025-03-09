
drop table if exists lc_main;
create table lc_main
(
    serialno bigint(10) not null comment '流水号' primary key,
    name     varchar(50) not null comment '流程类型名称',
    recno    int(10)     not null comment '顺序号',
    lc_table varchar(50) not null comment '业务表',
    lc_field varchar(50) not null comment '流程字段',
    id_field varchar(50) not null comment '业务表主键',
    reject_field varchar(20) not null comment '驳回字段',
    reject_field2 varchar(20) not null comment '驳回原因字段',
    mulkey   int(10)     not null comment '业务表是不是多主键 1多主键 0单主键'
)  ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程类型表';

drop table if exists lc_define;
create table lc_define
(
    id bigint(10) not null comment '流程id' primary key,
    name        varchar(50) not null comment '流程名称',
    recno       int(10)     not null comment '顺序号',
    lc_serialno int(10)     not null comment '流程类型',
    next_id     int(10)     not null comment '下一个流程节点'
)  ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程定义';

drop table if exists lc_dz;
create table lc_dz
(
    serialno BIGINT(20) not null comment '动作id' primary key,
    name     varchar(50)   not null comment '检查说明',
    recno    int(10)       not null comment '顺序号',
    lc_id    int(10)       not null comment '流程id',
    type     int(10)       not null comment '类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时',
    dz       varchar(5000) not null comment '动作条件',
    dz_type  int(10)       not null comment '动作类型 0-SQL 3-实现类'
)  ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程动作';

drop table if exists lc_check;
create table lc_check
(
    serialno   BIGINT(20) not null comment '检查id' primary key,
    name       varchar(50)   not null comment '检查说明',
    recno      int(10)       not null comment '顺序号',
    lc_id      int(10)       not null comment '流程id',
    type       int(10)       not null comment '类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时',
    check_tj   varchar(5000) not null comment '检查条件',
    check_type int(10)       not null comment '检查类型 0-SQL 3-实现类',
    check_msg   varchar(5000) not null comment '检查提示说明'
)ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程检查';

drop table if exists lc_tz;
create table lc_tz
(
    serialno   BIGINT(20) not null comment '跳转id' primary key,
    recno      int(10)       not null comment '顺序号',
    lc_id      int(10)       not null comment '流程id',
    lc_next_id int(10)       not null comment '下一个流程节点',
    tz_tj      varchar(5000) not null comment '跳转条件',
    sm         varchar(100)  not null comment '跳转说明'
)ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程跳转';

drop table if exists lc_processtrack;
create TABLE lc_processtrack
(
    serial_no    BIGINT(20)       NOT NULL comment '流水号' PRIMARY KEY,
    lc_serial_no INT(6)           NOT NULL comment '流程类型',
    last_flag    INT(2)           DEFAULT 0 NULL comment '最后标志',
    cz_type      INT(2)           DEFAULT 0 NULL comment '操作类型 0保存 1提交 -1退回',
    data_id      VARCHAR(20)      NOT NULL comment '业务数据ID',
    lc_id        INT(10)          NOT NULL comment '流程值',
    operator     VARCHAR(50)      DEFAULT '' NULL comment '操作人',
    make_date    DATETIME         NOT NULL comment '操作时间',
    yj           VARCHAR(200)     DEFAULT '' NULL comment '意见'
)ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程轨迹表';

drop table if exists lc_property;
create table lc_property
(
    serial_no bigint(20) not null comment '流水号' primary key,
    lc_id     int(10)      not null comment '流程id',
    attr_name varchar(50) not null comment '属性编码',
    type      int(2)      not null comment '属性类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时',
    val       varchar(200) not null comment '值'
)ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci comment ='流程属性';
