package com.zsc.module.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 审核员仪表盘统计数据
 */
@Data
public class ReviewerStatsVo {

    /** 待审核票据数量 */
    private int pendingCount;

    /** 今日通过数量 */
    private int todayApproved;

    /** 今日退回数量 */
    private int todayRejected;

    /** 审核通过率（百分比 0-100） */
    private double approvalRate;

    /** 积压预警：超过3天未审核的票据数 */
    private int stalePending;

    /** 最近5条审核记录 */
    private List<BizBillVo> recentReviews;
}
