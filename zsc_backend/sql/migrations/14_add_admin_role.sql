-- 14_add_admin_role.sql
-- 新增系统管理员角色 + 管理页面菜单

-- 1. 新增角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(5, '系统管理员', 'admin_user', 5, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '系统管理员 — 管理用户与类别');

-- 2. 新增管理端菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3014, '系统管理', 0, 5, 'admin', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', NOW(), '', NULL, '系统管理目录'),
(3015, '管理概览', 3014, 1, 'dashboard', 'biz/admin/index', '', '', 1, 0, 'C', '0', '0', 'biz:admin:list', 'dashboard', 'admin', NOW(), '', NULL, '管理员仪表盘'),
(3016, '用户管理', 3014, 2, 'users', 'biz/admin/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NULL, '用户管理'),
(3017, '类别管理', 3014, 3, 'bizCategory', 'biz/bizCategory/index', '', '', 1, 0, 'C', '0', '0', 'biz:category:list', 'category', 'admin', NOW(), '', NULL, '业务类别管理'),
(3018, '注册审核', 3014, 4, 'registerReview', 'biz/admin/register/index', '', '', 1, 0, 'C', '0', '0', 'biz:admin:list', 'example', 'admin', NOW(), '', NULL, '管理员审核注册申请');

-- 3. 为管理员角色分配菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(5, 3014),   -- 系统管理（目录）
(5, 3015),   -- 管理概览
(5, 3016),   -- 用户管理
(5, 3017),   -- 类别管理
-- 用户管理按钮权限
(5, 1000),   -- 用户查询（system:user:query）
(5, 1003),   -- 用户删除（system:user:remove）
(5, 1006),   -- 重置密码（system:user:resetPwd）
-- 注册审核
(5, 3018),   -- 注册审核（biz:admin:list）
-- 类别管理按钮权限
(5, 3008),   -- 类别查询（biz:category:query）
(5, 3009),   -- 类别新增（biz:category:add）
(5, 3010),   -- 类别编辑（biz:category:edit）
(5, 3011);   -- 类别删除（biz:category:remove）
