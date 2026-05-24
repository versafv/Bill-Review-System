-- 业务类别表
CREATE TABLE IF NOT EXISTS biz_category (
    category_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    sort_order    INT          DEFAULT 0,
    status        CHAR(1)      DEFAULT '0',
    create_time   DATETIME,
    update_time   DATETIME
);

-- 票据主表
CREATE TABLE IF NOT EXISTS biz_bill (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_no      VARCHAR(50),
    title        VARCHAR(200) NOT NULL,
    category_id  BIGINT,
    amount       DECIMAL(18,2),
    description  TEXT,
    status       CHAR(1)      DEFAULT '0',
    attachment   VARCHAR(500),
    create_by    VARCHAR(64),
    create_time  DATETIME,
    update_by    VARCHAR(64),
    update_time  DATETIME,
    audit_by     VARCHAR(64),
    audit_time   DATETIME,
    audit_comment VARCHAR(500),
    remark       VARCHAR(500)
);

-- 票据附件表
CREATE TABLE IF NOT EXISTS biz_bill_file (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id    BIGINT       NOT NULL,
    file_name  VARCHAR(255),
    file_path  VARCHAR(500),
    file_size  BIGINT,
    file_type  VARCHAR(50),
    sort_order INT,
    create_by  VARCHAR(64),
    create_time DATETIME
);

-- 审核记录表
CREATE TABLE IF NOT EXISTS biz_audit_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id    BIGINT NOT NULL,
    action     CHAR(1),
    comment    VARCHAR(500),
    audit_by   VARCHAR(64),
    audit_time DATETIME
);
