package com.itemrental.rentalService.upload;

import com.itemrental.rentalService.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;
  //이미지 링크 생성
  @PostMapping("/presigned-url")
  public ResponseEntity<String> getPresignedUrl(@RequestParam String filename) {
    String presignedUrl = s3Service.createPresignedUrl(filename);
    return ResponseEntity.ok(presignedUrl);
  }
}
