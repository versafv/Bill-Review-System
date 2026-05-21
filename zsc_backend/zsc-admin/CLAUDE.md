# zsc-admin — 启动入口 & Controller 层

Spring Boot 3.5.x 单体应用唯一启动模块（端口 8080），聚合所有子模块，提供全部 REST Controller。

## 职责

- 启动类 `ZscApplication.java` — 排除 DataSourceAutoConfiguration（由 zsc-framework 管理多数据源）
- 所有 Controller 集中在本模块的 `web/controller/`
- 配置文件 `src/main/resources/`

## 结构

```
web/controller/
  common/     ← CaptchaController, CommonController（验证码、文件上传下载）
  monitor/    ← CacheController, ServerController, SysLogininforController, SysOperlogController, SysUserOnlineController
  system/     ← 登录/注册 + 用户/角色/菜单/部门/岗位/字典/通知/配置（14个 Controller）
  tool/       ← TestController
resources/
  application.yml          ← 主配置（端口/Redis/日志等）
  application-druid.yml    ← 数据源配置（修改密码在这里）
  logback.xml
```

## 关键约定

- Controller 统一继承 `BaseController`（zsc-common），用 `startPage()` 构建分页，`getDataTable()` / `success()` / `error()` 返回
- 权限：`@PreAuthorize("@ss.hasPermi('system:user:list')")` — 由 zsc-framework 的 PermissionService 执行
- 操作日志：`@Log(title, businessType)` → zsc-framework 的 LogAspect 自动记录
- 响应体：系统模块用 `AjaxResult` / `TableDataInfo`，来源 zsc-common

## 聚合的子模块

```
zsc-admin 依赖 → zsc-framework → zsc-system → zsc-common
            依赖 → zsc-module, zsc-quartz, zsc-generator（都依赖 zsc-common）
```
