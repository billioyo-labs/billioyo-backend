package com.itemrental.rentalService.domain.order.service;

import com.itemrental.rentalService.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.rentalService.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.exception.AlreadyRentedException;
import com.itemrental.rentalService.domain.order.exception.SelfRentalNotAllowedException;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.payment.exception.PaymentMismatchException;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        RentalPost rentalPost = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(dto.getPostId()));

        if (rentalPost.getUser().getEmail().equals(email)) {
            throw new SelfRentalNotAllowedException();
        }

        if (rentalPost.isStatus()) {
            throw new AlreadyRentedException();
        }

        if (!rentalPost.getPrice().equals(dto.getAmount())) {
            throw new PaymentMismatchException("결제 요청 금액이 물품 가격과 일치하지 않습니다.");
        }

        if (dto.getAmount() <= 0) {
            throw new PaymentMismatchException("주문 금액은 0보다 커야 합니다.");
        }

        Order order = Order.builder()
                .user(user)
                .postId(rentalPost.getId())
                .merchantUid(generateMerchantUid())
                .amount(dto.getAmount())
                .build();

        Order saved = orderRepository.save(order);

        return OrderCreateResponseDto.builder()
                .merchantUid(saved.getMerchantUid())
                .orderId(saved.getId())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .build();
    }

    private String generateMerchantUid() {
        return "order_" + UUID.randomUUID().toString().replace("-", "");
    }
}
