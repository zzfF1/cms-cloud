DROP TABLE IF EXISTS lavacationset;
CREATE TABLE `lavacationset`
(
    `id`          bigint(8) NOT NULL AUTO_INCREMENT comment '主键',
    `managecom`   varchar(8)  DEFAULT NULL COMMENT '管理机构',
    `year`        varchar(4)  NOT NULL COMMENT '年度',
    `month`       varchar(2)  NOT NULL COMMENT '月度',
    `vacation`    date        NOT NULL COMMENT '假期日期',
    `week`        varchar(10) NOT NULL COMMENT '星期',
    `type`        varchar(2)  NOT NULL COMMENT '假期类型',
    `remark`      varchar(100)   DEFAULT NULL COMMENT '假期说明',
    `branchtype`  varchar(2)     DEFAULT NULL COMMENT '展业类型',
    `branchtype2` varchar(2)     DEFAULT NULL COMMENT '渠道',
    `operator`    VARCHAR(60) NOT NULL COMMENT '操作员',
    `modifyoperator`   VARCHAR(60) NOT NULL COMMENT '最后一次修改人',
    `makedate`    DATE        NOT NULL COMMENT '入机日期',
    `maketime`    VARCHAR(8)  NOT NULL COMMENT '入机时间',
    `modifydate`  DATE        NOT NULL COMMENT '最后一次修改日期',
    `modifytime`  VARCHAR(8)  NOT NULL COMMENT '最后一次修改时间',
    `f1`          VARCHAR(20)    DEFAULT NULL COMMENT 'F1',
    `f2`          VARCHAR(20)    DEFAULT NULL COMMENT 'F2',
    `f3`          DECIMAL(12, 4) DEFAULT NULL COMMENT 'F3',
    `f4`          DECIMAL(12, 4) DEFAULT NULL COMMENT 'F4',
    `f5`          DATE           DEFAULT NULL COMMENT 'F5',
    `f6`          DATE           DEFAULT NULL COMMENT 'F6',
    PRIMARY KEY (id),
    UNIQUE KEY unique_vacation (`year`, `month`, `vacation`)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci COMMENT ='销管节假日配置表';
