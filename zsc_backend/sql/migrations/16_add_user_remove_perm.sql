-- 16_add_user_remove_perm.sql
-- 为系统管理员角色分配用户删除权限

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(5, 1003);   -- 用户删除（system:user:remove）
