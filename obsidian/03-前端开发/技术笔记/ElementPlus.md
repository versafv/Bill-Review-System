---
tags: [tech-note, frontend]
aliases: [Element, UI组件库, el-plus]
created: "2026-05-23"
---

# Element Plus

## 版本

项目使用 Element Plus 2.13。

## 核心概念

- 基于 Vue 3 的企业级 UI 组件库
- 提供 70+ 高质量组件
- 支持按需引入和全局引入
- TypeScript 支持

## 项目常用组件

| 组件 | 用途 |
|------|------|
| el-table | 数据表格 |
| el-form | 表单 |
| el-input | 输入框 |
| el-select | 下拉选择 |
| el-date-picker | 日期选择 |
| el-upload | 文件上传 |
| el-dialog | 弹窗对话框 |
| el-button | 按钮 |
| el-pagination | 分页 |
| el-tag | 标签 (状态显示) |
| el-descriptions | 描述列表 (详情页) |

## 图标

使用 `@element-plus/icons-vue`:

```vue
<script setup>
import { Edit, Delete, Search } from '@element-plus/icons-vue'
</script>
<template>
  <el-icon><Edit /></el-icon>
</template>
```

## 相关笔记

- [[../前端总览|前端总览]]
- [[Vue3]]
- [[Pinia]]
