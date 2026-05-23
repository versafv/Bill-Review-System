---
tags: [tech-note, frontend]
---

# Vue 3

## 版本

项目使用 Vue 3.5。

## 核心概念

- **组合式 API (Composition API)**: `setup()` / `<script setup>` 语法
- **响应式**: `ref()` / `reactive()` / `computed()` / `watch()`
- **生命周期**: `onMounted()`, `onUnmounted()`, `onUpdated()`
- **Teleport**: 将组件渲染到 DOM 其他位置
- **Suspense**: 异步组件加载

## vs Vue 2 (Ruoyi-Vue 旧版用 Vue 2)

| Vue 2 | Vue 3 |
|-------|-------|
| Options API | Composition API |
| `data()`, `methods` | `ref()`, `reactive()` |
| `v-model` 单值 | 多 `v-model` |
| Vuex | Pinia |
| Webpack | Vite |
| `this.$emit` | `defineEmits()` |

## 在项目中的应用

- 所有 `.vue` 文件使用 `<script setup>` 语法
- Element Plus 组件全局注册
- 自定义指令 (`directive/`) 用于权限控制
