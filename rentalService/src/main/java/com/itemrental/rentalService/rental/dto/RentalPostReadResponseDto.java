package com.itemrental.rentalService.rental.dto;
import com.itemrental.rentalService.rental.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
public class RentalPostReadResponseDto {
  private Long id;
  private String title;
  private String description;
  private Long price;
  private String location;
  private boolean status;
  private LocalDateTime createdAt;
  private Long viewCount;
  private Long reportCount;
  private String username;
  private String category;
  private List<Image> imageUrls;
}
