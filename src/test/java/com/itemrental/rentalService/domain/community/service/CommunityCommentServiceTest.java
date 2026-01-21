package com.itemrental.rentalService.domain.community.service;

import com.itemrental.rentalService.domain.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.domain.community.entity.CommunityComment;
import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.repository.CommunityCommentRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityCommentServiceTest {

    @InjectMocks
    private CommunityCommentService commentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommunityCommentRepository commentRepository;

    @Mock
    private CommunityPostRepository postRepository;

    @Mock
    private Principal principal;

    private User user;
    private CommunityPost post;
    private String email = "test@test.com";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email(email)
                .username("tester")
                .build();

        post = CommunityPost.createPost(user, "제목", "내용", "FREE", "서울", null);
        ReflectionTestUtils.setField(post, "id", 100L);

        lenient().when(principal.getName()).thenReturn(email);
    }

    @Test
    @DisplayName("성공: 댓글 생성 - 정상 요청 시 댓글이 저장되고 게시글의 댓글 수가 증가한다")
    void createComment_Success() {
        // given
        CommentCreateRequestDto dto = new CommentCreateRequestDto();
        dto.setComment("테스트 댓글입니다.");
        int initialCommentCount = post.getCommentCount();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(100L)).willReturn(Optional.of(post));

        // when
        commentService.createCommunityComment(dto, 100L, principal);

        // then
        assertThat(post.getCommentCount()).isEqualTo(initialCommentCount + 1);
        verify(commentRepository, times(1)).save(any(CommunityComment.class));
    }

    @Test
    @DisplayName("실패: 댓글 생성 - 존재하지 않는 사용자인 경우 예외가 발생한다")
    void createComment_Fail_UserNotFound() {
        // given
        CommentCreateRequestDto dto = new CommentCreateRequestDto();
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createCommunityComment(dto, 100L, principal))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

        verify(postRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("실패: 댓글 생성 - 존재하지 않는 게시글인 경우 예외가 발생한다")
    void createComment_Fail_PostNotFound() {
        // given
        CommentCreateRequestDto dto = new CommentCreateRequestDto();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createCommunityComment(dto, 999L, principal))
                .isInstanceOf(PostNotFoundException.class);

        verify(commentRepository, never()).save(any());
    }
}
