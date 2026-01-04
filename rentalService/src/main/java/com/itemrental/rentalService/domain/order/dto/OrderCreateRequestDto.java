package com.itemrental.rentalService.domain.order.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {
  private Long postId;
  private Long amount;
}
