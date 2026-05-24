---
tags: [tech-note, backend, spring]
aliases: [Spring, Spring Boot 3, 自动配置]
created: "2026-05-23"
---

# Spring Boot

## 版本

项目使用 Spring Boot 3.5.8。

## 核心概念

- **自动配置 (Auto Configuration)**: 根据类路径和配置自动装配 Bean
- **起步依赖 (Starter)**: 集成一组依赖，简化配置
- **内嵌服务器**: 内嵌 Tomcat，无需部署 WAR

## 在项目中的应用

- 父项目 `pom.xml` 中继承 `spring-boot-starter-parent`
- 各模块引入所需 starter (`spring-boot-starter-web`, `spring-boot-starter-security` 等)
- 启动类 `RuoYiApplication.java` 使用 `@SpringBootApplication` 注解

## 项目中的 Starter

| Starter | 用途 |
|---------|------|
| spring-boot-starter-web | Web (MVC) 支持 |
| spring-boot-starter-security | 安全框架 |
| spring-boot-starter-data-redis | Redis 集成 |
| spring-boot-starter-validation | 参数校验 |
| spring-boot-starter-aop | AOP 支持 |
| spring-boot-starter-test | 测试 |

## 相关笔记

- [[../后端总览|后端总览]]
- [[MyBatis-Plus]]
- [[SpringSecurity]]
