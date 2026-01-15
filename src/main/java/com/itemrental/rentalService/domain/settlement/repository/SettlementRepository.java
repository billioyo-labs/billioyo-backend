package com.itemrental.rentalService.domain.settlement.repository;

import com.itemrental.rentalService.domain.settlement.entity.Settlement;
import com.itemrental.rentalService.domain.settlement.entity.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
