-- ============================================
-- 迁移: 03_add_bill_file_table.sql
-- 说明: 新增票据附件表 biz_bill_file，支持多文件上传
-- ============================================

DROP TABLE IF EXISTS biz_bill_file;
CREATE TABLE biz_bill_file (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    bill_id     bigint(20)   NOT NULL COMMENT '票据ID',
    file_name   varchar(255) NOT NULL COMMENT '原始文件名',
    file_path   varchar(500) NOT NULL COMMENT '存储路径（相对路径）',
    file_size   bigint(20)   DEFAULT NULL COMMENT '文件大小（字节）',
    file_type   varchar(50)  DEFAULT NULL COMMENT '文件类型（扩展名）',
    sort_order  int(11)      DEFAULT 0 COMMENT '排序（同票据内附件显示顺序）',
    create_by   varchar(64)  DEFAULT NULL COMMENT '上传者',
    create_time datetime     DEFAULT NULL COMMENT '上传时间',
    PRIMARY KEY (id),
    KEY idx_bill_id (bill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='票据附件表';
