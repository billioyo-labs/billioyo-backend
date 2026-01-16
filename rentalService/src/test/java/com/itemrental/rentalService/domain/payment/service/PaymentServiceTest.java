package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;
import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.entity.PaymentHistory;
import com.itemrental.rentalService.domain.payment.repository.PaymentHistoryRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentHistoryRepository paymentHistoryRepository;
    @Mock private PostRepository postRepository;

    private PortOneDto createDto() {
        return new PortOneDto("imp_123", "merchant_456", 10000L, 1L);
    }

    private PaymentInfo createPaymentInfo(String status, long amount) {
        return PaymentInfo.builder()
                .impUid("imp_123")
                .merchantUid("merchant_456")
                .status(status)
                .amount(amount)
                .payMethod("card")
                .pgProvider("kcp")
                .name("테스트 물품")
                .build();
    }

    @Test
    @DisplayName("성공: 모든 정보와 금액이 일치하면 결제 처리가 완료된다")
    void processPaymentDone_Success() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);

        Order order = Order.builder()
                .id(1L).merchantUid("merchant_456").amount(10000L).postId(10L).build();
        RentalPost post = new RentalPost();

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when
        paymentService.processPaymentDone(payment, dto);

        // then
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PAID);
        assertThat(post.isStatus()).isTrue();
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    @DisplayName("실패: 포트원 조회 결과가 null이면 예외가 발생한다")
    void processPaymentDone_PaymentNull() {
        assertThatThrownBy(() -> paymentService.processPaymentDone(null, createDto()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("포트원 결제 조회 결과가 없습니다.");
    }

    @Test
    @DisplayName("실패: impUid가 누락되었거나 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_ImpUidMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = PaymentInfo.builder().impUid("wrong_uid").build();

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("impUid 불일치");
    }

    @Test
    @DisplayName("실패: merchantUid가 누락되었거나 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_MerchantUidMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = PaymentInfo.builder().impUid("imp_123").merchantUid("wrong_uid").build();

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("merchantUid 불일치");
    }

    @Test
    @DisplayName("실패: 결제 상태가 paid가 아니면 예외가 발생한다")
    void processPaymentDone_StatusNotPaid() {
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("failed", 10000L);

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 미완료");
    }

    @Test
    @DisplayName("실패: 주문 정보를 찾을 수 없으면 예외가 발생한다")
    void processPaymentDone_OrderNotFound() {
        given(orderRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPaymentDone(createPaymentInfo("paid", 10000L), createDto()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("실패: 주문의 merchantUid와 포트원의 정보가 다르면 예외가 발생한다")
    void processPaymentDone_OrderMerchantUidMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);
        Order order = Order.builder().merchantUid("other_uid").build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 merchantUid 불일치");
    }

    @Test
    @DisplayName("실패: 결제 금액이 주문 금액과 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_AmountMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 5000L);
        Order order = Order.builder().merchantUid("merchant_456").amount(10000L).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 금액 불일치");
    }

    @Test
    @DisplayName("실패: 게시글 정보를 찾을 수 없으면 예외가 발생한다")
    void processPaymentDone_PostNotFound() {
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);
        Order order = Order.builder().merchantUid("merchant_456").amount(10000L).postId(999L).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }
}
