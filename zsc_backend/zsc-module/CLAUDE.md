# zsc-module — 图书分类业务

自定义业务模块，当前仅有 `BizCategory`（图书分类）CRUD。作为模块开发参考模板。

## 特点（与系统模块的区别）

- **独立的响应体系**：`ResultVo<T>` / `PageResult` / `BasePageReq`（`common/response/` `common/pagination/`）
- **DTO/VO 分离**：`domain/dto/`（入参）+ `domain/vo/`（出参）+ `domain/entity/`（数据库映射）
- **纯 MyBatis-Plus**：无 XML 文件，全用 `lambdaQuery()` 链式查询
- **SpringDoc**：Controller 用 `@Tag` / `@Operation` 注解
- **RESTful**：分页用 `POST /query`，其余 `GET/POST/PUT/DELETE`

## 包结构

```
controller/   ← BizCategoryController（路径 /api/category）
domain/
  entity/     ← BizCategory（@TableId 主键）
  dto/        ← BizCategoryDto, query/BizCategoryQueryDto（@Valid 校验）
  vo/         ← BizCategoryVo
mapper/       ← BizCategoryMapper
service/      ← BizCategoryService + impl
common/       ← ResultVo, PageResult, GlobalCodeEnum, ServiceException, BaseEnum, EnumUtil
```

## 关键约定

- 权限前缀统一 `biz:category:*`（如 `biz:category:list`, `biz:category:add`）
- ServiceImpl 继承 MyBatis-Plus `ServiceImpl<M, T>`
- Dto 用 Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
- 注意：`ResultVo` 和系统的 `AjaxResult` 是两套体系，别混用
