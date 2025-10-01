
package com.itemrental.rentalService.community.entity;

import com.itemrental.rentalService.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="user_id", nullable = false)
  @Getter
  @Setter
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="post_id", nullable = false)
  @Getter
  @Setter
  private CommunityPost post;

  @Getter
  @Setter
  @Column(nullable = false, columnDefinition = "TEXT")
  private String comment;

  @Getter
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

}