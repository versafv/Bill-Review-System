-- ============================================
-- 迁移: 04_add_audit_log_table.sql
-- 说明: 新增审核记录表 biz_audit_log，保留完整审核历史
-- ============================================

DROP TABLE IF EXISTS biz_audit_log;
CREATE TABLE biz_audit_log (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    bill_id     bigint(20)   NOT NULL COMMENT '票据ID',
    action      char(1)      NOT NULL COMMENT '操作: 1-通过 2-退回',
    comment     varchar(500) DEFAULT NULL COMMENT '审核意见',
    audit_by    varchar(64)  NOT NULL COMMENT '审核人',
    audit_time  datetime     NOT NULL COMMENT '审核时间',
    PRIMARY KEY (id),
    KEY idx_bill_id (bill_id),
    KEY idx_audit_by (audit_by),
    KEY idx_audit_time (audit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';
