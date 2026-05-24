package com.zsc.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsc.common.utils.SecurityUtils;
import com.zsc.module.common.exception.ServiceException;
import com.zsc.module.common.pagination.PageResult;
import com.zsc.module.domain.dto.BizBillDto;
import com.zsc.module.domain.dto.BizBillReviewDto;
import com.zsc.module.domain.dto.query.BizBillQueryDto;
import com.zsc.module.domain.entity.BizAuditLog;
import com.zsc.module.domain.entity.BizBill;
import com.zsc.module.domain.entity.BizBillFile;
import com.zsc.module.domain.entity.BizCategory;
import com.zsc.module.domain.vo.BizBillDetailVo;
import com.zsc.module.domain.vo.BizBillVo;
import com.zsc.module.mapper.BizAuditLogMapper;
import com.zsc.module.mapper.BizBillFileMapper;
import com.zsc.module.mapper.BizBillMapper;
import com.zsc.module.mapper.BizCategoryMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BizBillServiceImplTest {

    @Mock
    private BizBillMapper billMapper;
    @Mock
    private BizBillFileMapper fileMapper;
    @Mock
    private BizAuditLogMapper auditLogMapper;
    @Mock
    private BizCategoryMapper categoryMapper;

    private BizBillServiceImpl billService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
        billService = new BizBillServiceImpl();
        ReflectionTestUtils.setField(billService, "baseMapper", billMapper);
        ReflectionTestUtils.setField(billService, "fileMapper", fileMapper);
        ReflectionTestUtils.setField(billService, "auditLogMapper", auditLogMapper);
        ReflectionTestUtils.setField(billService, "categoryMapper", categoryMapper);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    // ==================== addBill ====================

    @Test
    void addBill_shouldSaveDraftWithTemporaryNo() {
        stubNonAdminUser("user1");
        when(billMapper.insert(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("测试票据", null, BigDecimal.valueOf(100), "0", null);
        billService.addBill(dto);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).insert(captor.capture());
        BizBill saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("测试票据");
        assertThat(saved.getStatus()).isEqualTo("0");
        assertThat(saved.getBillNo()).startsWith("DRAFT-");
        assertThat(saved.getCreateBy()).isEqualTo("user1");
    }

    @Test
    void addBill_shouldGenerateFormalNoWhenSubmitted() {
        stubNonAdminUser("user1");
        // generateBillNo: no existing bill today — getOne may call selectOne or selectList
        when(billMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        when(billMapper.insert(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("提交票据", 1L, BigDecimal.valueOf(200), "1", null);
        billService.addBill(dto);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).insert(captor.capture());
        assertThat(captor.getValue().getBillNo()).matches("BILL-\\d{8}-0001");
    }

    @Test
    void addBill_shouldThrowWhenAdmin() {
        securityUtilsMock.when(() -> SecurityUtils.hasPermi("biz:bill:review")).thenReturn(true);

        BizBillDto dto = buildBillDto("测试", null, BigDecimal.valueOf(100), "0", null);
        assertThatThrownBy(() -> billService.addBill(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("管理员不能执行用户操作！");
        verify(billMapper, never()).insert(org.mockito.Mockito.<BizBill>any());
    }

    @Test
    void addBill_shouldThrowWhenInsertFails() {
        stubNonAdminUser("user1");
        when(billMapper.insert(any(BizBill.class))).thenReturn(0);

        BizBillDto dto = buildBillDto("测试", null, BigDecimal.valueOf(100), "0", null);
        assertThatThrownBy(() -> billService.addBill(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("系统错误，票据保存失败！");
    }

    @Test
    void addBill_shouldSaveAttachments() {
        stubNonAdminUser("user1");
        when(billMapper.insert(any(BizBill.class))).thenReturn(1);
        when(fileMapper.insert(any(BizBillFile.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("带附件票据", null, BigDecimal.valueOf(100), "0", "/upload/a.pdf,/upload/b.pdf");
        billService.addBill(dto);

        ArgumentCaptor<BizBillFile> fileCaptor = ArgumentCaptor.forClass(BizBillFile.class);
        verify(fileMapper, times(2)).insert(fileCaptor.capture());
        List<BizBillFile> files = fileCaptor.getAllValues();
        assertThat(files.get(0).getFileName()).isEqualTo("a.pdf");
        assertThat(files.get(0).getSortOrder()).isEqualTo(0);
        assertThat(files.get(1).getFileName()).isEqualTo("b.pdf");
        assertThat(files.get(1).getSortOrder()).isEqualTo(1);
    }

    @Test
    void addBill_shouldSkipBlankAttachment() {
        stubNonAdminUser("user1");
        when(billMapper.insert(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("无附件", null, BigDecimal.valueOf(100), "0", null);
        billService.addBill(dto);

        verify(fileMapper, never()).insert(org.mockito.Mockito.<BizBillFile>any());
    }

    // ==================== queryBills ====================

    @Test
    void queryBills_shouldLimitToOwnBillsForNonAdmin() {
        stubNonAdminUser("user2");
        Page<BizBill> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        when(billMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        billService.queryBills(new BizBillQueryDto());

        verify(billMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void queryBills_shouldReturnAllForAdmin() {
        securityUtilsMock.when(() -> SecurityUtils.hasPermi("biz:bill:review")).thenReturn(true);

        BizBill bill = buildBill(1L, "BILL-001", "办公采购", 1L, BigDecimal.valueOf(100), "1", "user1");
        Page<BizBill> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(bill));
        page.setTotal(1);
        when(billMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        BizCategory cat = new BizCategory();
        cat.setCategoryId(1L);
        cat.setCategoryName("办公用品");
        when(categoryMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(cat));

        PageResult<BizBillVo> result = billService.queryBills(new BizBillQueryDto());

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getList().get(0).getCategoryName()).isEqualTo("办公用品");
    }

    @Test
    void queryBills_shouldReturnEmptyWhenNoRecords() {
        securityUtilsMock.when(() -> SecurityUtils.hasPermi("biz:bill:review")).thenReturn(true);
        Page<BizBill> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        when(billMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        PageResult<BizBillVo> result = billService.queryBills(new BizBillQueryDto());

        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getList()).isEmpty();
    }

    // ==================== getBillDetail ====================

    @Test
    void getBillDetail_shouldReturnDetailForOwner() {
        stubNonAdminUser("user1");
        BizBill bill = buildBill(1L, "BILL-001", "办公采购", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(bill);
        // No attachments, no audit logs
        when(fileMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(auditLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        BizBillDetailVo detail = billService.getBillDetail(1L);

        assertThat(detail.getTitle()).isEqualTo("办公采购");
        assertThat(detail.getFiles()).isEmpty();
        assertThat(detail.getAuditLogs()).isEmpty();
    }

    @Test
    void getBillDetail_shouldAllowAdminToViewAny() {
        securityUtilsMock.when(() -> SecurityUtils.hasPermi("biz:bill:review")).thenReturn(true);
        // Admin does not call getUsername() in the permission check path
        BizBill bill = buildBill(2L, "BILL-002", "他人票据", 1L, BigDecimal.valueOf(200), "1", "otherUser");
        when(billMapper.selectById(2L)).thenReturn(bill);
        when(fileMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(auditLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        BizBillDetailVo detail = billService.getBillDetail(2L);

        assertThat(detail.getTitle()).isEqualTo("他人票据");
    }

    @Test
    void getBillDetail_shouldThrowWhenNotOwnerAndNotAdmin() {
        stubNonAdminUser("user2");
        BizBill bill = buildBill(1L, "BILL-001", "他人票据", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(bill);

        assertThatThrownBy(() -> billService.getBillDetail(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能查看自己的票据！");
    }

    @Test
    void getBillDetail_shouldThrowWhenBillNotFound() {
        when(billMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> billService.getBillDetail(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("票据不存在！");
    }

    @Test
    void getBillDetail_shouldIncludeFilesAndAuditLogs() {
        stubNonAdminUser("user1");
        BizBill bill = buildBill(1L, "BILL-001", "办公采购", 1L, BigDecimal.valueOf(100), "2", "user1");
        bill.setAuditBy("admin");
        bill.setAuditComment("同意");
        when(billMapper.selectById(1L)).thenReturn(bill);

        BizBillFile file = BizBillFile.builder().id(10L).billId(1L).fileName("发票.pdf").filePath("/upload/发票.pdf").sortOrder(0).build();
        when(fileMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(file));

        BizAuditLog log = BizAuditLog.builder().id(100L).billId(1L).action("1").comment("同意").auditBy("admin").build();
        when(auditLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(log));

        BizBillDetailVo detail = billService.getBillDetail(1L);

        assertThat(detail.getFiles()).hasSize(1);
        assertThat(detail.getFiles().get(0).getFileName()).isEqualTo("发票.pdf");
        assertThat(detail.getAuditLogs()).hasSize(1);
        assertThat(detail.getAuditLogs().get(0).getAction()).isEqualTo("1");
    }

    // ==================== updateBill ====================

    @Test
    void updateBill_shouldUpdateDraftSuccessfully() {
        stubNonAdminUser("user1");
        BizBill existing = buildBill(1L, "DRAFT-001", "旧标题", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(existing);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("新标题", 2L, BigDecimal.valueOf(200), "0", null);
        dto.setId(1L);
        billService.updateBill(dto);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).updateById(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("新标题");
        assertThat(captor.getValue().getCategoryId()).isEqualTo(2L);
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void updateBill_shouldUpdateReturnedStatus() {
        stubNonAdminUser("user1");
        BizBill existing = buildBill(1L, "BILL-001", "旧标题", 1L, BigDecimal.valueOf(100), "3", "user1");
        when(billMapper.selectById(1L)).thenReturn(existing);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("修改后退回重交", 1L, BigDecimal.valueOf(150), "0", null);
        dto.setId(1L);
        billService.updateBill(dto);

        verify(billMapper).updateById(any(BizBill.class));
    }

    @Test
    void updateBill_shouldThrowWhenSubmittedStatus() {
        stubNonAdminUser("user1");
        BizBill existing = buildBill(1L, "BILL-001", "已提交的", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(existing);

        BizBillDto dto = buildBillDto("不能编辑", 1L, BigDecimal.valueOf(150), "0", null);
        dto.setId(1L);
        assertThatThrownBy(() -> billService.updateBill(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能编辑草稿或已退回状态的票据！");
    }

    @Test
    void updateBill_shouldThrowWhenNotOwner() {
        stubNonAdminUser("user2");
        BizBill existing = buildBill(1L, "DRAFT-001", "别人的草稿", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(existing);

        BizBillDto dto = buildBillDto("想改别人", 1L, BigDecimal.valueOf(150), "0", null);
        dto.setId(1L);
        assertThatThrownBy(() -> billService.updateBill(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能编辑自己的票据！");
    }

    @Test
    void updateBill_shouldThrowWhenBillNotFound() {
        stubNonAdminUser("user1");
        when(billMapper.selectById(999L)).thenReturn(null);

        BizBillDto dto = buildBillDto("不存在", 1L, BigDecimal.valueOf(100), "0", null);
        dto.setId(999L);
        assertThatThrownBy(() -> billService.updateBill(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("票据不存在！");
    }

    @Test
    void updateBill_shouldUpdateAttachmentsWhenProvided() {
        stubNonAdminUser("user1");
        BizBill existing = buildBill(1L, "DRAFT-001", "有附件", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(existing);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);
        when(fileMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(fileMapper.insert(any(BizBillFile.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("新附件", 1L, BigDecimal.valueOf(100), "0", "/upload/new.pdf");
        dto.setId(1L);
        billService.updateBill(dto);

        verify(fileMapper).delete(any(LambdaQueryWrapper.class));
        verify(fileMapper).insert(any(BizBillFile.class));
    }

    // ==================== submitBill ====================

    @Test
    void submitBill_shouldSubmitDraftAndReplaceTempNo() {
        stubNonAdminUser("user1");
        BizBill draft = buildBill(1L, "DRAFT-1234567890", "草稿标题", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(draft);
        when(billMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);

        billService.submitBill(1L);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("1");
        assertThat(captor.getValue().getBillNo()).matches("BILL-\\d{8}-0001");
    }

    @Test
    void submitBill_shouldSubmitReturnedBill() {
        stubNonAdminUser("user1");
        BizBill returned = buildBill(1L, "BILL-20260524-0001", "退回票据", 1L, BigDecimal.valueOf(100), "3", "user1");
        when(billMapper.selectById(1L)).thenReturn(returned);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);

        billService.submitBill(1L);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("1");
        // BillNo should remain (not DRAFT-)
        assertThat(captor.getValue().getBillNo()).isEqualTo("BILL-20260524-0001");
    }

    @Test
    void submitBill_shouldThrowWhenAlreadySubmitted() {
        stubNonAdminUser("user1");
        BizBill submitted = buildBill(1L, "BILL-001", "已提交", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(submitted);

        assertThatThrownBy(() -> billService.submitBill(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能提交草稿或已退回状态的票据！");
    }

    @Test
    void submitBill_shouldThrowWhenNotOwner() {
        stubNonAdminUser("user2");
        BizBill draft = buildBill(1L, "DRAFT-001", "别人草稿", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(draft);

        assertThatThrownBy(() -> billService.submitBill(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能提交自己的票据！");
    }

    // ==================== deleteBill ====================

    @Test
    void deleteBill_shouldDeleteDraftAndFiles() {
        stubNonAdminUser("user1");
        BizBill draft = buildBill(1L, "DRAFT-001", "草稿", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(draft);
        when(fileMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(billMapper.deleteById(1L)).thenReturn(1);

        billService.deleteBill(1L);

        verify(fileMapper).delete(any(LambdaQueryWrapper.class));
        verify(billMapper).deleteById(1L);
    }

    @Test
    void deleteBill_shouldThrowWhenSubmittedStatus() {
        stubNonAdminUser("user1");
        BizBill submitted = buildBill(1L, "BILL-001", "已提交", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(submitted);

        assertThatThrownBy(() -> billService.deleteBill(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能删除草稿状态的票据！");
    }

    @Test
    void deleteBill_shouldThrowWhenNotOwner() {
        stubNonAdminUser("user2");
        BizBill draft = buildBill(1L, "DRAFT-001", "别人草稿", 1L, BigDecimal.valueOf(100), "0", "user1");
        when(billMapper.selectById(1L)).thenReturn(draft);

        assertThatThrownBy(() -> billService.deleteBill(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能删除自己的票据！");
    }

    @Test
    void deleteBill_shouldThrowWhenBillNotFound() {
        stubNonAdminUser("user1");
        when(billMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> billService.deleteBill(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessage("票据不存在！");
    }

    // ==================== reviewBill ====================

    @Test
    void reviewBill_shouldApproveSuccessfully() {
        securityUtilsMock.when(() -> SecurityUtils.getUsername()).thenReturn("admin");
        BizBill submitted = buildBill(1L, "BILL-001", "待审票据", 1L, BigDecimal.valueOf(100), "1", "user1");
        when(billMapper.selectById(1L)).thenReturn(submitted);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);
        when(auditLogMapper.insert(any(BizAuditLog.class))).thenReturn(1);

        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(1L).action("1").comment("同意").build();
        billService.reviewBill(reviewDto);

        ArgumentCaptor<BizBill> billCaptor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).updateById(billCaptor.capture());
        assertThat(billCaptor.getValue().getStatus()).isEqualTo("2");
        assertThat(billCaptor.getValue().getAuditBy()).isEqualTo("admin");

        ArgumentCaptor<BizAuditLog> logCaptor = ArgumentCaptor.forClass(BizAuditLog.class);
        verify(auditLogMapper).insert(logCaptor.capture());
        assertThat(logCaptor.getValue().getAction()).isEqualTo("1");
        assertThat(logCaptor.getValue().getComment()).isEqualTo("同意");
    }

    @Test
    void reviewBill_shouldReturnSuccessfully() {
        securityUtilsMock.when(() -> SecurityUtils.getUsername()).thenReturn("admin");
        BizBill submitted = buildBill(2L, "BILL-002", "退回票据", 2L, BigDecimal.valueOf(200), "1", "user1");
        when(billMapper.selectById(2L)).thenReturn(submitted);
        when(billMapper.updateById(any(BizBill.class))).thenReturn(1);
        when(auditLogMapper.insert(any(BizAuditLog.class))).thenReturn(1);

        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(2L).action("2").comment("请补充说明").build();
        billService.reviewBill(reviewDto);

        ArgumentCaptor<BizBill> billCaptor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).updateById(billCaptor.capture());
        assertThat(billCaptor.getValue().getStatus()).isEqualTo("3");
    }

    @Test
    void reviewBill_shouldThrowWhenInvalidAction() {
        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(1L).action("5").comment("无效").build();

        assertThatThrownBy(() -> billService.reviewBill(reviewDto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("无效的审核结果，action 必须为 1（通过）或 2（退回）！");
    }

    @Test
    void reviewBill_shouldThrowWhenNotSubmittedStatus() {
        BizBill approved = buildBill(1L, "BILL-001", "已通过的", 1L, BigDecimal.valueOf(100), "2", "user1");
        when(billMapper.selectById(1L)).thenReturn(approved);

        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(1L).action("1").comment("重复审核").build();
        assertThatThrownBy(() -> billService.reviewBill(reviewDto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("只能审核已提交状态的票据！");
    }

    @Test
    void reviewBill_shouldThrowWhenBillNotFound() {
        when(billMapper.selectById(999L)).thenReturn(null);

        BizBillReviewDto reviewDto = BizBillReviewDto.builder()
                .billId(999L).action("1").comment("不存在").build();
        assertThatThrownBy(() -> billService.reviewBill(reviewDto))
                .isInstanceOf(ServiceException.class)
                .hasMessage("票据不存在！");
    }

    // ==================== generateBillNo (via addBill) ====================

    @Test
    void generateBillNo_shouldIncrementSequence() {
        stubNonAdminUser("user1");
        // Simulate existing bill today with seq 0005
        BizBill existingBill = new BizBill();
        existingBill.setBillNo("BILL-20260524-0005");
        when(billMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existingBill);
        when(billMapper.insert(any(BizBill.class))).thenReturn(1);

        BizBillDto dto = buildBillDto("测试序号", null, BigDecimal.valueOf(100), "1", null);
        billService.addBill(dto);

        ArgumentCaptor<BizBill> captor = ArgumentCaptor.forClass(BizBill.class);
        verify(billMapper).insert(captor.capture());
        assertThat(captor.getValue().getBillNo()).matches("BILL-\\d{8}-0006");
    }

    // ==================== helpers ====================

    private void stubNonAdminUser(String username) {
        securityUtilsMock.when(() -> SecurityUtils.hasPermi("biz:bill:review")).thenReturn(false);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn(username);
    }

    private BizBillDto buildBillDto(String title, Long categoryId, BigDecimal amount, String status, String attachment) {
        return BizBillDto.builder()
                .title(title).categoryId(categoryId).amount(amount)
                .status(status).attachment(attachment).build();
    }

    private BizBill buildBill(Long id, String billNo, String title, Long categoryId, BigDecimal amount, String status, String createBy) {
        return BizBill.builder()
                .id(id).billNo(billNo).title(title).categoryId(categoryId)
                .amount(amount).status(status).createBy(createBy).build();
    }
}
