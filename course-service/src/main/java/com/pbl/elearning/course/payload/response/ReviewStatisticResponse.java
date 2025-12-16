package com.pbl.elearning.course.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewStatisticResponse {
    private Double averageRating; // Trung bình (VD: 4.8)
    private Integer totalReviews; // Tổng số review

    // Số lượng review cho từng mức sao
    private Integer star1Count;
    private Integer star2Count;
    private Integer star3Count;
    private Integer star4Count;
    private Integer star5Count;

    // Phần trăm (Frontend có thể tự tính, nhưng Backend trả về luôn cho tiện)
    private Double star1Percent;
    private Double star2Percent;
    private Double star3Percent;
    private Double star4Percent;
    private Double star5Percent;
}