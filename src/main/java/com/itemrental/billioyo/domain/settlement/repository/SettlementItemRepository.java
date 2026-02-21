package com.itemrental.billioyo.domain.settlement.repository;

import com.itemrental.billioyo.domain.settlement.entity.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementItemRepository extends JpaRepository<SettlementItem, Long> {
    List<SettlementItem> findAllByOwnerIdAndStatus(
        Long ownerId,
        SettlementItem.SettlementItemStatus status);
    List<SettlementItem> findAllBySettlementId(Long settlementId);
    int countByOwnerIdAndStatus(Long ownerId,SettlementItem.SettlementItemStatus status);
}
