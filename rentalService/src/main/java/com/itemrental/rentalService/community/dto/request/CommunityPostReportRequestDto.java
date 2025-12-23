package com.itemrental.rentalService.community.dto.request;

import com.itemrental.rentalService.community.enums.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommunityPostReportRequestDto {
  private ReportReason reason;
  private String description;

}
