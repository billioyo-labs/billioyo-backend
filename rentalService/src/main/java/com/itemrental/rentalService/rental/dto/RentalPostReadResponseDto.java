package com.itemrental.rentalService.rental.dto;
import com.itemrental.rentalService.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.community.entity.CommunityPostImage;
import com.itemrental.rentalService.entity.Image;
import com.itemrental.rentalService.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
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
}
