package com.itemrental.rentalService.rental.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RentalPostCreateRequestDto {
  private String title;
  private String description;
  private Long price;
  private String location;
  private String category;
  private List<String> imageUrls;
}
