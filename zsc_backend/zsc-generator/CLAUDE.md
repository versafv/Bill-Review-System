# zsc-generator — 代码生成器

基于 Apache Velocity 模板引擎，从数据库表结构自动生成前后端代码。

## 输入 → 输出

```
数据库表 → 导入表结构(sys_gen_table + sys_gen_table_column)
  → Velocity 模板渲染
  → 生成文件：
    Java: domain, mapper, service, serviceImpl, controller
    XML:  MyBatis 映射文件
    Vue:  index.vue (crud/tree) + index-tree.vue
    JS:   API 请求模块
    SQL:  菜单插入语句
```

## 三种模板类型（tplCategory）

| 类型 | 模板数 | 适用 |
|------|--------|------|
| `crud` | 8 | 普通单表增删改查 |
| `tree` | 8 | 树形表（含 index-tree.vue） |
| `sub` | 9 | 主子表（额外含 sub-domain.java） |

## 关键文件

```
controller/GenController.java        ← 路径 /tool/gen
util/VelocityUtils.java              ← VelocityContext 构建、模板选择、包名/类名生成
util/GenUtils.java                   ← 表名→类名转换、字段类型映射
config/GenConfig.java                ← 配置：作者/包路径/表前缀/覆盖开关
resources/vm/                        ← Velocity 模板文件
```

## 配置（generator.yml）

```yaml
gen:
  author: zsc
  packageName: com.zsc.system
  autoRemovePre: true       # 自动去掉表前缀
  tablePrefix: sys_
  allowOverwrite: false     # 不覆盖已有文件
```

## 权限前缀

`tool:gen:list`, `tool:gen:query`, `tool:gen:add`, `tool:gen:edit`, `tool:gen:remove`, `tool:gen:code`, `tool:gen:preview`, `tool:gen:import`
