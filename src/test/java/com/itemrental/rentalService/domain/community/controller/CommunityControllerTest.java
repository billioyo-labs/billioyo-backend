package com.itemrental.rentalService.domain.community.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.domain.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.service.CommunityCommentService;
import com.itemrental.rentalService.domain.community.service.CommunityPostInteractionService;
import com.itemrental.rentalService.domain.community.service.CommunityPostService;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.global.infra.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityController.class)
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private CommunityPostService postService;
    @MockitoBean private CommunityPostInteractionService interactionService;
    @MockitoBean private CommunityCommentService commentService;
    @MockitoBean private S3Service s3Service;

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 성공")
    void createCommunityPost_Success() throws Exception {
        CommunityPostCreateRequestDto request = new CommunityPostCreateRequestDto("FREE", "제목", "내용", "서울", 37.0, 127.0, List.of());
        CommunityPostCreateResponseDto response = new CommunityPostCreateResponseDto(1L, "FREE", "user1", "제목", "내용");

        when(postService.createCommunityPost(any(), any())).thenReturn(response);

        mockMvc.perform(post("/community")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 생성되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 상세 조회 성공")
    void getPost_Success() throws Exception {
        List<CommentResponseDto> comments = List.of(
                new CommentResponseDto(1L, "commenter1", "첫 번째 댓글입니다.", LocalDateTime.now())
        );

        List<String> imageUrls = List.of("https://s3.bucket/image1.jpg", "https://s3.bucket/image2.jpg");

        CommunityPostReadResponseDto response = new CommunityPostReadResponseDto(
                "FREE",
                "authorName",
                "테스트 제목",
                "테스트 내용입니다.",
                LocalDateTime.now(),
                imageUrls,
                10,
                5,
                comments,
                "서울시 강남구"
        );

        when(postService.getCommunityPost(1L)).thenReturn(response);

        mockMvc.perform(get("/community/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("FREE"))
                .andExpect(jsonPath("$.username").value("authorName"))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.imageUrls[0]").value("https://s3.bucket/image1.jpg"))
                .andExpect(jsonPath("$.imageUrls.size()").value(2))
                .andExpect(jsonPath("$.comments[0].username").value("commenter1"))
                .andExpect(jsonPath("$.comments[0].comment").value("첫 번째 댓글입니다."))
                .andExpect(jsonPath("$.viewCount").value(10))
                .andExpect(jsonPath("$.likeCount").value(5))
                .andExpect(jsonPath("$.location").value("서울시 강남구"));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 성공")
    void updatePost_Success() throws Exception {
        CommunityPostUpdateRequestDto request = new CommunityPostUpdateRequestDto("수정제목", "수정내용", List.of());

        mockMvc.perform(put("/community/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 수정을 완료했습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() throws Exception {
        mockMvc.perform(delete("/community/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제를 완료했습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 토글 성공")
    void likePost_Success() throws Exception {
        when(interactionService.toggleLike(anyLong(), any())).thenReturn(5);

        mockMvc.perform(post("/community/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @WithMockUser
    @DisplayName("북마크 토글 성공")
    void bmPost_Success() throws Exception {
        when(interactionService.toggleBookmark(anyLong(), any())).thenReturn("북마크");

        mockMvc.perform(post("/community/1/bm").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크"));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 목록 조회 성공")
    void getPosts_Success() throws Exception {
        Page<CommunityPostListResponseDto> page = new PageImpl<>(List.of());
        when(postService.getPostList(any(), any())).thenReturn(page);

        mockMvc.perform(get("/community/posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 생성 성공")
    void createComment_Success() throws Exception {
        CommentCreateRequestDto request = new CommentCreateRequestDto();
        request.setComment("댓글내용");

        mockMvc.perform(post("/community/comment/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 등록되었습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("키워드 검색 성공")
    void searchPosts_Success() throws Exception {
        when(postService.searchPosts("키워드")).thenReturn(List.of());

        mockMvc.perform(get("/community/posts/search")
                        .param("keyword", "키워드"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("검색 결과를 반환합니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("상세 조회 실패 - 게시글 없음 (404)")
    void getPost_Fail_NotFound() throws Exception {
        when(postService.getCommunityPost(99L)).thenThrow(new PostNotFoundException(99L));

        mockMvc.perform(get("/community/99"))
                .andExpect(status().isNotFound());
    }
}
