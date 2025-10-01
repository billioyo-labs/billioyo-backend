package com.itemrental.rentalService.community;


import com.itemrental.rentalService.community.dto.response.CommunityImagePresignedUrlResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${AWS_S3_BUCKET}")
  private String bucket;
  @Value("${AWS_REGION}")
  private String region;
  private final S3Presigner s3Presigner;
  private static final String COMMUNITY_IMAGE_DIRECTORY = "community-image/";

  public CommunityImagePresignedUrlResponseDto createCommunityImagePresignedUrl(String originalFileName) {
    String objectKey = buildObjectKey(originalFileName);
    String uploadUrl = createPresignedUrl(objectKey);
    String imageUrl = buildImageUrl(objectKey);
    return new CommunityImagePresignedUrlResponseDto(originalFileName, objectKey, uploadUrl, imageUrl);
  }

  public List<CommunityImagePresignedUrlResponseDto> createCommunityImagePresignedUrls(List<String> fileNames) {
    if (fileNames == null || fileNames.isEmpty()) {
      throw new IllegalArgumentException("최소 하나 이상의 파일 이름이 필요합니다.");
    }

    return fileNames.stream()
        .map(this::createCommunityImagePresignedUrl)
        .toList();
  }

  private String buildObjectKey(String originalFileName) {
    String extension = extractExtension(originalFileName);
    String uniqueName = UUID.randomUUID().toString().replace("-", "");
    return COMMUNITY_IMAGE_DIRECTORY + uniqueName + extension;
  }

  private String extractExtension(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return "";
    }

    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
      return "";
    }

    return fileName.substring(lastDotIndex).toLowerCase();
  }

  private String buildImageUrl(String objectKey) {
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;
  }

  private String createPresignedUrl(String path) {
    var putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(path)
        .build();
    var preSignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(3))
        .putObjectRequest(putObjectRequest)
        .build();
    return s3Presigner.presignPutObject(preSignRequest).url().toString();
  }

}