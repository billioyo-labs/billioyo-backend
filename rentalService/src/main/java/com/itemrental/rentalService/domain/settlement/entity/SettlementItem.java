package com.itemrental.rentalService.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SettlementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementItemStatus status = SettlementItemStatus.AVAILABLE;


    @Column
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum SettlementItemStatus {
        AVAILABLE,  // 정산 가능 (대여 완료됨)
        SETTLED    // 정산 완료됨
    }

    @Builder
    private SettlementItem(
        Long postId,
        Long ownerId,
        Long orderId,
        Long amount
    ){
        this.postId = postId;
        this.ownerId = ownerId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
