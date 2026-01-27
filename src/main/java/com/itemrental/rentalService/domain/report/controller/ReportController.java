package com.itemrental.rentalService.domain.report.controller;

import com.itemrental.rentalService.domain.report.dto.ReportRequestDto;
import com.itemrental.rentalService.domain.report.service.ReportService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report", description = "신고 처리 API")
public class ReportController {

    private final ReportService reportService;

    @Operation(
        summary = "게시글 신고",
        description = "사용자가 신고 버튼을 클릭했을 때 호출되는 API입니다. 게시글 신고를 접수합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReport(@RequestBody ReportRequestDto dto) {
        ;
        reportService.createReport(dto);
        return ResponseEntity.ok(ApiResponse.success("게시글 신고 완료"));
    }
}
