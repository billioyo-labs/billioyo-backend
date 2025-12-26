package com.itemrental.rentalService.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TargetType targetType;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private User reporter;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportReason reason;

  @Column(columnDefinition = "TEXT")
  private String description;


  private LocalDateTime createdAt;


  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public enum TargetType {
    COMMUNITY,
    RENTAL
  }

  public enum ReportReason {
    ABUSE,      // 욕설
    SPAM,       // 광고/스팸
    ILLEGAL,    // 불법 콘텐츠
    HATE_SPEECH,// 혐오 표현
    SEXUAL,     // 음란물
    OTHER       // 기타
  }



  @Builder
  public Report(
      Long targetId,
      TargetType targetType,
      User reporter,
      ReportReason reason,
      String description
  ) {
    this.targetId = targetId;
    this.targetType = targetType;
    this.reporter = reporter;
    this.reason = reason;
    this.description = description;
  }


}
