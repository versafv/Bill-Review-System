package com.zsc.module.service.impl;

import com.zsc.module.common.exception.ServiceException;
import com.zsc.module.domain.dto.BizCategoryDto;
import com.zsc.module.domain.entity.BizCategory;
import com.zsc.module.mapper.BizCategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 单元测试 BizCategoryServiceImpl。lambdaQuery() 链需要 MyBatis 代理，无法使用 Mockito mock，
 * queryCategories 的覆盖由 BizCategoryServiceIntegrationTest 负责。
 */
@ExtendWith(MockitoExtension.class)
class BizCategoryServiceImplTest {

    @Mock
    private BizCategoryMapper categoryMapper;

    private BizCategoryServiceImpl categoryService;

    private BizCategoryDto dto;

    @BeforeEach
    void setUp() {
        categoryService = new BizCategoryServiceImpl();
        ReflectionTestUtils.setField(categoryService, "baseMapper", categoryMapper);

        dto = BizCategoryDto.builder()
                .categoryName("测试类别")
                .sortOrder(1)
                .status("0")
                .build();
    }

    // ==================== addCategory ====================

    @Test
    void addCategory_shouldSaveSuccessfully() {
        when(categoryMapper.insert(any(BizCategory.class))).thenReturn(1);

        categoryService.addCategory(dto);

        ArgumentCaptor<BizCategory> captor = ArgumentCaptor.forClass(BizCategory.class);
        verify(categoryMapper).insert(captor.capture());
        BizCategory saved = captor.getValue();
        assertThat(saved.getCategoryName()).isEqualTo("测试类别");
        assertThat(saved.getSortOrder()).isEqualTo(1);
        assertThat(saved.getStatus()).isEqualTo("0");
        assertThat(saved.getCreateTime()).isNotNull();
        assertThat(saved.getUpdateTime()).isNotNull();
    }

    @Test
    void addCategory_shouldThrowWhenInsertFails() {
        when(categoryMapper.insert(any(BizCategory.class))).thenReturn(0);

        assertThatThrownBy(() -> categoryService.addCategory(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("系统错误，业务类别添加失败！");
    }

    // ==================== updateCategory ====================

    @Test
    void updateCategory_shouldUpdateSuccessfully() {
        when(categoryMapper.updateById(any(BizCategory.class))).thenReturn(1);

        categoryService.updateCategory(dto);

        ArgumentCaptor<BizCategory> captor = ArgumentCaptor.forClass(BizCategory.class);
        verify(categoryMapper).updateById(captor.capture());
        BizCategory updated = captor.getValue();
        assertThat(updated.getCategoryName()).isEqualTo("测试类别");
        assertThat(updated.getUpdateTime()).isNotNull();
    }

    @Test
    void updateCategory_shouldThrowWhenUpdateFails() {
        when(categoryMapper.updateById(any(BizCategory.class))).thenReturn(0);

        assertThatThrownBy(() -> categoryService.updateCategory(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("系统错误，业务类别更新失败！");
    }
}
