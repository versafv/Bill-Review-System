---
tags: [backend, module]
aliases: [system, 系统管理, 用户角色菜单]
created: "2026-05-23"
---

# zsc-system（系统管理）

**POM**: `zsc_backend/zsc-system/pom.xml`
**依赖**: 仅 zsc-common

包含所有 `sys_*` 表对应的实体、Mapper (15)、Service (12)。不包含 Controller —— 所有 Controller 在 zsc-admin。

## 域实体 (`com.zsc.system.domain`)

### 主实体

| 类 | 对应表 | 说明 |
|----|--------|------|
| `SysConfig` | sys_config | 系统配置参数 |
| `SysDept` | sys_dept | 部门 (扩展 TreeEntity) |
| `SysDictData` | sys_dict_data | 字典数据 |
| `SysDictType` | sys_dict_type | 字典类型 |
| `SysLogininfor` | sys_logininfor | 登录日志 |
| `SysMenu` | sys_menu | 菜单/权限 (扩展 TreeEntity) |
| `SysNotice` | sys_notice | 通知公告 |
| `SysOperLog` | sys_oper_log | 操作日志 |
| `SysPost` | sys_post | 岗位 |
| `SysRole` | sys_role | 角色 |
| `SysUser` | sys_user | 用户 (扩展 BaseEntity) |
| `SysUserOnline` | — | 在线用户会话 (仅内存) |

### 关联实体

| 类 | 对应表 | 说明 |
|----|--------|------|
| `SysUserRole` | sys_user_role | 用户-角色 N:M |
| `SysRoleMenu` | sys_role_menu | 角色-菜单 N:M |
| `SysRoleDept` | sys_role_dept | 角色-部门关联 |
| `SysUserPost` | sys_user_post | 用户-岗位 N:M |

### 视图 VO

| 类 | 说明 |
|----|------|
| `MetaVo` | 前端路由元数据 (title, icon, noCache, link) |
| `RouterVo` | 前端路由结构 (name, path, component, meta, children) |

## Mapper 层 (15 个接口 + 15 个 XML)

所有接口位于 `com.zsc.system.mapper`，XML 位于 `zsc-system/src/main/resources/mapper/system/`。

| Mapper | XML | 关键方法 |
|--------|-----|----------|
| `SysUserMapper` | SysUserMapper.xml | 按用户名查询, 分页列表, 角色关联查询 |
| `SysRoleMapper` | SysRoleMapper.xml | 按用户ID查角色, 角色列表 |
| `SysMenuMapper` | SysMenuMapper.xml | 按用户查菜单, 按角色查菜单, 权限字符串 |
| `SysDeptMapper` | SysDeptMapper.xml | 部门树查询, 子部门查询 |
| `SysPostMapper` | SysPostMapper.xml | 岗位列表, 按用户查岗位 |
| `SysConfigMapper` | SysConfigMapper.xml | 按 key 查配置 |
| `SysDictDataMapper` | SysDictDataMapper.xml | 按类型查字典 |
| `SysDictTypeMapper` | SysDictTypeMapper.xml | 字典类型 CRUD |
| `SysNoticeMapper` | SysNoticeMapper.xml | 通知公告列表 |
| `SysLogininforMapper` | SysLogininforMapper.xml | 登录日志写入/清理 |
| `SysOperLogMapper` | SysOperLogMapper.xml | 操作日志写入/清理 |
| `SysUserRoleMapper` | SysUserRoleMapper.xml | 用户角色关联 |
| `SysRoleMenuMapper` | SysRoleMenuMapper.xml | 角色菜单关联 |
| `SysRoleDeptMapper` | SysRoleDeptMapper.xml | 角色部门关联 |
| `SysUserPostMapper` | SysUserPostMapper.xml | 用户岗位关联 |

## Service 层 (12 对接口+实现)

| 接口 | 实现 |
|------|------|
| `ISysUserService` | `SysUserServiceImpl` |
| `ISysRoleService` | `SysRoleServiceImpl` |
| `ISysMenuService` | `SysMenuServiceImpl` — 构建前端路由树 |
| `ISysDeptService` | `SysDeptServiceImpl` |
| `ISysPostService` | `SysPostServiceImpl` |
| `ISysConfigService` | `SysConfigServiceImpl` |
| `ISysDictDataService` | `SysDictDataServiceImpl` |
| `ISysDictTypeService` | `SysDictTypeServiceImpl` |
| `ISysNoticeService` | `SysNoticeServiceImpl` |
| `ISysLogininforService` | `SysLogininforServiceImpl` |
| `ISysOperLogService` | `SysOperLogServiceImpl` |
| `ISysUserOnlineService` | `SysUserOnlineServiceImpl` |

## RBAC 权限模型

```
用户 (sys_user) ←N:M→ 角色 (sys_role) ←N:M→ 菜单/权限 (sys_menu)
                       ↓
                 数据权限 (DataScope):
                   - 全部数据
                   - 本部门数据
                   - 本部门及子部门
                   - 仅本人数据
```

## 相关笔记

- [[zsc-admin]] — Controller 层
- [[zsc-framework]] — 安全认证
- [[../数据库设计]]
- [[../../01-需求与设计/E-R图|E-R 图]]
