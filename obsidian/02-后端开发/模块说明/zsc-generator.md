---
tags: [backend, module]
aliases: [generator, 代码生成, Velocity]
created: "2026-05-23"
---

# zsc-generator（代码生成器）

**POM**: `zsc_backend/zsc-generator/pom.xml`
**依赖**: velocity-engine-core, zsc-common, druid-spring-boot-3-starter

基于 Apache Velocity 模板引擎的代码生成器。从数据库表结构自动生成完整的 CRUD 代码（Java + XML + Vue + JS + SQL）。

## Controller

`GenController` — 路径 `/tool/gen`，权限 `tool:gen:*`

| 端点 | 说明 |
|------|------|
| `listTable` | 已导入表列表 |
| `listDbTable` | 数据库表列表（待导入） |
| `getGenTable` | 表生成配置详情 + 列信息 |
| `updateGenTable` | 修改生成配置 |
| `importTable` | 从数据库导入表 |
| `createTable` | 在线创建表 |
| `previewTable` | 预览生成代码 |
| `delTable` | 删除表配置 |
| `genCode` | 生成代码 ZIP 下载 |
| `synchDb` | 同步数据库表结构 |

## 域

| 类 | 说明 | 关键字段 |
|----|------|----------|
| `GenTable` | 表生成配置 | tableName, tableComment, className, tplCategory, packageName, moduleName, businessName, functionName, genType, pkColumn, columns |
| `GenTableColumn` | 列生成配置 | columnName, javaType, javaField, isPk, isIncrement, isRequired, isInsert, isEdit, isList, isQuery, queryType, htmlType, dictType |

## Service

| 接口 | 实现 | 关键逻辑 |
|------|------|----------|
| `IGenTableService` | `GenTableServiceImpl` | 读取表结构 → 填充列信息 → 预览/生成 |
| `IGenTableColumnService` | `GenTableColumnServiceImpl` | 列配置管理 |

## 三种生成策略

| 策略 | 模板数量 | 适用场景 |
|------|----------|----------|
| `crud` | 8 个 | 普通单表增删改查 |
| `tree` | 8 个 (+ index-tree.vue) | 树形表（部门/菜单型） |
| `sub` | 9 个 (+ sub-domain.java) | 主子表 |

## 生成文件清单

按策略 `crud` 生成 8 个文件：

| 模板 | 输出路径 |
|------|----------|
| `vm/java/domain.java.vm` | `src/main/java/.../domain/Xxx.java` |
| `vm/java/mapper.java.vm` | `src/main/java/.../mapper/XxxMapper.java` |
| `vm/java/service.java.vm` | `src/main/java/.../service/IXxxService.java` |
| `vm/java/serviceImpl.java.vm` | `src/main/java/.../service/impl/XxxServiceImpl.java` |
| `vm/java/controller.java.vm` | `src/main/java/.../controller/XxxController.java` |
| `vm/xml/mapper.xml.vm` | `src/main/resources/mapper/.../XxxMapper.xml` |
| `vm/vue/v3/index.vue.vm` | `src/views/.../index.vue` |
| `vm/js/api.js.vm` | `src/api/.../xxx.js` |
| `vm/sql/sql.vm` | `sql/xxxMenu.sql` (菜单 SQL) |

## 工具类

| 类 | 说明 |
|----|------|
| `GenUtils` | 表名转类名 (下划线→驼峰)，列类型→Java 类型映射 |
| `VelocityInitializer` | Velocity 引擎初始化 |
| `VelocityUtils` | 构建 Velocity 上下文，选择模板，生成文件头注释 |

## 配置

`GenConfig` — 读取 `generator.yml`:
- 作者: `zsc`
- 包名: `com.zsc.system`
- 表前缀: `sys_` (生成类名时自动移除)

## 使用方式

1. 设计数据库表 → 执行 DDL
2. 登录系统 → 系统工具 → 代码生成
3. 导入对应表
4. 预览 → 确认无误 → 生成 → 下载 ZIP
5. 解压到项目对应目录 → 重启

## 相关笔记

- [[../后端总览|后端总览]]
- [[zsc-admin]]
- [[zsc-module]] — 使用生成器搭建的示例模块
