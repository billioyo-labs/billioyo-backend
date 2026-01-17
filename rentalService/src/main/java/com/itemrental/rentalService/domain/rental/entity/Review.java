package com.itemrental.rentalService.domain.rental.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int rating; // 1~5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private RentalPost rentalPost;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Review create(User user, RentalPost post, String content, int rating) {
        Review review = new Review();
        review.user = user;
        review.rentalPost = post;
        review.content = content;
        review.rating = rating;
        review.createdAt = LocalDateTime.now();

        post.addReview(review);
        return review;
    }
}
