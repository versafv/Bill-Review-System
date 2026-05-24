---
tags: [tech-note, backend, security]
aliases: [JSON Web Token, Token认证, 无状态认证]
created: "2026-05-23"
---

# JWT (JSON Web Token)

## 核心概念

- **无状态**: 服务端不存储 Session，Token 本身包含用户信息
- **三段式**: Header.Payload.Signature
- **签名验证**: 使用密钥对 Token 签名，防篡改

## JWT 结构

```
Header (算法 + 类型)
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload (用户信息 + 过期时间)
{
  "sub": "user123",
  "iat": 1516239022,
  "exp": 1516242622
}

Signature (签名)
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

## 在项目中的应用

### 登录流程

1. 用户提交用户名/密码
2. 后端验证 → 生成 JWT (包含 userId, username, 过期时间)
3. JWT 存入 Redis (用于退出登录时失效)
4. 前端收到 Token → 存入 localStorage

### 请求认证

1. 前端每次请求携带 `Authorization: Bearer <token>`
2. `JwtAuthenticationTokenFilter` 拦截请求
3. 从 Token 中解析 userId
4. 从 Redis 获取用户信息
5. 设置 SecurityContext

## Token 刷新

- 项目使用"快过期自动续期"策略
- Token 剩余时间 < 20 分钟 → 自动生成新 Token → 写入响应头

## 相关笔记

- [[SpringSecurity]]
- [[../模块说明/zsc-framework|zsc-framework]]
