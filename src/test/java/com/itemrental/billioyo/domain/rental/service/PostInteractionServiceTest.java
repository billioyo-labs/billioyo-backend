package com.itemrental.billioyo.domain.rental.service;

import com.itemrental.billioyo.domain.rental.dto.request.ReviewCreateRequestDto;
import com.itemrental.billioyo.domain.rental.dto.response.RentalPostListResponseDto;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.entity.RentalPostBookmark;
import com.itemrental.billioyo.domain.rental.entity.RentalPostLike;
import com.itemrental.billioyo.domain.rental.entity.Review;
import com.itemrental.billioyo.domain.rental.exception.PostNotFoundException;
import com.itemrental.billioyo.domain.rental.repository.PostBookmarkRepository;
import com.itemrental.billioyo.domain.rental.repository.PostLikeRepository;
import com.itemrental.billioyo.domain.rental.repository.PostRepository;
import com.itemrental.billioyo.domain.rental.repository.PostReviewRepository;
import com.itemrental.billioyo.domain.user.entity.User;
import com.itemrental.billioyo.domain.user.exception.UserNotFoundException;
import com.itemrental.billioyo.domain.user.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostInteractionServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostReviewRepository reviewRepository;
    @Mock private PostBookmarkRepository bmRepository;
    @Mock private PostLikeRepository likeRepository;

    @InjectMocks private PostInteractionService interactionService;

    private User user;
    private RentalPost post;
    private String email = "test@test.com";
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email(email).nickName("테스터").build();
        post = RentalPost.create(user, "제목", "내용", 1000L, "위치", null, "카테고리");
    }

    @Test
    @DisplayName("리뷰 작성 성공 - 게시글의 리뷰수와 평점이 갱신되어야 한다")
    void createReview_Success() {
        // given
        ReviewCreateRequestDto dto = new ReviewCreateRequestDto("좋아요", 5);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        interactionService.createPostReview(dto, postId, email);

        // then
        assertThat(post.getReviewsCount()).isEqualTo(1L);
        assertThat(post.getRating()).isEqualTo(5.0);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 존재하지 않는 사용자의 경우")
    void createReview_UserNotFound() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> interactionService.createPostReview(new ReviewCreateRequestDto("내용", 5), postId, email))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("판매자의 다른 상품 목록을 조회한다")
    void getSellerPosts_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalPost> page = new PageImpl<>(List.of(post));
        given(postRepository.findByUserId(1L, pageable)).willReturn(page);

        // when
        Page<RentalPostListResponseDto> result = interactionService.getSellerPosts(pageable, 1L);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("좋아요 토글 - 기존에 없으면 생성하고 카운트를 올린다")
    void toggleLike_Create() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(likeRepository.findByUserAndRentalPost(user, post)).willReturn(Optional.empty());

        // when
        Long likeCount = interactionService.toggleLike(postId, email);

        // then
        assertThat(likeCount).isEqualTo(1L);
        verify(likeRepository, times(1)).save(any(RentalPostLike.class));
    }

    @Test
    @DisplayName("좋아요 토글 - 이미 있으면 삭제하고 카운트를 내린다")
    void toggleLike_Delete() {
        // given
        post.toggleLike(true);
        RentalPostLike like = RentalPostLike.create(user, post);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(likeRepository.findByUserAndRentalPost(user, post)).willReturn(Optional.of(like));

        // when
        Long likeCount = interactionService.toggleLike(postId, email);

        // then
        assertThat(likeCount).isEqualTo(0L);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    @DisplayName("북마크 토글 - 기존에 없으면 추가 메시지를 반환한다")
    void toggleBookmark_Create() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(bmRepository.findByUserAndRentalPost(user, post)).willReturn(Optional.empty());

        // when
        String result = interactionService.toggleBookmark(postId, email);

        // then
        assertThat(result).isEqualTo("북마크");
        verify(bmRepository, times(1)).save(any(RentalPostBookmark.class));
    }

    @Test
    @DisplayName("북마크 토글 - 이미 있으면 취소 메시지를 반환한다")
    void toggleBookmark_Delete() {
        // given
        RentalPostBookmark bookmark = RentalPostBookmark.create(user, post);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(bmRepository.findByUserAndRentalPost(user, post)).willReturn(Optional.of(bookmark));

        // when
        String result = interactionService.toggleBookmark(postId, email);

        // then
        assertThat(result).isEqualTo("북마크 취소");
        verify(bmRepository, times(1)).delete(bookmark);
    }

    @Test
    @DisplayName("좋아요 실패 - 존재하지 않는 게시글")
    void toggleLike_PostNotFound() {
        // given
        User user = User.builder().email("test@test.com").build();
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> interactionService.toggleLike(999L, "test@test.com"))
                .isInstanceOf(PostNotFoundException.class);
    }
}
