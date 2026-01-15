package com.itemrental.rentalService.domain.settlement.service;


import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateResponse;
import com.itemrental.rentalService.domain.settlement.dto.SettlementItemResponse;
import com.itemrental.rentalService.domain.settlement.entity.Settlement;
import com.itemrental.rentalService.domain.settlement.entity.SettlementItem;
import com.itemrental.rentalService.domain.settlement.repository.SettlementItemRepository;
import com.itemrental.rentalService.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.itemrental.rentalService.domain.settlement.entity.SettlementItem.SettlementItemStatus.AVAILABLE;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementItemRepository settlementItemRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public SettlementItemResponse getSettlementItems(Long ownerId) {
        List<SettlementItem> items = settlementItemRepository.findAllByOwnerIdAndStatus(
            ownerId,
            AVAILABLE
        );
        long total = items.stream().mapToLong(SettlementItem::getAmount).sum();

        return new SettlementItemResponse(items, total);
    }

    @Transactional
    public SettlementCreateResponse createSettlement(Long ownerId) {

        List<SettlementItem> items = settlementItemRepository
            .findAllByOwnerIdAndStatus(ownerId, AVAILABLE);

        if (items.isEmpty()) throw new IllegalStateException("정산 대상 없음");

        long total = items.stream().mapToLong(SettlementItem::getAmount).sum();

        Settlement settlement = new Settlement();
        settlement.setOwnerId(ownerId);
        settlement.setTotalAmount(total);
        settlementRepository.save(settlement);


        return new SettlementCreateResponse(settlement.getId(), total);
    }





}
