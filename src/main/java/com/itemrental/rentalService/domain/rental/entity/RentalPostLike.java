package com.itemrental.rentalService.domain.rental.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(
    name = "rental_post_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
    }
)
@AllArgsConstructor
@NoArgsConstructor
public class RentalPostLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @Getter
  @Setter
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  @Getter
  @Setter
  private RentalPost rentalPost;

  @Getter
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

}
