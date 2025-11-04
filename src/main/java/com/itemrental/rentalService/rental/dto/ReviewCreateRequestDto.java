package com.itemrental.rentalService.rental.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {
  String content;
  int rating;
}
