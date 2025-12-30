package com.itemrental.rentalService.domain.report.service;


import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.report.dto.ReportRequestDto;
import com.itemrental.rentalService.domain.report.entity.Report;
import com.itemrental.rentalService.domain.report.repository.ReportRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;
  private final CommunityPostRepository communityPostRepository;
  private final PostRepository postRepository;

  @Transactional
  public void createReport(ReportRequestDto dto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User reporter = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    Report report = Report.builder( )
        .targetType(dto.getTargetType())
        .targetId(dto.getTargetId())
        .reason(dto.getReason())
        .description(dto.getDescription())
        .reporter(reporter)
        .build();
    reportRepository.save(report);
  }

}
