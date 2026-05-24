-- 预置测试类别
INSERT INTO biz_category (category_id, category_name, sort_order, status, create_time, update_time)
VALUES (1, '办公用品', 1, '0', NOW(), NOW()),
       (2, '差旅费', 2, '0', NOW(), NOW()),
       (3, '招待费', 3, '0', NOW(), NOW());

-- 预置测试票据（草稿 - 由 user1 创建）
INSERT INTO biz_bill (id, bill_no, title, category_id, amount, description, status, create_by, create_time)
VALUES (1, 'DRAFT-1001', '测试草稿票据', 1, 100.00, '用于测试的草稿', '0', 'user1', NOW());

-- 预置测试票据（已提交 - 由 user1 创建）
INSERT INTO biz_bill (id, bill_no, title, category_id, amount, description, status, create_by, create_time)
VALUES (2, 'BILL-20260524-0001', '测试已提交票据', 1, 200.00, '用于测试的已提交状态', '1', 'user1', NOW());

-- 预置测试票据（已通过 - 由 user1 创建）
INSERT INTO biz_bill (id, bill_no, title, category_id, amount, description, status, create_by, create_time, audit_by, audit_time, audit_comment)
VALUES (3, 'BILL-20260524-0002', '测试已通过票据', 2, 300.00, '用于测试的已通过状态', '2', 'user1', NOW(), 'admin', NOW(), '同意');

-- 预置测试票据（已退回 - 由 user1 创建）
INSERT INTO biz_bill (id, bill_no, title, category_id, amount, description, status, create_by, create_time, audit_by, audit_time, audit_comment)
VALUES (4, 'BILL-20260524-0003', '测试已退回票据', 2, 400.00, '用于测试的已退回状态', '3', 'user1', NOW(), 'admin', NOW(), '请补充说明');

-- 预置附件（关联票据2）
INSERT INTO biz_bill_file (id, bill_id, file_name, file_path, sort_order, create_by, create_time)
VALUES (1, 2, 'test.pdf', '/upload/test.pdf', 0, 'user1', NOW());

-- 预置审核记录（票据3的审核历史）
INSERT INTO biz_audit_log (id, bill_id, action, comment, audit_by, audit_time)
VALUES (1, 3, '1', '同意', 'admin', NOW());

-- 预置审核记录（票据4的审核历史）
INSERT INTO biz_audit_log (id, bill_id, action, comment, audit_by, audit_time)
VALUES (2, 4, '2', '请补充说明', 'admin', NOW());
