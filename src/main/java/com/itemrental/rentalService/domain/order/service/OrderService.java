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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final OrderRepository orderRepository;

  @Transactional
  public OrderCreateResponseDto createOrder(OrderCreateRequestDto dto) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    RentalPost rentalPost = postRepository.findById(dto.getPostId())
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    String merchantUid = "order_" + UUID.randomUUID();

    Order order = Order.builder()
        .user(user)
        .postId(rentalPost.getId())
        .merchantUid(merchantUid)
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


}
