package com.itemrental.billioyo.domain.payment.service;

import com.itemrental.billioyo.domain.order.entity.Order;
import com.itemrental.billioyo.domain.order.exception.OrderNotFoundException;
import com.itemrental.billioyo.domain.order.exception.UnauthorizedOrderAccessException;
import com.itemrental.billioyo.domain.order.repository.OrderRepository;
import com.itemrental.billioyo.domain.payment.dto.PaymentInfo;
import com.itemrental.billioyo.domain.payment.dto.PortOneDto;
import com.itemrental.billioyo.domain.payment.entity.PaymentHistory;
import com.itemrental.billioyo.domain.payment.exception.AlreadyPaidException;
import com.itemrental.billioyo.domain.payment.exception.PaymentMismatchException;
import com.itemrental.billioyo.domain.payment.repository.PaymentHistoryRepository;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.exception.PostNotFoundException;
import com.itemrental.billioyo.domain.rental.repository.PostRepository;
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

    @Transactional
    public void processPaymentDone(PaymentInfo payment, PortOneDto dto, String email) {
        // 1. 포트원 조회 결과 기본 검증
        validatePaymentData(payment, dto);

        // 2. 주문 조회
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(dto.getOrderId()));

        // 3. 결제 주체 검증
        if (!order.getUser().getEmail().equals(email)) {
            throw new UnauthorizedOrderAccessException();
        }

        // 4. 중복 결제 및 금액/UID 매칭 검증
        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new AlreadyPaidException();
        }
        validateOrderMatching(order, payment);

        // 5. 상태 변경 및 이력 저장
        RentalPost post = postRepository.findById(order.getPostId())
                .orElseThrow(() -> new PostNotFoundException(order.getPostId()));

        post.changeStatus(true);
        order.setStatus(Order.OrderStatus.PAID);
        savePaymentHistory(payment, order);
    }

    private void validatePaymentData(PaymentInfo payment, PortOneDto dto) {
        if (payment == null) throw new PaymentMismatchException("포트원 결제 조회 결과가 없습니다.");
        if (!dto.getImpUid().equals(payment.getImpUid())) throw new PaymentMismatchException("impUid 불일치");
        if (!dto.getMerchantUid().equals(payment.getMerchantUid())) throw new PaymentMismatchException("주문 merchantUid 불일치");
        if (!"paid".equals(payment.getStatus())) throw new PaymentMismatchException("결제 미완료");
    }

    private void validateOrderMatching(Order order, PaymentInfo payment) {
        if (!order.getMerchantUid().equals(payment.getMerchantUid())) {
            throw new PaymentMismatchException("주문 merchantUid 불일치");
        }
        if (order.getAmount() != payment.getAmount()) {
            throw new PaymentMismatchException("결제 금액 불일치");
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

