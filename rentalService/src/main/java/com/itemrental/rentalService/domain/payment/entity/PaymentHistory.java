package com.itemrental.rentalService.domain.payment.entity;

import com.itemrental.rentalService.domain.order.entity.Order;
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
public class PaymentHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String impUid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(name = "merchant_uid", nullable = false)
  private String merchantUid;


  @Column(nullable = false)
  private Long amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status = PaymentStatus.PAID;

  @Column(name = "pay_method", nullable = false)
  private String payMethod;

  @Column(name = "pg_provider", nullable = false)
  private String pgProvider;

  @Column(name = "payment_name")
  private String name;

  private LocalDateTime createdAt;
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

  public enum PaymentStatus {
    READY, PAID, FAILED, CANCELED
  }

  @Builder
  private PaymentHistory(
      String impUid,
      Order order,
      String merchantUid,
      Long amount,
      String payMethod,
      String pgProvider,
      String name
  ){
    this.impUid = impUid;
    this.order = order;
    this.merchantUid = merchantUid;
    this.amount = amount;
    this.payMethod = payMethod;
    this.pgProvider = pgProvider;
    this.name = name;
  }
}
