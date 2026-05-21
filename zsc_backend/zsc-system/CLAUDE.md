# zsc-system — 系统业务层

系统核心业务模块，对应 `sys_*` 表。只含 **Entity / Mapper / Service**，无 Controller。

## 依赖

仅依赖 `zsc-common`，被 `zsc-framework` 依赖。

## 分层

```
domain/       ← 实体类：SysUser, SysRole, SysDept, SysMenu, SysPost, SysDictType, SysDictData,
                 SysConfig, SysNotice, SysOperLog, SysLogininfor, SysUserOnline,
                 关联类：SysUserRole, SysRoleMenu, SysRoleDept, SysUserPost
                 + RouterVo/MetaVo (前端路由 JSON)
mapper/       ← MyBatis 接口（继承 BaseMapper）+ XML 在 resources/mapper/system/
service/      ← ISysXxxService 接口 + impl 实现类
```

## 关键约定

- **MyBatis-Plus + XML 混写**：简单查询用 BaseMapper lambdaQuery，复杂 SQL 在 XML 里手写
- **无 Controller**：Controller 全部在 zsc-admin
- 角色数据权限：`checkRoleDataScope()` / 部门数据权限：`checkDeptDataScope()`
- 用户唯一性校验：`checkUserNameUnique()`, `checkPhoneUnique()`, `checkEmailUnique()`
- 菜单树构建：`SysMenuServiceImpl` 递归构建父子结构，返回 RouterVo 列表给前端
