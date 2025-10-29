package com.itemrental.rentalService.controller;


import com.itemrental.rentalService.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.dto.response.RentalPostListResponseDto;
import com.itemrental.rentalService.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class RentalController {

  private final RentalService rentalService;

  @GetMapping
  public ResponseEntity<Page<RentalPostListResponseDto>> getPosts(
      @PageableDefault(size = 10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
  ){
    return ResponseEntity.ok(rentalService.getPosts(pageable));
  }
}
