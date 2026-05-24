---
tags: [tech-note, backend, orm]
aliases: [MyBatis, ORM, BaseMapper, LambdaQuery]
created: "2026-05-23"
---

# MyBatis-Plus

## 版本

项目使用 MyBatis-Plus 3.5.9。

## 核心概念

- **BaseMapper<T>**: 通用 Mapper 接口，提供内置 CRUD 方法
- **IService<T>**: 通用 Service 接口
- **ServiceImpl<M, T>**: 通用 Service 实现
- **LambdaQueryWrapper**: 类型安全的条件构造器
- **分页插件**: 自动分页支持

## 在项目中的应用

```java
// Mapper 层 - 继承 BaseMapper 获得通用 CRUD
public interface BizBillMapper extends BaseMapper<BizBill> {
}

// Service 层 - 继承 IService
public interface IBizBillService extends IService<BizBill> {
}

// Service 实现 - 继承 ServiceImpl
public class BizBillServiceImpl
    extends ServiceImpl<BizBillMapper, BizBill>
    implements IBizBillService {
}
```

## 常用方法

| 方法 | 说明 |
|------|------|
| selectById(id) | 按 ID 查询 |
| selectList(wrapper) | 按条件查询列表 |
| selectPage(page, wrapper) | 分页查询 |
| insert(entity) | 插入 |
| updateById(entity) | 按 ID 更新 |
| deleteById(id) | 按 ID 删除 |

## 相关笔记

- [[../后端总览|后端总览]]
- [[SpringBoot]]
- [[../数据库设计|数据库设计]]
