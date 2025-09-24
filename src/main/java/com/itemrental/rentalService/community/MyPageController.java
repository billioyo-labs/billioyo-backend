package com.itemrental.rentalService.community;


import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.service.CommunityPostInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final CommunityPostInteractionService interactionService;


  @GetMapping("/likes")
  public ResponseEntity<List<CommunityPostReadResponseDto>> getLikePosts() {
    return ResponseEntity.ok(interactionService.getLikedPosts());
  }
}
