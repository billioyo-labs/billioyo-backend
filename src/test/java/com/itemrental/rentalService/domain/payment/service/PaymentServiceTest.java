package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.exception.OrderNotFoundException;
import com.itemrental.rentalService.domain.order.exception.UnauthorizedOrderAccessException;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;
import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.entity.PaymentHistory;
import com.itemrental.rentalService.domain.payment.exception.AlreadyPaidException;
import com.itemrental.rentalService.domain.payment.exception.PaymentMismatchException;
import com.itemrental.rentalService.domain.payment.repository.PaymentHistoryRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    private final String TEST_EMAIL = "owner@test.com";

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
        User user = User.builder().email(TEST_EMAIL).build();
        User owner = User.builder().email("owner@test.com").build();

        Order order = Order.builder()
                .id(1L).user(user).merchantUid("merchant_456").amount(10000L).postId(10L).build();
        RentalPost post = RentalPost.create(owner, "제목", "내용", 10000L, "서울", null, "가전");
        ReflectionTestUtils.setField(post, "id", 10L);

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when
        paymentService.processPaymentDone(payment, dto, TEST_EMAIL);

        // then
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PAID);
        assertThat(post.isStatus()).isTrue();
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    @DisplayName("실패: 포트원 조회 결과가 null이면 예외가 발생한다")
    void processPaymentDone_PaymentNull() {
        assertThatThrownBy(() -> paymentService.processPaymentDone(null, createDto(), TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("포트원 결제 조회 결과가 없습니다.");
    }

    @Test
    @DisplayName("실패: impUid가 누락되었거나 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_ImpUidMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = PaymentInfo.builder().impUid("wrong_uid").build();

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("impUid 불일치");
    }

    @Test
    @DisplayName("실패: merchantUid가 누락되었거나 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_MerchantUidMismatch() {
        PortOneDto dto = createDto();
        PaymentInfo payment = PaymentInfo.builder().impUid("imp_123").merchantUid("wrong_uid").build();

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("merchantUid 불일치");
    }

    @Test
    @DisplayName("실패: 결제 상태가 paid가 아니면 예외가 발생한다")
    void processPaymentDone_StatusNotPaid() {
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("failed", 10000L);

        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("결제 미완료");
    }

    @Test
    @DisplayName("실패: 주문 정보를 찾을 수 없으면 예외가 발생한다")
    void processPaymentDone_OrderNotFound() {
        given(orderRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPaymentDone(createPaymentInfo("paid", 10000L), createDto(), TEST_EMAIL))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("실패: 결제를 시도하는 유저와 주문한 유저가 다르면 예외가 발생한다")
    void processPaymentDone_UserMismatch() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);
        User owner = User.builder().email("actual_owner@test.com").build();
        Order order = Order.builder().user(owner).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, "stranger@test.com"))
                .isInstanceOf(UnauthorizedOrderAccessException.class);
    }

    @Test
    @DisplayName("실패: 이미 결제 완료(PAID) 상태인 주문을 다시 처리하려고 하면 예외가 발생한다")
    void processPaymentDone_AlreadyPaid() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);
        User user = User.builder().email(TEST_EMAIL).build();
        Order order = Order.builder().user(user).status(Order.OrderStatus.PAID).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(AlreadyPaidException.class);
    }

    @Test
    @DisplayName("실패: 주문의 merchantUid와 포트원의 정보가 다르면 예외가 발생한다")
    void processPaymentDone_OrderMerchantUidMismatch() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);

        User user = User.builder().email(TEST_EMAIL).build();
        Order order = Order.builder().merchantUid("other_uid").user(user).amount(10000L).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("주문 merchantUid 불일치");
    }

    @Test
    @DisplayName("실패: 결제 금액이 주문 금액과 일치하지 않으면 예외가 발생한다")
    void processPaymentDone_AmountMismatch() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 5000L);
        User user = User.builder().email(TEST_EMAIL).build();
        Order order = Order.builder().user(user).merchantUid("merchant_456").amount(10000L).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PaymentMismatchException.class)
                .hasMessageContaining("결제 금액 불일치");
    }

    @Test
    @DisplayName("실패: 게시글 정보를 찾을 수 없으면 예외가 발생한다")
    void processPaymentDone_PostNotFound() {
        // given
        PortOneDto dto = createDto();
        PaymentInfo payment = createPaymentInfo("paid", 10000L);
        User user = User.builder().email(TEST_EMAIL).build();
        Order order = Order.builder().user(user).merchantUid("merchant_456").amount(10000L).postId(999L).build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.processPaymentDone(payment, dto, TEST_EMAIL))
                .isInstanceOf(PostNotFoundException.class);
    }
}
