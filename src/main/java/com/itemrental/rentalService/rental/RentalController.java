package com.itemrental.rentalService.rental;


import com.itemrental.rentalService.dto.ApiResponse;
import com.itemrental.rentalService.rental.dto.*;
import com.itemrental.rentalService.rental.service.PostInteractionService;
import com.itemrental.rentalService.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class RentalController {

  private final RentalService rentalService;
  private final PostInteractionService interactionService;

  //대여 게시글 생성
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createPost(@RequestBody RentalPostCreateRequestDto dto) {
    return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", rentalService.createRentalPost(dto)));
  }
  //대여 게시글 상세 조회
  @GetMapping("/{postId}")
  public ResponseEntity<RentalPostReadResponseDto> getRentalPost(@PathVariable Long postId) {
    return ResponseEntity.ok(rentalService.getRentalPost(postId));
  }
  //대여 게시글 수정
  @PutMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> updateRentalPost(@PathVariable Long postId, @RequestBody RentalPostUpdateRequestDto dto) {
    rentalService.updateRentalPost(postId, dto);
    return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
  }
  //대여 게시글 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> deleteRentalPost(@PathVariable Long postId) {
    rentalService.deleteRentalPost(postId);
    return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다"));
  }


  @GetMapping
  public ResponseEntity<Page<RentalPostListResponseDto>> getPosts(
      @PageableDefault(size = 10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
  ){
    return ResponseEntity.ok(rentalService.getPosts(pageable));
  }


  //게시글 리뷰 작성
  public ResponseEntity<ApiResponse<Void>> createPostReview(@RequestBody ReviewCreateRequestDto dto, @RequestParam Long postId) {
    interactionService.createPostReview(dto, postId);
    return ResponseEntity.ok(ApiResponse.success("리뷰가 등록되었습니다"));
  }

}
