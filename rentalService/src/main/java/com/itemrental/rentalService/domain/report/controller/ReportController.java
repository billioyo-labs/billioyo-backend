package com.itemrental.rentalService.domain.report.controller;

import com.itemrental.rentalService.domain.report.dto.ReportRequestDto;
import com.itemrental.rentalService.domain.report.service.ReportService;
import com.itemrental.rentalService.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/report")
public class ReportController {

  private final ReportService reportService;

  // POST /reports
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createReport(@RequestBody ReportRequestDto dto) {;
    reportService.createReport(dto);
    return ResponseEntity.ok(ApiResponse.success("게시글 신고 완료"));
  }
}
