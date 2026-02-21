package com.itemrental.billioyo.domain.community.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Column(nullable = false, length = 1024)
    @Getter
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id", nullable = false)
    private CommunityPost post;

    public CommunityPostImage(String imageUrl, CommunityPost post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }

}
