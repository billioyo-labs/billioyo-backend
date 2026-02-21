package com.itemrental.billioyo.domain.settlement.entity;

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
    private SettlementStatus status=SettlementStatus.REQUESTED;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime settledAt;

    @Column
    private String bankName;

    @Column
    private String bankAccountNumber;

    @Column
    private String bankAccountHolderName;

    @PrePersist
    void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }

    public enum SettlementStatus {
        REQUESTED, // 정산 요청됨
        SETTLED   // 정산 완료
    }

    @Builder
    private Settlement(
        Long ownerId,
        Long totalAmount,
        String bankName,
        String bankAccountNumber,
        String bankAccountHolderName
    ){
        this.ownerId = ownerId;
        this.totalAmount = totalAmount;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountHolderName = bankAccountHolderName;
    }
    public void complete() {
        this.status = SettlementStatus.SETTLED;
        this.settledAt = LocalDateTime.now();
    }
}
