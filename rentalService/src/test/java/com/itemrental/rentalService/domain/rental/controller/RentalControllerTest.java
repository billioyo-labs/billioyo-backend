package com.itemrental.rentalService.domain.rental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.rental.dto.request.*;
import com.itemrental.rentalService.domain.rental.dto.response.*;
import com.itemrental.rentalService.domain.rental.service.ImageAnalysisService;
import com.itemrental.rentalService.domain.rental.service.PostInteractionService;
import com.itemrental.rentalService.domain.rental.service.RentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = RentalController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class
        }
)
class RentalControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RentalService rentalService;
    @MockitoBean private PostInteractionService interactionService;
    @MockitoBean private ImageAnalysisService imageAnalysisService;

    // 1. 게시글 생성
    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() throws Exception {
        RentalPostCreateRequestDto dto = new RentalPostCreateRequestDto("제목", "내용", 1000L, "서울", 37.5, 127.0, "가전", List.of("url1"));
        given(rentalService.createRentalPost(any(), anyString())).willReturn(1L);
        Principal mockPrincipal = () -> "test@test.com";

        mockMvc.perform(post("/products").principal(mockPrincipal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1L));
    }

    // 2. 상세 조회 (로그인 시)
    @Test
    @DisplayName("상세조회 성공 - 로그인 사용자")
    void getRentalPost_Authenticated() throws Exception {
        RentalPostReadResponseDto response = RentalPostReadResponseDto.builder().id(1L).build();
        given(rentalService.getRentalPost(eq(1L), eq("test@test.com"))).willReturn(response);


        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(get("/products/1").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    // 3. 상세 조회 (비로그인 시 - principal null 케이스)
    @Test
    @DisplayName("상세조회 성공 - 비로그인 사용자")
    void getRentalPost_Anonymous() throws Exception {
        // given
        RentalPostReadResponseDto response = RentalPostReadResponseDto.builder()
                .id(1L)
                .title("테스트 상품")
                .build();
        given(rentalService.getRentalPost(eq(1L), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/products/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("테스트 상품"));
    }

    // 4. 게시글 수정
    @Test
    @DisplayName("게시글 수정 성공")
    void updateRentalPost_Success() throws Exception {
        RentalPostCreateRequestDto dto = new RentalPostCreateRequestDto("제목", "내용", 1000L, "서울", 37.5, 127.0, "가전", List.of("url1"));
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(put("/products/1").principal(mockPrincipal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // 5. 게시글 삭제
    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("게시글 삭제 성공")
    void deleteRentalPost_Success() throws Exception {
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(delete("/products/1").principal(mockPrincipal).with(csrf()))
                .andExpect(status().isOk());
    }

    // 6. 목록 조회
    @Test
    @DisplayName("목록 조회 성공")
    void getPosts_Success() throws Exception {
        // given
        RentalPostListResponseDto dto = new RentalPostListResponseDto(
                1L, "닉네임", "제목", 1000L, false, LocalDateTime.now(), "url", 0.0, 0L
        );
        Page<RentalPostListResponseDto> page = new PageImpl<>(List.of(dto));
        given(rentalService.getPosts(any(Pageable.class), any(RentalPostSearchRequestDto.class))).willReturn(page);

        // when & then
        mockMvc.perform(get("/products").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    // 7. 리뷰 작성
    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("리뷰 작성 성공")
    void createPostReview_Success() throws Exception {
        ReviewCreateRequestDto dto = new ReviewCreateRequestDto("내용", 5);
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(post("/products/review/1").principal(mockPrincipal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // 8. 판매자 상품 조회
    @Test
    @DisplayName("판매자 상품 조회 성공")
    void getSellerPosts_Success() throws Exception {
        given(interactionService.getSellerPosts(any(Pageable.class), eq(1L)))
                .willReturn(new PageImpl<>(List.of()));
        Principal mockPrincipal = () -> "test@test.com";

        mockMvc.perform(get("/products/seller/1").principal(mockPrincipal))
                .andExpect(status().isOk());
    }

    // 9. 좋아요/북마크 토글
    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("좋아요 토글 성공")
    void likePost_Success() throws Exception {
        given(interactionService.toggleLike(eq(1L), anyString())).willReturn(10L);
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(post("/products/1/like").principal(mockPrincipal).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(10L));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("북마크 토글 성공")
    void bmPost_Success() throws Exception {
        given(interactionService.toggleBookmark(eq(1L), anyString())).willReturn("북마크");
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(post("/products/1/bm").principal(mockPrincipal).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("북마크"));
    }

    // 10. 인기글 조회
    @Test
    @DisplayName("인기글 조회 성공")
    void getPopularPosts_Success() throws Exception {
        given(rentalService.getPopularPosts(any(Pageable.class))).willReturn(new PageImpl<>(List.of()));
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(get("/products/popular").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").exists());
    }

    // 11. AI 이미지 분석
    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("AI 분석 성공")
    void analyzeImage_Success() throws Exception {
        Map<String, String> request = Map.of("imageUrl", "test-url");
        given(imageAnalysisService.generateDescription(eq("test-url"))).willReturn("AI 설명");
        Principal mockPrincipal = () -> "test@test.com";

        mockMvc.perform(post("/products/analyze-image").principal(mockPrincipal)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("AI 설명"));
    }

    // 12. 반납 처리
    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("반납 처리 성공")
    void returnRental_Success() throws Exception {
        Principal mockPrincipal = () -> "test@test.com";
        mockMvc.perform(post("/products/return/1").principal(mockPrincipal).with(csrf()))
                .andExpect(status().isOk());
    }
}
