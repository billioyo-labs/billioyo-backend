package com.itemrental.billioyo.domain.rental.entity;


import com.itemrental.billioyo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "rental_post_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class RentalPostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private RentalPost rentalPost;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RentalPostLike create(User user, RentalPost rentalPost) {
        RentalPostLike like = new RentalPostLike();
        like.user = user;
        like.rentalPost = rentalPost;
        like.createdAt = LocalDateTime.now();
        return like;
    }
}
