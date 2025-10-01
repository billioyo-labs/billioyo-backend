
package com.itemrental.rentalService.community.entity;

import com.itemrental.rentalService.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="userId", nullable = false)
  @Getter
  @Setter
  private User user;

  @Getter
  @Setter
  @Column(nullable = false)
  private String title;

  @Getter
  @Setter
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Getter
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Getter
  @Setter
  @Column(nullable = false)
  private int viewCount = 0;

  @Getter
  @Setter
  @Column(nullable = false)
  private int likeCount = 0;

  @Getter
  @Setter
  @Column(nullable = false)
  private int commentCount = 0;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Getter
  private List<CommunityPostImage> images;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Getter
  private List<CommunityPostLike> likes;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Getter
  private List<CommunityPostBookmark> bookmarks;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Getter
  private List<CommunityComment> comments;
}