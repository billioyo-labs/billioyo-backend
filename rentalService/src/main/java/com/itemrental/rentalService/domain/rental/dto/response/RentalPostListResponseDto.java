package com.itemrental.rentalService.domain.rental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class RentalPostListResponseDto {
  private Long id;
  private String nickname;
  private String title;
  private Long price;
  private boolean status;
  private LocalDateTime registerTime;
  private String imageUrl;
  private double rating;
  private Long reviewsCount;
}





