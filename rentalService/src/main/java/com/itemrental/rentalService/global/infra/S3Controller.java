package com.itemrental.rentalService.global.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
