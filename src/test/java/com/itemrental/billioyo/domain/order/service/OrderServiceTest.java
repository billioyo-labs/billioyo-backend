package com.itemrental.billioyo.domain.order.service;

import com.itemrental.billioyo.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.billioyo.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.billioyo.domain.order.entity.Order;
import com.itemrental.billioyo.domain.order.exception.AlreadyRentedException;
import com.itemrental.billioyo.domain.order.exception.SelfRentalNotAllowedException;
import com.itemrental.billioyo.domain.order.repository.OrderRepository;
import com.itemrental.billioyo.domain.payment.exception.PaymentMismatchException;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.exception.PostNotFoundException;
import com.itemrental.billioyo.domain.rental.repository.PostRepository;
import com.itemrental.billioyo.domain.user.entity.User;
import com.itemrental.billioyo.domain.user.exception.UserNotFoundException;
import com.itemrental.billioyo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("성공: 모든 정보가 유효하면 주문이 생성되고 정보를 반환한다")
    void createOrder_Success() {
        // given
        String email = "user@test.com";
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(10L, 5000L);

        User owner = User.builder().id(2L).email("owner@test.com").build();
        User renter = User.builder().id(1L).email(email).build();

        RentalPost post = RentalPost.create(owner, "제목", "내용", 5000L, "서울", null, "가전");
        ReflectionTestUtils.setField(post, "id", 10L);
        post.changeStatus(false);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(renter));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderCreateResponseDto response = orderService.createOrder(requestDto, email);

        // then
        assertThat(response.getAmount()).isEqualTo(5000L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 이메일로 주문하면 예외가 발생한다")
    void createOrder_UserNotFound() {
        // given
        String email = "wrong@test.com";
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(10L, 5000L);

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(requestDto, email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 게시글 ID로 주문하면 예외가 발생한다")
    void createOrder_PostNotFound() {
        // given
        String email = "user@test.com";
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(99L, 5000L);
        User user = User.builder().email(email).build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(requestDto, email))
                .isInstanceOf(PostNotFoundException.class);
    }
    @Test
    @DisplayName("실패: 본인이 등록한 물건을 대여하려고 하면 예외가 발생한다")
    void createOrder_SelfRental() {
        // given
        String email = "owner@test.com";
        User owner = User.builder().email(email).build();
        RentalPost post = RentalPost.create(owner, "제목", "내용", 5000L, "서울", null, "가전");
        ReflectionTestUtils.setField(post, "id", 10L);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(owner));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(new OrderCreateRequestDto(10L, 5000L), email))
                .isInstanceOf(SelfRentalNotAllowedException.class);
    }

    @Test
    @DisplayName("실패: 이미 대여 중인 물건(status=true)을 주문하면 예외가 발생한다")
    void createOrder_AlreadyRented() {
        // given
        String email = "renter@test.com";
        User owner = User.builder().email("owner@test.com").build();
        RentalPost post = RentalPost.create(owner, "제목", "내용", 5000L, "서울", null, "가전");
        post.changeStatus(true);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(User.builder().build()));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(new OrderCreateRequestDto(10L, 5000L), email))
                .isInstanceOf(AlreadyRentedException.class);
    }

    @Test
    @DisplayName("실패: 요청 금액이 게시글 가격과 다르면 예외가 발생한다")
    void createOrder_PriceMismatch() {
        //given
        String renterEmail = "renter@test.com";
        String ownerEmail = "owner@test.com";

        User owner = User.builder().email(ownerEmail).build();
        User renter = User.builder().email(renterEmail).build();

        RentalPost post = RentalPost.create(owner, "제목", "내용", 10000L, "서울", null, "가전");
        post.changeStatus(false);

        given(userRepository.findByEmail(renterEmail)).willReturn(Optional.of(renter));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(new OrderCreateRequestDto(10L, 5000L), renterEmail))
                .isInstanceOf(PaymentMismatchException.class);
    }

    @Test
    @DisplayName("실패: 주문 금액이 0원 이하이면 예외가 발생한다")
    void createOrder_InvalidAmount() {
        // given
        String email = "renter@test.com";
        Long postId = 10L;
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(10L, 0L);

        User renter = User.builder().email(email).build();
        User owner = User.builder().email("owner@test.com").build();
        RentalPost post = RentalPost.create(owner, "제목", "내용", 0L, "서울", null, "가전");
        ReflectionTestUtils.setField(post, "id", postId);
        post.changeStatus(false);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(renter));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(requestDto, email))
                .isInstanceOf(PaymentMismatchException.class);
    }
}
