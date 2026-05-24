package com.zsc.module.service.impl;

import com.zsc.module.BaseServiceTest;
import com.zsc.module.common.pagination.PageResult;
import com.zsc.module.domain.dto.BizCategoryDto;
import com.zsc.module.domain.dto.query.BizCategoryQueryDto;
import com.zsc.module.domain.entity.BizCategory;
import com.zsc.module.service.BizCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@BaseServiceTest
class BizCategoryServiceIntegrationTest {

    @Autowired
    private BizCategoryService categoryService;

    @Test
    void shouldAddAndQueryCategory() {
        BizCategoryDto dto = BizCategoryDto.builder()
                .categoryName("集成测试类别")
                .sortOrder(10)
                .status("0")
                .build();
        categoryService.addCategory(dto);

        BizCategoryQueryDto queryDto = new BizCategoryQueryDto();
        queryDto.setCategoryName("集成测试类别");
        PageResult result = categoryService.queryCategories(queryDto);

        assertThat(result.getTotal()).isEqualTo(1);
        List list = result.getList();
        BizCategory cat = (BizCategory) list.get(0);
        assertThat(cat.getCategoryName()).isEqualTo("集成测试类别");
        assertThat(cat.getSortOrder()).isEqualTo(10);
        assertThat(cat.getStatus()).isEqualTo("0");
    }

    @Test
    void shouldUpdateCategory() {
        BizCategoryDto addDto = BizCategoryDto.builder()
                .categoryName("更新前")
                .sortOrder(1)
                .status("0")
                .build();
        categoryService.addCategory(addDto);

        BizCategoryQueryDto queryDto = new BizCategoryQueryDto();
        queryDto.setCategoryName("更新前");
        PageResult result = categoryService.queryCategories(queryDto);
        BizCategory cat = (BizCategory) result.getList().get(0);
        Long categoryId = cat.getCategoryId();

        BizCategoryDto updateDto = BizCategoryDto.builder()
                .categoryId(categoryId)
                .categoryName("更新后")
                .sortOrder(99)
                .status("1")
                .build();
        categoryService.updateCategory(updateDto);

        BizCategoryQueryDto queryDto2 = new BizCategoryQueryDto();
        queryDto2.setCategoryName("更新后");
        PageResult result2 = categoryService.queryCategories(queryDto2);
        assertThat(result2.getList()).hasSize(1);
        BizCategory updated = (BizCategory) result2.getList().get(0);
        assertThat(updated.getCategoryName()).isEqualTo("更新后");
        assertThat(updated.getSortOrder()).isEqualTo(99);
        assertThat(updated.getStatus()).isEqualTo("1");
    }

    @Test
    void shouldPaginateCategories() {
        BizCategoryQueryDto queryDto = new BizCategoryQueryDto();
        PageResult result = categoryService.queryCategories(queryDto);

        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotal()).isGreaterThanOrEqualTo(3);
    }
}
