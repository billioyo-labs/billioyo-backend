package com.itemrental.rentalService.domain.admin.dto;

import com.itemrental.rentalService.domain.report.entity.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportListResponseDto {
    private Long id;
    private Report.TargetType targetType;
    private Long targetId;
    private Report.ReportReason reason;
    private String description;
    private LocalDateTime createdAt;
}
