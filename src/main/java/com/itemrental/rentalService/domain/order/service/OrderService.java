package com.itemrental.rentalService.domain.order.service;

import com.itemrental.rentalService.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.rentalService.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.user.entity.User;
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
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        RentalPost rentalPost = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (rentalPost.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인이 등록한 물품은 대여할 수 없습니다.");
        }

        if (rentalPost.isStatus()) {
            throw new IllegalStateException("이미 대여 중인 물품입니다.");
        }

        if (!rentalPost.getPrice().equals(dto.getAmount())) {
            throw new IllegalArgumentException("결제 요청 금액이 물품 가격과 일치하지 않습니다.");
        }

        if (dto.getAmount() <= 0) {
            throw new IllegalArgumentException("주문 금액은 0보다 커야 합니다.");
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
