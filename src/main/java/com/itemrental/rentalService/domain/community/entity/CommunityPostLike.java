package com.itemrental.rentalService.domain.community.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "community_post_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
    }
)
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPostLike {

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
    private CommunityPost post;

    @Getter
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CommunityPostLike(User user, CommunityPost post) {
        this.user = user;
        this.post = post;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
