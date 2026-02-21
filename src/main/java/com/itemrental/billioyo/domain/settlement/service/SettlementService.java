package com.itemrental.billioyo.domain.settlement.service;


import com.itemrental.billioyo.domain.settlement.dto.SettlementCreateRequest;
import com.itemrental.billioyo.domain.settlement.dto.SettlementCreateResponse;
import com.itemrental.billioyo.domain.settlement.dto.SettlementItemResponse;
import com.itemrental.billioyo.domain.settlement.entity.Settlement;
import com.itemrental.billioyo.domain.settlement.entity.SettlementItem;
import com.itemrental.billioyo.domain.settlement.repository.SettlementItemRepository;
import com.itemrental.billioyo.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementItemRepository settlementItemRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public SettlementItemResponse getSettlementItems(Long ownerId) {
        List<SettlementItem> items = settlementItemRepository.findAllByOwnerIdAndStatus(
            ownerId,
            SettlementItem.SettlementItemStatus.AVAILABLE
        );
        long total = items.stream().mapToLong(SettlementItem::getAmount).sum();

        return new SettlementItemResponse(items, total);
    }

    @Transactional
    public SettlementCreateResponse createSettlement(Long ownerId, SettlementCreateRequest dto) {

        List<SettlementItem> items = settlementItemRepository
            .findAllByOwnerIdAndStatus(ownerId, SettlementItem.SettlementItemStatus.AVAILABLE);

        if (items.isEmpty()) throw new IllegalStateException("정산 대상 없음");

        long total = items.stream().mapToLong(SettlementItem::getAmount).sum();

        Settlement settlement = Settlement.builder()
            .ownerId(ownerId)
            .totalAmount(total)
            .bankName(dto.getBankName())
            .bankAccountNumber(dto.getAccountNumber())
            .bankAccountHolderName(dto.getAccountHolder())
            .build();

        settlementRepository.save(settlement);

        for (SettlementItem item : items) {
            item.setSettlementId(settlement.getId());
        }
        return new SettlementCreateResponse(settlement.getId(), total);
    }
    //정산 완료
    @Transactional
    public void completeSettlement(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
            .orElseThrow(()-> new IllegalArgumentException("정산 건이 존재하지 않습니다"));

        if (settlement.getStatus() == Settlement.SettlementStatus.SETTLED) return;

        List<SettlementItem> items = settlementItemRepository.findAllBySettlementId(settlementId);

        if (items.isEmpty()) throw new IllegalStateException("정산 아이템이 없습니다.");


        for(SettlementItem item : items) {
            item.setStatus(SettlementItem.SettlementItemStatus.SETTLED);
        }

        settlement.complete();
    }
}
