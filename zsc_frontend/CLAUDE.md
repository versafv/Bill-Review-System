# ZSC 前端 — Vue 3 后台管理界面

基于 RuoYi-Vue 模板，Vue 3.5 + Vite 5 + Element Plus 2.13 + Pinia 3。

## 核心机制

### 登录 → 动态路由注册（最重要）

```
1. POST /login → 拿到 token → Cookie 存储
2. GET /getInfo → 拿到 roles[], permissions[] → user store
3. GET /getRouters → 拿到菜单 JSON 树 [{path, component, meta, children}]
4. permission store.generateRoutes()
   → import.meta.glob('@/views/**/*.vue') 匹配 component 路径字符串
   → filterDynamicRoutes() 按用户权限过滤前端预定义动态路由
   → router.addRoute() 逐一注册
```

### 权限控制（三级）

| 级别 | 机制 | 位置 |
|------|------|------|
| 路由级 | `roles: ['admin']` / `permissions: ['system:user:list']` | `router/index.js` |
| 按钮级 | `v-hasPermi="['system:user:add']"` / `v-hasRole="['admin']"` | 页面 `.vue` |
| 请求级 | 后端 `@PreAuthorize` 拦截 | Controller |

### 请求链路

```
页面 → api/*.js → Axios instance (baseURL: /dev-api)
  → 请求拦截器：注入 Authorization: Bearer <token> + 防重复提交（sessionStorage 指纹）
  → Vite proxy：去掉 /dev-api → http://localhost:8080/*
  → 响应拦截器：code 200 放行 / 401 弹登录过期框 / 500 消息提示
```

## 关键文件

```
src/
  main.js                  ← 注册 Pinia/Router/指令/全局组件
  permission.js             ← 路由守卫 beforeEach（token 校验 + 动态路由加载）
  router/index.js           ← constantRoutes + dynamicRoutes
  utils/request.js          ← Axios 封装（token 注入 + 防重复提交 + 错误处理）
  utils/auth.js             ← Cookie Token 读写
  store/modules/
    user.js                 ← token/roles/permissions/登录/登出/用户信息
    permission.js           ← generateRoutes() 动态路由
    settings.js             ← 主题/布局（localStorage 持久化）
    tagsView.js             ← 标签页导航
    dict.js                 ← 字典数据缓存
  layout/index.vue          ← Sidebar + Navbar + TagsView + <router-view>
  directive/
    permission/v-hasPermi   ← 按钮级权限指令
    permission/v-hasRole    ← 角色级权限指令
  views/
    login.vue               ← 登录页（验证码 + 记住密码加密）
    index.vue               ← 仪表盘首页（图书统计 + ECharts）
    system/                 ← 用户/角色/菜单/部门/岗位/字典/配置/通知
    monitor/                ← 在线用户/日志/定时任务/缓存/服务器/Druid
    biz/bizCategory/        ← 图书分类 CRUD
    tool/gen/               ← 代码生成器
```

## 约定

- 开发代理 `/dev-api` → `http://localhost:8080`，生产用 `/prod-api`
- 前端端口 80，自动打开浏览器
- 布局模式 `navType`: 1=纯侧边栏, 2=混合, 3=纯顶部；992px 断点切换移动端
- 字典数据通过 `dict.js` Pinia store 缓存，不用每次请求
