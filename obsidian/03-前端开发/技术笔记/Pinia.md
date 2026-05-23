---
tags: [tech-note, frontend]
---

# Pinia

## 版本

项目使用 Pinia 3（Vue 官方状态管理库）。

## 核心概念

- **Store**: 状态容器，类似 Vuex 的 Module
- **State**: 响应式数据
- **Getters**: 计算属性
- **Actions**: 修改 state 的方法（支持异步）

## vs Vuex

| Vuex | Pinia |
|------|-------|
| mutations + actions | 只有 actions |
| 复杂的模块嵌套 | 扁平 store 结构 |
| `this.$store` | `useXxxStore()` |
| TypeScript 支持弱 | 完整 TS 支持 |

## 在项目中的应用

```javascript
// store/modules/user.js
export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: {},
    roles: [],
    permissions: []
  }),
  actions: {
    async login(loginForm) {
      // 调用登录 API → 存储 token
    },
    async getUserInfo() {
      // 获取用户信息 → 存储 roles/permissions
    }
  }
})
```

## 相关笔记

- [[前端总览]]
- [[路由设计]]
