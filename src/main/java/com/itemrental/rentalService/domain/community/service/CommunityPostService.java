package com.itemrental.rentalService.domain.community.service;

import com.itemrental.rentalService.domain.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostSearchRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.domain.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.entity.CommunityPostImage;
import com.itemrental.rentalService.domain.community.repository.CommunityPostImageRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import com.itemrental.rentalService.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final UserRepository userRepository;
    private final CommunityPostImageRepository imageRepository;
    private final SecurityUtil securityUtil;

    //게시글 생성
    @Transactional
    public CommunityPostCreateResponseDto createCommunityPost(CommunityPostCreateRequestDto dto) {
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username).get();

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setLocation(dto.getLocation());

        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            post.setPosition(new Position(dto.getLatitude(), dto.getLongitude()));
        }

        repository.save(post);

        if (dto.getImageUrls() != null) {
            for (String imageUrl : dto.getImageUrls()) {
                CommunityPostImage image = new CommunityPostImage();
                image.setPost(post);
                image.setImageUrl(imageUrl);
                imageRepository.save(image);
            }
        }

        return new CommunityPostCreateResponseDto(
            post.getId(),
            post.getCategory(),
            user.getUsername(),
            post.getTitle(),
            post.getContent()
        );
    }

    //게시글 상세 조회
    @Transactional
    public CommunityPostReadResponseDto getCommunityPost(Long postId) {
        CommunityPost post = repository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        User user = post.getUser();
        post.setViewCount(post.getViewCount() + 1);

        List<CommentResponseDto> comments = post.getComments().stream().map(
            comment -> new CommentResponseDto(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getComment(),
                comment.getCreatedAt()
            )).toList();

        return new CommunityPostReadResponseDto(
            post.getCategory(),
            user.getUsername(),
            post.getTitle(),
            post.getContent(),
            post.getCreatedAt(),
            post.getImages(),
            post.getViewCount(),
            post.getLikeCount(),
            comments,
            post.getLocation()
        );
    }

    //게시글 수정
    @Transactional
    public void updateCommunityPost(Long postId, CommunityPostUpdateRequestDto dto) {
        String username = securityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

        CommunityPost post = repository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        User postUser = post.getUser();

        if (!postUser.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        post.getImages().clear();

        if (dto.getImageUrls() != null) {
            for (String imageUrl : dto.getImageUrls()) {
                CommunityPostImage image = new CommunityPostImage();
                image.setPost(post);
                image.setImageUrl(imageUrl);
                imageRepository.save(image);
            }
        }
    }

    //게시글 삭제
    @Transactional
    public void deleteCommunityPost(Long postId) {
        String username = securityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));

        CommunityPost post = repository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        ;
        User postUser = post.getUser();

        if (!postUser.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }
        repository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<CommunityPostListResponseDto> getPostList(Pageable pageable, CommunityPostSearchRequestDto searchDto) {
        Page<CommunityPost> page;

        if (searchDto.getLat() != null && searchDto.getLng() != null && searchDto.getDistance() != null) {
            page = repository.findWithinDistance(
                searchDto.getLat(),
                searchDto.getLng(),
                searchDto.getDistance(),
                pageable
            );
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(post -> new CommunityPostListResponseDto(
            post.getId(),
            post.getCategory(),
            post.getTitle(),
            post.getUser().getUsername(),
            post.getLikeCount(),
            post.getViewCount(),
            post.getCommentCount(),
            post.getCreatedAt(),
            (post.getImages() == null || post.getImages().isEmpty())
                ? null : post.getImages().get(0).getImageUrl(),
            post.getLocation()
        ));
    }

    //게시글 검색
    @Transactional(readOnly = true)
    public List<CommunityPostListResponseDto> searchPosts(String keyword) {

        return repository.findByTitleContainingOrContentContaining(keyword, keyword).stream().map(
            post -> new CommunityPostListResponseDto(
                post.getId(),
                post.getCategory(),
                post.getTitle(),
                post.getUser().getUsername(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl(),
                post.getLocation()
            )).toList();
    }
}


