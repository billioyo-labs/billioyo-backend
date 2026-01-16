package com.itemrental.rentalService.domain.settlement.repository;

import com.itemrental.rentalService.domain.settlement.entity.Settlement;
import com.itemrental.rentalService.domain.settlement.entity.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementItemRepository extends JpaRepository<SettlementItem, Long> {
    List<SettlementItem> findAllByOwnerIdAndStatus(
        Long ownerId,
        SettlementItem.SettlementItemStatus status);
    List<SettlementItem> findAllBySettlementId(Long settlementId);
    int countByOwnerIdAndStatus_Settled(Long ownerId);
}
