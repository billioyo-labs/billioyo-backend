package com.itemrental.billioyo.domain.community.entity;

import com.itemrental.billioyo.domain.rental.exception.UnauthorizedAccessException;
import com.itemrental.billioyo.domain.user.entity.User;
import com.itemrental.billioyo.domain.user.entity.Position;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Embedded
    private Position position;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityCategory category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityPostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments = new ArrayList<>();

    public static CommunityPost createPost(User user, String title, String content, CommunityCategory category, String location, Position position) {
        CommunityPost post = new CommunityPost();
        post.user = user;
        post.title = title;
        post.content = content;
        post.category = category;
        post.location = location;
        post.position = position;
        return post;
    }

    public void update(String title, String content, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        updateImages(imageUrls);
    }

    public void updateImages(List<String> imageUrls) {
        this.images.clear();
        if (imageUrls != null) {
            imageUrls.forEach(url -> this.images.add(new CommunityPostImage(url, this)));
        }
    }

    public void increaseViewCount() { this.viewCount++; }
    public void increaseCommentCount() { this.commentCount++; }
    public void addLike() { this.likeCount++; }
    public void removeLike() { if(this.likeCount > 0) this.likeCount--; }

    public void validateAuthor(String currentUserEmail) {
        if (!this.user.getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedAccessException();
        }
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum CommunityCategory {
        INFO,
        TIP,
        PET,
        BBANG,
        LOST
    }

}