package com.itemrental.rentalService.community;


import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.service.CommunityPostInteractionService;
import com.itemrental.rentalService.dto.UpdateUserDto;
import com.itemrental.rentalService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final CommunityPostInteractionService interactionService;
  private final UserService userService;


  @GetMapping("/likes")
  public ResponseEntity<List<CommunityPostReadResponseDto>> getLikePosts() {
    return ResponseEntity.ok(interactionService.getLikedPosts());
  }
  @GetMapping("/bms")
  public ResponseEntity<List<CommunityPostReadResponseDto>> getBmPosts() {
    return ResponseEntity.ok(interactionService.getBmPosts());
  }
  @GetMapping("/profile")
  public ResponseEntity<UpdateUserDto> getProfile() {
    return ResponseEntity.ok(userService.getProfile());
  }
}
