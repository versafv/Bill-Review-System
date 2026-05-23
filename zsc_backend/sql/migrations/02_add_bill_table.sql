-- ============================================
-- 迁移: 02_add_bill_table.sql
-- 说明: 新增票据主表 biz_bill
-- ============================================

DROP TABLE IF EXISTS biz_bill;
CREATE TABLE biz_bill (
    id            bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
    bill_no       varchar(32)   NOT NULL COMMENT '票据编号（自动生成，唯一）',
    title         varchar(255)  NOT NULL COMMENT '票据标题',
    category_id   bigint(20)    DEFAULT NULL COMMENT '票据类别ID',
    amount        decimal(10,2) NOT NULL COMMENT '金额',
    description   varchar(500)  DEFAULT NULL COMMENT '描述',
    status        char(1)       DEFAULT '0' COMMENT '状态: 0-草稿 1-已提交 2-已通过 3-已退回',
    attachment    varchar(500)  DEFAULT NULL COMMENT '附件路径（单文件，保留兼容）',
    create_by     varchar(64)   DEFAULT NULL COMMENT '创建者',
    create_time   datetime      DEFAULT NULL COMMENT '创建时间',
    update_by     varchar(64)   DEFAULT NULL COMMENT '更新者',
    update_time   datetime      DEFAULT NULL COMMENT '更新时间',
    audit_by      varchar(64)   DEFAULT NULL COMMENT '最近审核人',
    audit_time    datetime      DEFAULT NULL COMMENT '最近审核时间',
    audit_comment varchar(500)  DEFAULT NULL COMMENT '最近审核意见',
    remark        varchar(500)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_bill_no (bill_no),
    KEY idx_create_by (create_by),
    KEY idx_status (status),
    KEY idx_category_id (category_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='票据表';
