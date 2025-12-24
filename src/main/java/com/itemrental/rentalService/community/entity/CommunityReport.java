package com.itemrental.rentalService.community.entity;

import com.itemrental.rentalService.community.enums.ReportReason;
import com.itemrental.rentalService.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CommunityReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private CommunityPost post;

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
  @Builder
  public CommunityReport(
      CommunityPost post,
      User reporter,
      ReportReason reason,
      String description
  ) {
    this.post = post;
    this.reporter = reporter;
    this.reason = reason;
    this.description = description;
  }


}
