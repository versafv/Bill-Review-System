---
tags: [tech-note, backend, browser]
aliases: [CORS, 跨域, 同源策略, Cross-Origin]
created: "2026-05-24"
---

# CORS 跨域

## 核心概念

- **同源策略 (Same-Origin Policy)**: 浏览器的安全机制，协议、域名、端口三者必须完全相同才能发 AJAX 请求
- **跨域 (Cross-Origin)**: 三者任一不同即为跨域，浏览器会拦截响应
- **CORS**: W3C 标准，服务器通过响应头告诉浏览器"允许跨域"
- **预检请求 (Preflight)**: 非简单请求先发 `OPTIONS` 请求，服务器返回允许的方法/头，浏览器再发真实请求

## 为什么前后端分离会有跨域

```
前端 Vite 开发服务器  →  http://localhost:5173
后端 Spring Boot     →  http://localhost:8080
                          ↑ 端口不同 → 跨域
```

## 关键响应头

| 响应头 | 作用 |
|--------|------|
| `Access-Control-Allow-Origin` | 允许的来源域名 |
| `Access-Control-Allow-Methods` | 允许的 HTTP 方法 |
| `Access-Control-Allow-Headers` | 允许的请求头 |
| `Access-Control-Max-Age` | 预检请求缓存时间（秒） |

## 在项目中的配置

### ResourcesConfig.java (zsc-framework)

```java
@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOriginPattern("*");   // 允许任意来源
    config.addAllowedHeader("*");          // 允许任意请求头
    config.addAllowedMethod("*");          // 允许任意 HTTP 方法
    config.setMaxAge(1800L);              // 预检缓存 30 分钟
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
```

### SecurityConfig.java — 插入过滤器链

```java
.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
.addFilterBefore(corsFilter, LogoutFilter.class)
```

CORS Filter 必须放在 Security 过滤器链最前面，在 JWT 认证之前处理跨域。

## 请求流程

```
浏览器                                    后端
  │                                        │
  │  OPTIONS /api/bill (预检)              │
  │──────────────────────────────────────→│
  │  ← Access-Control-Allow-Origin: *      │
  │  ← Access-Control-Allow-Methods: *     │
  │  ← Access-Control-Max-Age: 1800        │
  │                                        │
  │  POST /api/bill (真实请求)              │
  │──────────────────────────────────────→│
  │  ← 业务数据                            │
  │                                        │
```

## 注意事项

- **开发阶段**: `*` 全放通，方便本地调试
- **生产环境**: 将 `addAllowedOriginPattern("*")` 改为具体前端域名，如 `"https://app.example.com"`
- **不需要跨域的情况**: 如果用 Nginx 反向代理将前后端统一到同端口同域名，可移除 CORS 配置

## 相关笔记

- [[SpringSecurity]]
- [[../模块说明/zsc-framework|zsc-framework]]
