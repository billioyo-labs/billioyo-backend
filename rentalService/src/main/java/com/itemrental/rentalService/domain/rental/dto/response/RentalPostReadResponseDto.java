package com.itemrental.rentalService.domain.rental.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itemrental.rentalService.domain.rental.entity.Image;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.user.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
@Builder
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
    private List<String> imageUrls;
    private Long reviewsCount;
    private double rating;
    private double likeCount;
    @JsonProperty("isLiked")
    private boolean isLiked;
    private UserSummary seller;

    public static RentalPostReadResponseDto from(RentalPost post, boolean isLiked) {
        return RentalPostReadResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .location(post.getLocation())
                .latitude(post.getPosition() != null ? post.getPosition().getLatitude() : null)
                .longitude(post.getPosition() != null ? post.getPosition().getLongitude() : null)
                .status(post.isStatus())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .username(post.getUser().getUsername())
                .category(post.getCategory())
                .imageUrls(post.getImages().stream()
                        .map(Image::getImageUrl)
                        .toList())
                .reviewsCount(post.getReviewsCount())
                .rating(post.getRating())
                .likeCount((double) post.getLikeCount())
                .isLiked(isLiked)
                .seller(UserSummary.from(post.getUser()))
                .build();
    }
}