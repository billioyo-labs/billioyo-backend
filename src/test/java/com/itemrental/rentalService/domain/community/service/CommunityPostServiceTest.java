package com.itemrental.rentalService.domain.community.service;

import com.itemrental.rentalService.domain.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostSearchRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.rental.exception.UnauthorizedAccessException;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityPostServiceTest {

    @InjectMocks
    private CommunityPostService postService;

    @Mock
    private CommunityPostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    private User user;
    private CommunityPost post;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").username("tester").build();
        post = CommunityPost.createPost(user, "제목", "내용", "FREE", "서울", new Position(37.0, 127.0));
        lenient().when(principal.getName()).thenReturn("test@test.com");
        ReflectionTestUtils.setField(post, "id", 1L);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        // given
        CommunityPostCreateRequestDto dto = new CommunityPostCreateRequestDto("FREE", "제목", "내용", "서울", 37.0, 127.0, List.of());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postRepository.save(any(CommunityPost.class))).thenReturn(post);

        // when
        CommunityPostCreateResponseDto result = postService.createCommunityPost(dto, principal);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        verify(postRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_Success(){
        CommunityPostUpdateRequestDto dto = new CommunityPostUpdateRequestDto("수정된 제목", "수정된 내용", List.of("url1"));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        when(principal.getName()).thenReturn("test@test.com");

        // when
        postService.updateCommunityPost(1L, dto, principal);

        // then
        assertThat(post.getTitle()).isEqualTo("수정된 제목");
        assertThat(post.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자 권한 없음")
    void updatePost_Fail_AccessDenied() {
        // given
        CommunityPostUpdateRequestDto dto = new CommunityPostUpdateRequestDto("수정제목", "수정내용", List.of());
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        when(principal.getName()).thenReturn("other@test.com");

        // when & then
        assertThatThrownBy(() -> postService.updateCommunityPost(1L, dto, principal))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("상세 조회 성공 - 조회수 증가 확인")
    void getPost_Success() {
        // given
        int initialViewCount = post.getViewCount();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        CommunityPostReadResponseDto result = postService.getCommunityPost(1L);

        // then
        assertThat(post.getViewCount()).isEqualTo(initialViewCount + 1);
        assertThat(result.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    void getPost_Fail_NotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getCommunityPost(99L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("성공: 게시글 삭제 - 작성자가 요청하면 삭제 메서드가 호출된다")
    void deletePost_Success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        when(principal.getName()).thenReturn("test@test.com");

        // when
        postService.deleteCommunityPost(1L, principal);

        // then
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    @DisplayName("성공: 위치 기반 목록 조회 - 위치 정보가 있으면 거리 검색 저장소를 호출한다")
    void getPostList_WithLocation() {
        // given
        CommunityPostSearchRequestDto searchDto = new CommunityPostSearchRequestDto();
        searchDto.setLat(37.0);
        searchDto.setLng(127.0);
        searchDto.setDistance(5.0);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CommunityPost> mockPage = new PageImpl<>(List.of(post));

        given(postRepository.findWithinDistance(anyDouble(), anyDouble(), anyDouble(), any()))
                .willReturn(mockPage);

        // when
        Page<CommunityPostListResponseDto> result = postService.getPostList(pageable, searchDto);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findWithinDistance(eq(37.0), eq(127.0), eq(5.0), eq(pageable));
    }

    @Test
    @DisplayName("성공: 일반 목록 조회 - 위치 정보가 없으면 전체 조회를 호출한다")
    void getPostList_WithoutLocation() {
        // given
        CommunityPostSearchRequestDto searchDto = new CommunityPostSearchRequestDto(); // 빈 객체
        Pageable pageable = PageRequest.of(0, 10);
        given(postRepository.findAll(pageable)).willReturn(new PageImpl<>(List.of(post)));

        // when
        postService.getPostList(pageable, searchDto);

        // then
        verify(postRepository).findAll(pageable);
        verify(postRepository, never()).findWithinDistance(anyDouble(), anyDouble(), anyDouble(), any());
    }

    @Test
    @DisplayName("성공: 키워드 검색 - 검색 결과가 DTO로 변환되어 반환된다")
    void searchPosts_Success() {
        // given
        String keyword = "테스트";
        given(postRepository.findByTitleContainingOrContentContaining(keyword, keyword))
                .willReturn(List.of(post));

        // when
        List<CommunityPostListResponseDto> result = postService.searchPosts(keyword);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getTitle()).isEqualTo(post.getTitle());
        verify(postRepository).findByTitleContainingOrContentContaining(keyword, keyword);
    }
}
