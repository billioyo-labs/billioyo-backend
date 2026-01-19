package com.itemrental.rentalService.domain.admin.controller;

import com.itemrental.rentalService.domain.admin.dto.ReportListResponseDto;
import com.itemrental.rentalService.domain.admin.service.AdminService;
import com.itemrental.rentalService.global.common.ApiResponse;
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

    // POST /admin/users/ban?email=xxx@xxx.com
    @PostMapping("/users/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@RequestParam String email) {
        String message = adminService.banUser(email);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // GET /admin/reports?page=0&size=10&sort=createdAt,desc
    @GetMapping("/reports")
    public ResponseEntity<Page<ReportListResponseDto>> getReports(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminService.getReportList(pageable));
    }

    // DELETE /admin/community-posts/{postId}
    @DeleteMapping("/community/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunityPost(@PathVariable Long postId) {
        adminService.adminDeleteCommunityPost(postId);
        return ResponseEntity.ok(ApiResponse.success("커뮤니티 게시글 삭제 완료"));
    }


    // DELETE /admin/rental-posts/{postId}
    @DeleteMapping("/rental/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteRentalPost(@PathVariable Long postId) {
        adminService.adminDeleteRentalPost(postId);
        return ResponseEntity.ok(ApiResponse.success("렌탈 게시글 삭제 완료"));
    }
}
