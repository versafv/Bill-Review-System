-- 13_add_reviewer_role.sql
-- 新增票据审核员角色

-- 1. 新增角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(4, '票据审核员', 'reviewer', 4, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '票据审核员 — 审核通过/退回票据');

-- 2. 分配菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 3000),   -- 票据管理（目录）
(4, 3012),   -- 票据概览（统计仪表盘）
(4, 3002),   -- 票据审核（待审队列）
(4, 3006),   -- 审核按钮（biz:bill:review）
(4, 3003),   -- 查询按钮（biz:bill:query）
(4, 3008);   -- 类别查询（biz:category:query，供 BillForm 下拉框加载类别选项）
