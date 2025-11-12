package com.itemrental.rentalService.rental.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.itemrental.rentalService.rental.entity.Image;
import lombok.*;

import java.math.BigDecimal;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdAt;
  private Long viewCount;
  private Long reportCount;
  private String username;
  private String category;
  private List<Image> imageUrls;
  private int reviewsCount;
  private BigDecimal rating;
  private SellerSummary seller;

  @Getter @Setter
  @NoArgsConstructor @AllArgsConstructor @Builder
  public static class SellerSummary {
    private Long id;
    private String email;
    private String name;
    private String nickname;
  }
}