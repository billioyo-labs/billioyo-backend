package com.itemrental.rentalService.domain.community.service;

import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.entity.CommunityPostBookmark;
import com.itemrental.rentalService.domain.community.repository.CommunityPostBookmarkRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostLikeRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityPostInteractionServiceTest {

    @InjectMocks
    private CommunityPostInteractionService interactionService;

    @Mock private CommunityPostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommunityPostLikeRepository likeRepo;
    @Mock private Principal principal;
    @Mock private CommunityPostBookmarkRepository communityPostBookmarkRepository;

    private User user;
    private CommunityPost post;
    private String email = "test@test.com";

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email(email).build();
        post = CommunityPost.createPost(user, "제목", "내용", "FREE", "서울", null);
        ReflectionTestUtils.setField(post, "id", 100L);

        lenient().when(principal.getName()).thenReturn(email);
    }

    @Test
    @DisplayName("좋아요 토글 - 기존에 없으면 추가")
    void toggleLike_Create() {
        // given
        when(principal.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(likeRepo.existsByUser_IdAndPost_Id(any(), any())).thenReturn(false);

        // when
        int count = interactionService.toggleLike(1L, principal);

        // then
        assertThat(count).isEqualTo(1);
        verify(likeRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("좋아요 토글 - 이미 있으면 삭제")
    void toggleLike_Delete() {
        // given
        post.addLike();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(likeRepo.existsByUser_IdAndPost_Id(any(), any())).thenReturn(true);

        // when
        int count = interactionService.toggleLike(1L, principal);

        // then
        assertThat(count).isZero();
        verify(likeRepo, times(1)).deleteByUser_IdAndPost_Id(any(), any());
    }

    @Test
    @DisplayName("성공: 북마크 등록 - 기존에 북마크가 없으면 새로 저장하고 '북마크'를 반환한다")
    void toggleBookmark_Create() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(100L)).willReturn(Optional.of(post));
        given(communityPostBookmarkRepository.existsByUser_IdAndPost_Id(user.getId(), post.getId())).willReturn(false);

        // when
        String result = interactionService.toggleBookmark(100L, principal);

        // then
        assertThat(result).isEqualTo("북마크");
        verify(communityPostBookmarkRepository, times(1)).save(any(CommunityPostBookmark.class));
        verify(communityPostBookmarkRepository, never()).deleteByUser_IdAndPost_Id(anyLong(), anyLong());
    }

    @Test
    @DisplayName("성공: 북마크 취소 - 이미 북마크가 존재하면 삭제하고 '북마크 취소'를 반환한다")
    void toggleBookmark_Delete() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(100L)).willReturn(Optional.of(post));
        given(communityPostBookmarkRepository.existsByUser_IdAndPost_Id(user.getId(), post.getId())).willReturn(true);

        // when
        String result = interactionService.toggleBookmark(100L, principal);

        // then
        assertThat(result).isEqualTo("북마크 취소");
        verify(communityPostBookmarkRepository, times(1)).deleteByUser_IdAndPost_Id(user.getId(), post.getId());
        verify(communityPostBookmarkRepository, never()).save(any(CommunityPostBookmark.class));
    }

    @Test
    @DisplayName("실패: 북마크 토글 - 존재하지 않는 게시글일 경우 예외가 발생한다")
    void toggleBookmark_Fail_PostNotFound() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> interactionService.toggleBookmark(999L, principal))
                .isInstanceOf(PostNotFoundException.class);

        verify(communityPostBookmarkRepository, never()).existsByUser_IdAndPost_Id(anyLong(), anyLong());
    }
}
