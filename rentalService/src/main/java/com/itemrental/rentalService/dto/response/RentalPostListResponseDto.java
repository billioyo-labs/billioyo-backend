package com.itemrental.rentalService.dto.response;

import com.itemrental.rentalService.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class RentalPostListResponseDto {
  private Long id;
  private User user;
  private String title;
  private Long price;
  private boolean status;
  private LocalDateTime registerTime;
}





