package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.entity.PaymentHistory;
import com.itemrental.rentalService.domain.payment.repository.PaymentHistoryRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PostRepository postRepository;

    public void processPaymentDone(Payment payment, PortOneDto dto) {


        //포트원 조회 결과
        if (payment == null) {
            throw new IllegalStateException("포트원 결제 조회 결과가 없습니다.");
        }

        //impUid/merchantUid 일치 확인
        if (dto.getImpUid() == null || !dto.getImpUid().equals(payment.getImpUid())) {
            throw new IllegalStateException("impUid 불일치");
        }
        if (dto.getMerchantUid() == null || !dto.getMerchantUid().equals(payment.getMerchantUid())) {
            throw new IllegalStateException("merchantUid 불일치");
        }

        //결제 상태 확인 (paid만 통과)
        if (!"paid".equals(payment.getStatus())) {
            throw new IllegalStateException("결제가 완료되지 않았습니다. status=" + payment.getStatus());
        }

        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다. orderId=" + dto.getOrderId()));

        //주문 merchantUid와 포트원 merchantUid 매칭
        if (order.getMerchantUid() == null || !order.getMerchantUid().equals(payment.getMerchantUid())) {
            throw new IllegalStateException("주문 merchantUid 불일치");
        }

        //금액 일치 확인
        long paidAmount = payment.getAmount().longValue();

        long expectedAmount = order.getAmount();

        if (paidAmount != expectedAmount) {
            throw new IllegalStateException("결제 금액 불일치");
        }
        RentalPost post = postRepository.findById(order.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        post.setStatus(true);

        PaymentHistory ph = PaymentHistory.builder()
            .impUid(payment.getImpUid())
            .order(order)
            .merchantUid(payment.getMerchantUid())
            .amount(paidAmount)
            .payMethod(payment.getPayMethod())
            .pgProvider(payment.getPgProvider())
            .name(payment.getName())
            .build();
        paymentHistoryRepository.save(ph);
        order.setStatus(Order.OrderStatus.PAID);
    }

}

