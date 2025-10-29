package com.itemrental.rentalService.rental.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RentalPostUpdateRequestDto {
  String title;
  String description;
  Long price;
  String location;
  String category;
}
