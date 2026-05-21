# zsc-quartz — 定时任务管理

Quartz 动态任务调度模块，任务持久化到 `sys_job` / `sys_job_log` 表。

## 核心流程

1. **启动恢复**：`SysJobServiceImpl.init()` → 清空 Scheduler → 从 DB 加载所有 Status=正常 的任务 → 逐个注册
2. **任务执行**：`AbstractQuartzJob.execute()` → before(记开始时间) → `JobInvokeUtil.invokeMethod()` 反射调用 → after(写 SysJobLog)
3. **并发控制**：`concurrent=0` 用 `QuartzJobExecution`（允许并发），`concurrent=1` 用 `QuartzDisallowConcurrentExecution`（禁止）

## invokeTarget 格式

```
beanName.methodName(params)        例: ryTask.ryMultipleParams('zsc')
com.xx.ClassName.methodName(params) 例: com.zsc.task.MyTask.run(1, true)
```
参数类型：String（引号包裹）/ boolean / long / double / int 字面量

## 安全约束

- 禁止调用：rmi, ldap/ldaps, http/https
- invokeTarget 必须匹配 JOB_WHITELIST_STR 白名单前缀
- Controller 权限前缀：`monitor:job:*` 和 `monitor:jobLog:*`

## 关键文件

```
controller/SysJobController, SysJobLogController    ← 路径 /monitor/job, /monitor/jobLog
service/impl/SysJobServiceImpl                      ← 核心调度逻辑
util/AbstractQuartzJob.java                         ← Job 抽象基类（模板方法）
util/JobInvokeUtil.java                             ← 反射调用工具
task/RyTask.java                                    ← 测试示例任务
```
