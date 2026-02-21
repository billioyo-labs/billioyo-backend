package com.itemrental.billioyo.domain.settlement.repository;

import com.itemrental.billioyo.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findAllByOwnerIdAndStatus(Long userId,Settlement.SettlementStatus status);

}
