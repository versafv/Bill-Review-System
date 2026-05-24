---
tags: [tech-note, backend, security]
aliases: [SpringSecurity, Security, 安全框架, 认证授权]
created: "2026-05-23"
---

# Spring Security

## 核心概念

- **认证 (Authentication)**: 验证用户身份
- **授权 (Authorization)**: 验证用户权限
- **SecurityContext**: 存储当前用户的安全上下文
- **Filter Chain**: 过滤器链，处理认证和授权

## 在项目中的应用 (RuoYi-Vue)

### 安全配置

- `SecurityConfig` 配置哪些路径需要认证
- 静态资源、登录接口、Swagger 放行
- 其他请求通过 JWT Filter 认证

### JWT 认证流程

```
1. 用户登录 → 验证用户名密码 → 生成 JWT Token → 返回前端
2. 前端携带 Token (Bearer) → JWT Filter 解析 → 设置 SecurityContext
3. 后续请求 → SecurityContext 中获取用户信息
```

### 权限控制

- `@PreAuthorize("@ss.hasPermi('system:user:list')")` 方法级权限控制
- `@PreAuthorize("@ss.hasRole('admin')")` 角色级权限控制

## 相关笔记

- [[JWT]]
- [[../模块说明/zsc-framework|zsc-framework]]
