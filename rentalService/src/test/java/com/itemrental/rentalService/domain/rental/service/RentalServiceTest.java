package com.itemrental.rentalService.domain.rental.service;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.repository.OrderRepository;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostCreateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.rentalService.domain.rental.dto.request.RentalPostUpdateRequestDto;
import com.itemrental.rentalService.domain.rental.dto.response.RentalPostReadResponseDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostLikeRepository;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import com.itemrental.rentalService.domain.settlement.repository.SettlementItemRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostLikeRepository likeRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private SettlementItemRepository settlementItemRepository;

    private DefaultSearchStrategy defaultSearchStrategy = mock(DefaultSearchStrategy.class);
    private DistanceSearchStrategy distanceSearchStrategy = mock(DistanceSearchStrategy.class);

    private RentalService rentalService;

    private User user;
    private String email = "test@test.com";

    @BeforeEach
    void setUp() {
        List<PostSearchStrategy> strategyList = List.of(defaultSearchStrategy, distanceSearchStrategy);

        rentalService = new RentalService(
                postRepository, userRepository, likeRepository,
                orderRepository, settlementItemRepository, strategyList
        );
        user = User.builder().id(1L).email(email).nickName("테스터").build();
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        RentalPostCreateRequestDto dto = new RentalPostCreateRequestDto("제목", "내용", 1000L, "서울", 37.5, 127.0, "가전", List.of("url1"));
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "서울", new Position(37.5, 127.0), "가전");
        ReflectionTestUtils.setField(post, "id", 100L);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(postRepository.save(any())).willReturn(post);

        Long result = rentalService.createRentalPost(dto, email);

        assertThat(result).isEqualTo(100L);
    }

    @Test
    @DisplayName("게시물 생성 - 이미지 리스트가 null인 경우")
    void createRentalPost_NoImages() {
        // given
        User user = User.builder().id(1L).email("test@test.com").build();
        RentalPostCreateRequestDto dto = new RentalPostCreateRequestDto("제목", "내용", 1000L, "서울", 37.5, 127.0, "가전", null);

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(postRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        rentalService.createRentalPost(dto, "test@test.com");

        // then
        verify(postRepository).save(any());
    }

    @Test
    @DisplayName("상세조회 성공 - 로그인 유저가 좋아요를 누른 경우")
    void getPost_Success_IsLiked() {
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "서울", null, "가전");
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(likeRepository.existsByUserAndRentalPost(user, post)).willReturn(true);

        RentalPostReadResponseDto result = rentalService.getRentalPost(1L, email);

        assertThat(result.isLiked()).isTrue();
        assertThat(post.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("상세 조회 - 로그인한 유저가 좋아요를 누르지 않은 경우")
    void getRentalPost_NotLiked() {
        // given
        User user = User.builder().email("test@test.com").build();
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "위치", null, "카테고리");

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(likeRepository.existsByUserAndRentalPost(user, post)).willReturn(false);

        // when
        RentalPostReadResponseDto result = rentalService.getRentalPost(1L, "test@test.com");

        // then
        assertThat(result.isLiked()).isFalse();
    }

    @Test
    @DisplayName("상세조회 성공 - 비로그인 유저")
    void getPost_Success_Anonymous() {
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "서울", null, "가전");
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        RentalPostReadResponseDto result = rentalService.getRentalPost(1L, null);

        assertThat(result.isLiked()).isFalse();
    }

    @Test
    @DisplayName("상세 조회 실패 - 존재하지 않는 게시글 아이디")
    void getRentalPost_NotFound() {
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.getRentalPost(999L, "user@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("게시글 수정 성공 - 작성자가 일치할 때")
    void updateRentalPost_Success() {
        // given
        String email = "author@test.com";
        User author = User.builder().email(email).build();
        RentalPost post = RentalPost.create(author, "원본 제목", "원본 내용", 1000L, "서울", new Position(37.5, 127.0), "가전");

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        RentalPostUpdateRequestDto updateDto = new RentalPostUpdateRequestDto(
                "수정된 제목", "수정된 내용", 2000L, "부산", "의류"
        );

        // when
        rentalService.updateRentalPost(1L, updateDto, email);

        // then
        assertThat(post.getTitle()).isEqualTo("수정된 제목");
        assertThat(post.getPrice()).isEqualTo(2000L);
        assertThat(post.getCategory()).isEqualTo("의류");
    }

    @Test
    @DisplayName("수정 실패 - 작성자가 아님")
    void updatePost_Fail_Author() {
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "서울", null, "가전");
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        RentalPostUpdateRequestDto dto = new RentalPostUpdateRequestDto("수정", "내용", 2000L, "인천", "가구");

        assertThatThrownBy(() -> rentalService.updateRentalPost(1L, dto, "wrong@email.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("인기글 조회 성공")
    void getPopularPosts_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        given(postRepository.findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of()));

        rentalService.getPopularPosts(pageable);

        verify(postRepository).findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("검색 전략 선택 - 위경도가 있을 때 DistanceStrategy 사용")
    void getPosts_DistanceStrategy() {
        RentalPostSearchRequestDto searchDto = new RentalPostSearchRequestDto(37.5, 127.0, 5.0);
        Pageable pageable = PageRequest.of(0, 10);

        given(distanceSearchStrategy.search(any(), any())).willReturn(new PageImpl<>(List.of()));

        rentalService.getPosts(pageable, searchDto);

        verify(distanceSearchStrategy).search(any(), any());
        verify(defaultSearchStrategy, never()).search(any(), any());
    }

    @Test
    @DisplayName("목록 조회 - 위도/경도가 없을 때 DefaultStrategy 사용")
    void getPosts_DefaultStrategy() {
        // given
        RentalPostSearchRequestDto searchDto = new RentalPostSearchRequestDto(null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        given(defaultSearchStrategy.search(any(), any())).willReturn(Page.empty());

        // when
        rentalService.getPosts(pageable, searchDto);

        // then
        verify(defaultSearchStrategy).search(eq(searchDto), eq(pageable));
        verify(distanceSearchStrategy, never()).search(any(), any());
    }

    @Test
    @DisplayName("반납 성공 - 정산 아이템 생성 확인")
    void returnRental_Success() {
        Order order = Order.builder().id(1L).user(user).postId(100L).amount(5000L).build();
        RentalPost post = RentalPost.create(user, "제목", "내용", 1000L, "서울", null, "가전");

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(postRepository.findById(100L)).willReturn(Optional.of(post));

        rentalService.returnRental(1L, email);

        assertThat(post.isStatus()).isFalse();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.RETURNED);
        verify(settlementItemRepository).save(any());
    }

    @Test
    @DisplayName("반납 실패 - 주문자가 아님")
    void returnRental_Fail_NotOwner() {
        User anotherUser = User.builder().id(2L).email("other@test.com").build();
        Order order = Order.builder().id(1L).user(anotherUser).build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> rentalService.returnRental(1L, email))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("게시글 삭제 성공 - 작성자가 일치할 때")
    void deleteRentalPost_Success() {
        // given
        String email = "author@test.com";
        User author = User.builder().email(email).build();
        RentalPost post = RentalPost.create(author, "제목", "내용", 1000L, "서울", null, "가전");

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        rentalService.deleteRentalPost(1L, email);

        // then
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글일 때")
    void deleteRentalPost_Fail_NotFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rentalService.deleteRentalPost(999L, "any@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }
}
