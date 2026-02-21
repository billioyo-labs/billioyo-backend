package com.itemrental.billioyo.global.infra;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Tag(name = "S3 이미지 업로드", description = "S3 이미지 업로드 API")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(
        summary = "S3 업로드용 Presigned URL 발급",
        description =
            "사용자가 이미지를 업로드하기 전에 호출되는 API입니다. S3에 직접 업로드할 수 있는 Presigned URL을 발급합니다."
    )
    @PostMapping("/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String filename) {
        String presignedUrl = s3Service.createPresignedUrl(filename);
        return ResponseEntity.ok(presignedUrl);
    }
}
