package com.itemrental.rentalService.domain.rental.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.itemrental.rentalService.domain.user.dto.UserSummary;
import com.itemrental.rentalService.domain.rental.entity.Image;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
  private Double latitude;
  private Double longitude;
  private boolean status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdAt;
  private Long viewCount;
  private String username;
  private String category;
  private List<Image> imageUrls;
  private Long reviewsCount;
  private double rating;
  private double likeCount;
  @JsonProperty("isLiked")
  private boolean isLiked;
  private UserSummary seller;
}