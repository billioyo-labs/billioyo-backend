
package com.itemrental.rentalService.community;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Community{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "postId", nullable = false, updatable = false, unique = true)
  private Long id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public void update(String title, String content) {
   this.title = title;
   this.content = content;
  }
}
