package com.itemrental.rentalService.domain.rental.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.global.utils.Position;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rental_post")
public class RentalPost {

    @Id @Getter @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Getter @Setter @Column(nullable = false)
    private String title;

    @Getter @Setter @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Getter @Setter @Column(nullable = false)
    private Long price;

    @Getter @Setter @Column(nullable = false)
    private String location;

    @Getter @Setter @Column(nullable = false) @Embedded
    private Position position;

    @Getter @Setter @Column(nullable = false)
    private boolean status = true;

    @Getter @Setter @Column(nullable = false)
    private LocalDateTime createdAt;

    @Getter @Setter @Column(nullable = false)
    private Long viewCount = 0L;

    @Getter @Setter @Column(nullable = false)
    private Long reviewsCount = 0L;

    @Getter @Setter @Column(nullable = false)
    private double rating = 0.0;

    @Getter @Setter @Column(nullable = false)
    private Long likeCount = 0L;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @Getter @Setter
    private User user;

    @Getter @Setter @Column(nullable = false)
    private String category;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<RentalPostBookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<RentalPostLike> likes = new ArrayList<>();

    public void addImage(Image image) {
        this.images.add(image);
        image.setRentalPost(this);
    }
}
