package com.itemrental.rentalService.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime settledAt;

    @Column
    private String referenceNo;

    @PrePersist
    void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }

    public enum SettlementStatus {
        REQUESTED, // 정산 요청됨
        SETTLED   // 정산 완료
    }

}
