package com.itemrental.rentalService.user.dto;


import com.itemrental.rentalService.user.entity.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {
  private Report.TargetType targetType;
  private Long targetId;
  private Report.ReportReason reason;
  private String description;

}
