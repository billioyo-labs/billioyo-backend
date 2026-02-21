package com.itemrental.billioyo.domain.rental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;


@Service
public class ImageAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(ImageAnalysisService.class);
    private final ChatClient chatClient;

    public ImageAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateDescription(String s3PublicUrl) {
        try {
            Resource imageResource = new UrlResource(s3PublicUrl);

            String promptText = "이미지 분석 후 다음 JSON 형식으로만 답하세요. 마크다운 기호(```)는 절대 쓰지 마세요. " +
                "{" +
                "\"title\": \"물건이름\"," +
                "\"description\": \"상세설명\"," +
                "\"price\": 10000," +
                "\"condition\": \"상태\"" +
                "}";

            String response = chatClient.prompt()
                .user(u -> u.text(promptText).media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .call()
                .content();

            if (response != null) {
                response = response.replaceAll("```json|```", "").trim();
            }
            log.info("{}", response);

            return response;
        } catch (Exception e) {
            return "{\"error\": \"분석 실패\"}";
        }
    }
}