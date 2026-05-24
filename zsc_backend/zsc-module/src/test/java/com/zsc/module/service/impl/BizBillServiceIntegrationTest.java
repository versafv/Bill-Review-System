package com.zsc.module.service.impl;

import com.zsc.common.core.domain.model.LoginUser;
import com.zsc.module.BaseServiceTest;
import com.zsc.module.common.pagination.PageResult;
import com.zsc.module.domain.dto.BizBillDto;
import com.zsc.module.domain.dto.BizBillReviewDto;
import com.zsc.module.domain.dto.query.BizBillQueryDto;
import com.zsc.module.domain.vo.BizBillDetailVo;
import com.zsc.module.domain.vo.BizBillVo;
import com.zsc.module.service.BizBillService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@BaseServiceTest
class BizBillServiceIntegrationTest {

    @Autowired
    private BizBillService billService;

    private Authentication auth;
    private LoginUser loginUser;

    @BeforeEach
    void setUp() {
        auth = mock(Authentication.class);
        loginUser = mock(LoginUser.class);
        when(auth.getPrincipal()).thenReturn(loginUser);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void loginAsUser(String username) {
        when(loginUser.getUsername()).thenReturn(username);
        when(loginUser.getPermissions()).thenReturn(Set.of("biz:bill:list"));
    }

    private void loginAsAdmin() {
        when(loginUser.getUsername()).thenReturn("admin");
        when(loginUser.getPermissions()).thenReturn(Set.of("biz:bill:list", "biz:bill:review"));
    }

    // ==================== 草稿流程 ====================

    @Test
    void shouldSaveDraft() {
        loginAsUser("user1");

        BizBillDto dto = BizBillDto.builder()
                .title("集成测试草稿")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(500))
                .description("测试描述")
                .status("0")
                .build();
        billService.addBill(dto);

        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("0");
        PageResult<BizBillVo> result = billService.queryBills(queryDto);

        assertThat(result.getTotal()).isGreaterThanOrEqualTo(1);
        BizBillVo vo = result.getList().get(0);
        assertThat(vo.getTitle()).isEqualTo("集成测试草稿");
        assertThat(vo.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(vo.getStatus()).isEqualTo("0");
        assertThat(vo.getBillNo()).startsWith("DRAFT-");
    }

    // ==================== 提交流程 ====================

    @Test
    void shouldSubmitDraftWithFormalBillNo() {
        loginAsUser("user1");

        BizBillDto dto = BizBillDto.builder()
                .title("直接提交")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(300))
                .status("1")
                .build();
        billService.addBill(dto);

        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("1");
        PageResult<BizBillVo> result = billService.queryBills(queryDto);

        assertThat(result.getTotal()).isGreaterThanOrEqualTo(1);
        BizBillVo vo = result.getList().get(0);
        assertThat(vo.getBillNo()).matches("BILL-\\d{8}-\\d{4}");
        assertThat(vo.getStatus()).isEqualTo("1");
    }

    // ==================== 草稿→编辑→提交→查看详情 ====================

    @Test
    void shouldFullDraftEditSubmitViewFlow() {
        loginAsUser("user1");

        // 1. Save draft
        BizBillDto draftDto = BizBillDto.builder()
                .title("完整流程测试")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(1000))
                .status("0")
                .attachment("/upload/test1.pdf,/upload/test2.pdf")
                .build();
        billService.addBill(draftDto);

        // 2. Query draft to get ID
        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("0");
        PageResult<BizBillVo> drafts = billService.queryBills(queryDto);
        Long billId = drafts.getList().get(0).getId();

        // 3. Edit draft
        BizBillDto editDto = BizBillDto.builder()
                .id(billId)
                .title("完整流程测试-已修改")
                .categoryId(2L)
                .amount(BigDecimal.valueOf(1200))
                .status("0")
                .build();
        billService.updateBill(editDto);

        // 4. Submit
        billService.submitBill(billId);

        // 5. View detail
        BizBillDetailVo detail = billService.getBillDetail(billId);
        assertThat(detail.getTitle()).isEqualTo("完整流程测试-已修改");
        assertThat(detail.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1200));
        assertThat(detail.getCategoryId()).isEqualTo(2L);
        assertThat(detail.getStatus()).isEqualTo("1");
        assertThat(detail.getFiles()).hasSize(2);
    }

    // ==================== 审核通过 ====================

    @Test
    void shouldApproveBill() {
        loginAsUser("user1");

        // Submit a bill first
        BizBillDto dto = BizBillDto.builder()
                .title("待审核通过")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(700))
                .status("1")
                .build();
        billService.addBill(dto);

        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("1");
        Long billId = billService.queryBills(queryDto).getList().get(0).getId();

        // Admin reviews
        loginAsAdmin();
        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(billId).action("1").comment("同意报销").build();
        billService.reviewBill(reviewDto);

        // Verify approved
        BizBillDetailVo detail = billService.getBillDetail(billId);
        assertThat(detail.getStatus()).isEqualTo("2");
        assertThat(detail.getAuditComment()).isEqualTo("同意报销");
        assertThat(detail.getAuditLogs()).hasSize(1);
        assertThat(detail.getAuditLogs().get(0).getAction()).isEqualTo("1");
    }

    // ==================== 审核退回 ====================

    @Test
    void shouldReturnBill() {
        loginAsUser("user1");

        BizBillDto dto = BizBillDto.builder()
                .title("待审核退回")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(800))
                .status("1")
                .build();
        billService.addBill(dto);

        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("1");
        Long billId = billService.queryBills(queryDto).getList().get(0).getId();

        loginAsAdmin();
        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(billId).action("2").comment("金额不合理，请核实").build();
        billService.reviewBill(reviewDto);

        BizBillDetailVo detail = billService.getBillDetail(billId);
        assertThat(detail.getStatus()).isEqualTo("3");
        assertThat(detail.getAuditComment()).isEqualTo("金额不合理，请核实");
    }

    // ==================== 退回→重编→重提→通过 ====================

    @Test
    void shouldReturnEditResubmitApprove() {
        loginAsUser("user1");

        // Submit
        BizBillDto dto = BizBillDto.builder()
                .title("退回重提测试")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(500))
                .status("1")
                .build();
        billService.addBill(dto);

        BizBillQueryDto queryDto = new BizBillQueryDto();
        queryDto.setStatus("1");
        Long billId = billService.queryBills(queryDto).getList().get(0).getId();

        // Admin returns
        loginAsAdmin();
        billService.reviewBill(BizBillReviewDto.builder()
                .billId(billId).action("2").comment("请修改").build());

        // User edits and resubmits
        loginAsUser("user1");
        BizBillDto editDto = BizBillDto.builder()
                .id(billId).title("退回重提测试-已修正")
                .categoryId(2L).amount(BigDecimal.valueOf(600)).status("0").build();
        billService.updateBill(editDto);
        billService.submitBill(billId);

        // Admin approves
        loginAsAdmin();
        billService.reviewBill(BizBillReviewDto.builder()
                .billId(billId).action("1").comment("修正后同意").build());

        BizBillDetailVo detail = billService.getBillDetail(billId);
        assertThat(detail.getStatus()).isEqualTo("2");
        assertThat(detail.getTitle()).isEqualTo("退回重提测试-已修正");
        assertThat(detail.getAuditLogs()).hasSize(2); // rejected + approved
    }

    // ==================== 权限验证 ====================

    @Test
    void shouldDenyAdminFromUserOperations() {
        loginAsAdmin();

        BizBillDto dto = BizBillDto.builder()
                .title("管理员不能创建")
                .categoryId(1L)
                .amount(BigDecimal.valueOf(100))
                .status("0")
                .build();

        assertThatThrownBy(() -> billService.addBill(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("管理员不能执行用户操作");
    }
}
