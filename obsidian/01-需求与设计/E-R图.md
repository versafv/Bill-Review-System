---
tags: [design, diagram]
aliases: [ER图, 实体关系图, Entity Relationship]
created: "2026-05-23"
---

# E-R 图

## 核心实体关系

```mermaid
erDiagram
    sys_user ||--o{ sys_user_role : "拥有"
    sys_user_role }o--|| sys_role : "关联"
    sys_role ||--o{ sys_role_menu : "拥有"
    sys_role_menu }o--|| sys_menu : "关联"
    sys_user ||--o{ sys_user_post : "担任"
    sys_user_post }o--|| sys_post : "关联"
    sys_role ||--o{ sys_role_dept : "绑定"
    sys_role_dept }o--|| sys_dept : "关联"

    sys_user {
        bigint user_id PK "主键"
        varchar user_name "用户名"
        varchar nick_name "昵称"
        varchar password "密码"
        char status "状态: 0-正常 1-停用"
        varchar email "邮箱"
        varchar phonenumber "手机号"
        bigint dept_id FK "部门ID"
    }

    sys_role {
        bigint role_id PK "主键"
        varchar role_name "角色名"
        varchar role_key "角色标识"
        int data_scope "数据权限范围"
        char status "状态"
    }

    sys_menu {
        bigint menu_id PK "主键"
        varchar menu_name "菜单名"
        varchar perms "权限标识"
        varchar path "路由路径"
        varchar component "组件路径"
        char menu_type "M-目录 C-菜单 F-按钮"
        bigint parent_id "父菜单ID"
    }

    sys_dept {
        bigint dept_id PK "主键"
        varchar dept_name "部门名"
        bigint parent_id "父部门ID"
        varchar ancestors "祖级列表"
    }

    biz_bill {
        bigint id PK "主键"
        varchar bill_no "票据编号"
        varchar title "票据标题"
        decimal amount "金额"
        char status "0-草稿 1-已提交 2-已通过 3-已退回"
        bigint category_id FK "类别ID"
        varchar create_by "创建者"
        varchar audit_by "审核人"
        varchar audit_comment "审核意见"
    }

    biz_category {
        bigint id PK "主键"
        varchar category_name "类别名称"
        int sort_order "排序"
    }

    sys_user ||--o{ biz_bill : "提交"
    biz_bill }o--|| biz_category : "属于"
```

## 实体清单

| 实体 | 表名 | 说明 |
|------|------|------|
| 用户 | sys_user | 系统用户，扩展 BaseEntity |
| 角色 | sys_role | 角色，关联菜单和数据权限 |
| 菜单 | sys_menu | 菜单/权限树，扩展 TreeEntity |
| 部门 | sys_dept | 组织架构树，扩展 TreeEntity |
| 岗位 | sys_post | 用户岗位 |
| 票据 | biz_bill | 核心业务实体（待实现） |
| 票据类别 | biz_category | 票据分类 |

## 关键关系

- **用户 ↔ 角色**: N:M，通过 `sys_user_role` 关联
- **角色 ↔ 菜单**: N:M，通过 `sys_role_menu` 关联（RBAC 权限模型）
- **角色 ↔ 部门**: 1:N，通过 `sys_role_dept` 关联（数据权限）
- **用户 → 票据**: 1:N，一个用户可提交多张票据
- **票据 → 类别**: N:1，每张票据属于一个类别

## 相关笔记

- [[详细设计]]
- [[需求规格说明]]
- [[../02-后端开发/数据库设计|数据库设计]]
