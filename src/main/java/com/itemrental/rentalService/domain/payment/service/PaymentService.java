package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;
import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.entity.PaymentHistory;
import com.itemrental.rentalService.domain.payment.repository.PaymentHistoryRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // 모든 데이터 변경을 하나의 작업 단위로 묶음
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PostRepository postRepository;

    public void processPaymentDone(PaymentInfo payment, PortOneDto dto) {
        // 1. 데이터 정합성 검증 (Value Object들끼리 비교)
        validatePaymentData(payment, dto);

        // 2. 주문 정보 조회 및 검증
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다. orderId=" + dto.getOrderId()));

        validateOrderMatching(order, payment);

        // 3. 비즈니스 상태 변경 (객체에게 메시지를 던짐)
        RentalPost post = postRepository.findById(order.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        post.setStatus(true);
        order.setStatus(Order.OrderStatus.PAID);

        // 4. 결제 이력 저장
        savePaymentHistory(payment, order);
    }

    private void validatePaymentData(PaymentInfo payment, PortOneDto dto) {
        if (payment == null) throw new IllegalStateException("포트원 결제 조회 결과가 없습니다.");
        if (!dto.getImpUid().equals(payment.getImpUid())) throw new IllegalStateException("impUid 불일치");
        if (!dto.getMerchantUid().equals(payment.getMerchantUid())) throw new IllegalStateException("merchantUid 불일치");
        if (!"paid".equals(payment.getStatus())) throw new IllegalStateException("결제 미완료");
    }

    private void validateOrderMatching(Order order, PaymentInfo payment) {
        if (!order.getMerchantUid().equals(payment.getMerchantUid())) {
            throw new IllegalStateException("주문 merchantUid 불일치");
        }
        if (order.getAmount() != payment.getAmount()) {
            throw new IllegalStateException("결제 금액 불일치");
        }
    }

    private void savePaymentHistory(PaymentInfo payment, Order order) {
        PaymentHistory ph = PaymentHistory.builder()
                .impUid(payment.getImpUid())
                .order(order)
                .merchantUid(payment.getMerchantUid())
                .amount(payment.getAmount())
                .payMethod(payment.getPayMethod())
                .pgProvider(payment.getPgProvider())
                .name(payment.getName())
                .build();
        paymentHistoryRepository.save(ph);
    }
}

