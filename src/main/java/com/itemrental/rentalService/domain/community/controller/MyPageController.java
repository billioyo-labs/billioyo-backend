package com.itemrental.rentalService.domain.community.controller;


import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.service.CommunityPostInteractionService;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.domain.rental.service.PostInteractionService;
import com.itemrental.rentalService.domain.user.dto.UpdateUserDto;
import com.itemrental.rentalService.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final PostInteractionService postInteractionService;


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

    @GetMapping("/products")
    public ResponseEntity<Page<RentalPostListResponseDto>> getProducts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postInteractionService.getMyPosts(pageable));
    }

}
