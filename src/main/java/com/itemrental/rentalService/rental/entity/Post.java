package com.itemrental.rentalService.rental.entity;


import com.itemrental.rentalService.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id @Getter @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId", nullable = false, updatable = false, unique = true)
    private Long id;

    @Getter @Setter @Column(nullable = false)
    private String title;

    @Getter @Setter @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Getter @Setter @Column(nullable = false)
    private Long price;

    @Getter @Setter @Column(nullable = false)
    private String location;

    @Getter @Setter @Column(nullable = false)
    private boolean status = true;

    @Getter @Setter @Column(nullable = false)
    private LocalDateTime createdAt;

    @Getter @Setter @Column(nullable = false)
    private Long viewCount = 0L;

    @Getter @Setter @Column(nullable = false)
    private Long reviewsCount = 0L;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Review> reviews;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Image> images;

}
