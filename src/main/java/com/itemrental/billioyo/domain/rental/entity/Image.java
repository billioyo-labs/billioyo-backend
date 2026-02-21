package com.itemrental.billioyo.domain.rental.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private RentalPost rentalPost;

    public Image(String imageUrl, RentalPost post) {
        this.imageUrl = imageUrl;
        this.rentalPost = post;
    }
}
