package com.itemrental.rentalService.rental.dto;


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
