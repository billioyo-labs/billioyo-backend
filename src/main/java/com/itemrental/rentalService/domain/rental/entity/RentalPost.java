package com.itemrental.rentalService.domain.rental.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.entity.Position;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "rental_post")
public class RentalPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String location;

    @Embedded
    @Column(nullable = false)
    private Position position;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long reviewsCount;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private Long likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String category;

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RentalPostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RentalPostBookmark> bookmarks = new ArrayList<>();



    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public static RentalPost create(User user, String title, String description, Long price,
                                    String location, Position position, String category) {
        return RentalPost.builder()
                .user(user)
                .title(title)
                .description(description)
                .price(price)
                .location(location)
                .position(position)
                .category(category)
                .status(false)
                .viewCount(0L)
                .reviewsCount(0L)
                .likeCount(0L)
                .rating(0.0)
                .createdAt(LocalDateTime.now())
                .images(new ArrayList<>())
                .build();
    }

    public void update(String title, String description, Long price, String location, String category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.category = category;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void toggleLike(boolean isAdding) {
        this.likeCount = isAdding ? this.likeCount + 1 : Math.max(0, this.likeCount - 1);
    }

    public void changeStatus(boolean status) {
        this.status = status;
    }
    
    public void addImage(String imageUrl) {
        Image image = new Image(imageUrl, this);
        this.images.add(image);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void updateRating(int newRating) {
        double totalScore = (this.rating * this.reviewsCount) + newRating;
        this.reviewsCount++;
        this.rating = Math.round((totalScore / this.reviewsCount) * 10) / 10.0;
    }
}
