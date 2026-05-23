---
tags: [backend, module]
---

# zsc-quartz（定时任务）

**POM**: `zsc_backend/zsc-quartz/pom.xml`
**依赖**: quartz, zsc-common

基于 Quartz 的动态定时任务调度模块，任务持久化到 `sys_job` 和 `sys_job_log` 表。支持运行时创建/暂停/恢复/删除任务。

## Controller (路径前缀 `/monitor/job`)

| Controller | 说明 | 权限前缀 |
|------------|------|----------|
| `SysJobController` | 任务 CRUD, 状态变更, 立即执行一次 | `monitor:job:*` |
| `SysJobLogController` | 执行日志查看/清空 | `monitor:jobLog:*` |

## 域

| 类 | 对应表 | 关键字段 |
|----|--------|----------|
| `SysJob` | sys_job | jobId, jobName, invokeTarget (调用目标字符串), cronExpression, concurrent (1=禁止并发), status (0=暂停) |
| `SysJobLog` | sys_job_log | jobLogId, jobName, invokeTarget, status (0=成功 1=失败), exceptionInfo, startTime, endTime |

## Service

| 接口 | 实现 | 关键逻辑 |
|------|------|----------|
| `ISysJobService` | `SysJobServiceImpl` | `init()` 在启动时从数据库加载所有活动任务并注册到 Quartz 调度器 |
| `ISysJobLogService` | `SysJobLogServiceImpl` | 日志写入/清理 |

## Quartz 执行机制

```
SysJob.invokeTarget = "ryTask.ryParams('hello')"
  → Quartz Job 触发
  → AbstractQuartzJob.execute()
    → before() — 记录开始时间
    → JobInvokeUtil.invokeMethod() — 反射调用目标方法
    → after() — 写入 SysJobLog (成功/失败)
```

### Job 执行类

| 类 | 说明 |
|----|------|
| `QuartzJobExecution` | 允许并发执行的任务 |
| `QuartzDisallowConcurrentExecution` | 禁止并发执行 (concurrent=1) |

### 工具类

| 类 | 说明 |
|----|------|
| `ScheduleUtils` | 创建/暂停/恢复/删除 Quartz Job |
| `CronUtils` | Cron 表达式校验 |
| `JobInvokeUtil` | 反射调用目标 Bean 的方法，支持参数 |

### 安全限制

- 禁止协议: rmi, ldap/ldaps, http/https
- invokeTarget 必须匹配白名单前缀 (`ryTask.`, `task.`)

## 配置

`ScheduleConfig` — Quartz 调度器工厂配置。

## 示例任务

`RyTask.java` — 演示任务:
- `ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)` — 多类型参数
- `ryParams(String params)` — 字符串参数
- `ryNoParams()` — 无参数

## 典型使用

1. 登录系统 → 系统工具 → 定时任务
2. 新建任务: 填写名称、Cron 表达式、调用目标字符串
3. 启动任务 → Quartz 按 Cron 调度执行
4. 查看执行日志 → 排查失败原因

## 相关笔记

- [[../后端总览|后端总览]]
- [[zsc-admin]]
