package com.itemrental.rentalService.domain.community.entity;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Getter
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @Getter
    @Setter
    private CommunityPost post;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Getter
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public CommunityComment(User user, CommunityPost post, String comment) {
        this.user = user;
        this.post = post;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

}