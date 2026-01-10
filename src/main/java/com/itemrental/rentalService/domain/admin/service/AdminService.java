package com.itemrental.rentalService.domain.admin.service;


import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.admin.dto.ReportListResponseDto;
import com.itemrental.rentalService.domain.report.entity.Report;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.report.repository.ReportRepository;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

  @Value("${admin.signup-secret}")
  private String adminSignupSecret;

  private final UserRepository userRepository;
  private final ReportRepository reportRepository;
  private final PostRepository postRepository;
  private final CommunityPostRepository communityPostRepository;

  //관리자 유저 차단
  public String banUser(String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

    if (user.getUserState() == User.UserState.BANNED) {
      throw new IllegalStateException("이미 차단된 유저입니다.");
    }
    user.setUserState(User.UserState.BANNED);
    userRepository.save(user);
    return "유저가 차단 되었습니다.";
  }



  //관리자 신고 게시글 조회
  @Transactional(readOnly = true)
  public Page<ReportListResponseDto> getReportList(Pageable pageable) {
    Page<Report> page = reportRepository.findAll(pageable);

    return page
        .map(report -> ReportListResponseDto.builder()
            .id(report.getId())
            .targetType(report.getTargetType())
            .targetId(report.getTargetId())
            .reason(report.getReason())
            .description(report.getDescription())
            .createdAt(report.getCreatedAt())
            .build());
  }
  //관리자 게시글 삭제
  @Transactional
  public void adminDeleteCommunityPost(Long postId) {

    CommunityPost post = communityPostRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    communityPostRepository.delete(post);
  }
  @Transactional
  public void adminDeleteRentalPost(Long postId) {
    RentalPost rentalPost = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    postRepository.delete(rentalPost);
  }
}
