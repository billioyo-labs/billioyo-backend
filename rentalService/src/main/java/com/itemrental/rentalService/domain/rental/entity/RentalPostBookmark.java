package com.itemrental.rentalService.domain.rental.entity;


import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더나 팩토리 메서드에서만 사용
@Builder(access = AccessLevel.PRIVATE)
@Table(
        name = "rental_post_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_user_bookmark", columnNames = {"post_id", "user_id"})
        }
)
public class RentalPostBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private RentalPost rentalPost;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RentalPostBookmark create(User user, RentalPost rentalPost) {
        return RentalPostBookmark.builder()
                .user(user)
                .rentalPost(rentalPost)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
