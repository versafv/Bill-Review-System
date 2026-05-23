---
tags: [tech-note, backend, cache]
---

# Redis

## 核心概念

- **内存数据库**: 数据存储在内存中，读写极快
- **数据结构**: String, List, Set, Hash, ZSet
- **持久化**: RDB (快照) / AOF (日志)
- **过期策略**: 可设置 Key 的过期时间

## 在项目中的应用

| 用途 | 说明 |
|------|------|
| 用户 Session | 存储登录用户信息 |
| JWT Token | Token 黑名单 (退出登录) |
| 验证码 | 登录验证码，设置过期时间 |
| 系统配置 | 系统参数缓存 |
| 字典数据 | 字典数据缓存 |

## 配置

Spring Boot 通过 `spring-boot-starter-data-redis` 集成：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

## 常用操作

```java
@Autowired
private RedisCache redisCache;

// 设置缓存 (30分钟过期)
redisCache.setCacheObject("key", value, 30, TimeUnit.MINUTES);

// 获取缓存
String value = redisCache.getCacheObject("key");

// 删除缓存
redisCache.deleteObject("key");
```

## 相关笔记

- [[../模块说明/zsc-framework|zsc-framework]]
- [[../../00-项目总览/环境配置|环境配置]]
