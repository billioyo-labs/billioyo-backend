package com.itemrental.rentalService.domain.rental.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RentalPostUpdateRequestDto {
  private String title;
  private String description;
  private Long price;
  private String location;
  private String category;
}
