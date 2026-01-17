package com.itemrental.rentalService.domain.rental.dto.response;

import com.itemrental.rentalService.domain.rental.entity.RentalPost;
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

  public static RentalPostListResponseDto from(RentalPost post) {
    String firstImageUrl = post.getImages().isEmpty() ? null
            : post.getImages().get(0).getImageUrl();

    return new RentalPostListResponseDto(
            post.getId(),
            post.getUser().getNickName(),
            post.getTitle(),
            post.getPrice(),
            post.isStatus(),
            post.getCreatedAt(),
            firstImageUrl,
            post.getRating(),
            post.getReviewsCount()
    );
  }
}





