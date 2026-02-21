package com.itemrental.billioyo.domain.order.entity;

import com.itemrental.billioyo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
@Builder
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문자

    @Column
    private Long postId;

    @Column
    private String merchantUid;

    @Column
    private Long amount; // 가격

    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    @Setter
    private OrderStatus status = OrderStatus.CREATED;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;


    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public enum OrderStatus {
        CREATED,    // 주문 생성 (결제 전)
        PAID,       // 결제 완료
        CANCELED,   // 주문 취소 (사용자/시스템)
        EXPIRED,     // 결제 미완료로 만료
        RETURNED // 반납됨
    }
}
