package com.itemrental.billioyo.domain.payment.repository;

import com.itemrental.billioyo.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
