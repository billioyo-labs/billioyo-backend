package com.itemrental.rentalService.domain.order.dto;


import com.itemrental.rentalService.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderCreateResponseDto {
    private String merchantUid;
    private Long orderId;
    private Long amount;
    private Order.OrderStatus status;
}



