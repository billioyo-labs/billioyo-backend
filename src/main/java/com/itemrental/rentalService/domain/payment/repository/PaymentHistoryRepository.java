package com.itemrental.rentalService.domain.payment.repository;

import com.itemrental.rentalService.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
