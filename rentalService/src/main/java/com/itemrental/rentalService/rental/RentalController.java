package com.itemrental.rentalService.rental;


import com.itemrental.rentalService.dto.ApiResponse;
import com.itemrental.rentalService.rental.dto.RentalPostCreateRequestDto;
import com.itemrental.rentalService.rental.dto.RentalPostListResponseDto;
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

  //게시글 생성
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createPost(@RequestBody RentalPostCreateRequestDto dto) {
    return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", rentalService.createRentalPost(dto)));
  }


  @GetMapping
  public ResponseEntity<Page<RentalPostListResponseDto>> getPosts(
      @PageableDefault(size = 10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
  ){
    return ResponseEntity.ok(rentalService.getPosts(pageable));
  }
}
