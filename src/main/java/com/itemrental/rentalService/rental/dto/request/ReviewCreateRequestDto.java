package com.itemrental.rentalService.rental.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {
  private String content;
  private int rating;
}
