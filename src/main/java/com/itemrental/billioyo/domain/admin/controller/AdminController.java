package com.itemrental.billioyo.domain.admin.controller;

import com.itemrental.billioyo.domain.admin.dto.ReportListResponseDto;
import com.itemrental.billioyo.domain.admin.service.AdminService;
import com.itemrental.billioyo.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    // =====================
    // 유저 정지
    // =====================
    @Operation(
        summary = "유저 정지",
        description = "이메일(email)을 기준으로 유저를 정지 처리합니다."
    )
    @PostMapping("/users/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@RequestParam String email) {
        String message = adminService.banUser(email);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // =====================
    // 신고 목록 조회 (페이징)
    // =====================
    @Operation(
        summary = "신고 목록 조회",
        description =
            "신고된 게시글/댓글 목록을 페이징 조회합니다." + "예시:" + "- /admin/reports?page=0&size=10&sort=createdAt,desc"
    )
    @GetMapping("/reports")
    public ResponseEntity<Page<ReportListResponseDto>> getReports(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminService.getReportList(pageable));
    }

    // =====================
    // 커뮤니티 게시글 삭제 (관리자)
    // =====================
    @Operation(
        summary = "커뮤니티 게시글 삭제(관리자)",
        description = "관리자 권한으로 커뮤니티 게시글을 삭제합니다"
    )
    @DeleteMapping("/community/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunityPost(@PathVariable Long postId) {
        adminService.adminDeleteCommunityPost(postId);
        return ResponseEntity.ok(ApiResponse.success("커뮤니티 게시글 삭제 완료"));
    }


    @Operation(
        summary = "렌탈 게시글 삭제(관리자)",
        description = "관리자 권한으로 렌탈 게시글을 삭제합니다."
    )
    @DeleteMapping("/rental/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteRentalPost(@PathVariable Long postId) {
        adminService.adminDeleteRentalPost(postId);
        return ResponseEntity.ok(ApiResponse.success("렌탈 게시글 삭제 완료"));
    }
}
