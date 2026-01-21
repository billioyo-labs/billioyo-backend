package com.itemrental.rentalService.domain.community.service;

import com.itemrental.rentalService.domain.community.dto.request.CommunityPostCreateRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostSearchRequestDto;
import com.itemrental.rentalService.domain.community.dto.request.CommunityPostUpdateRequestDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostCreateResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostListResponseDto;
import com.itemrental.rentalService.domain.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.repository.CommunityPostImageRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.rental.exception.PostNotFoundException;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.exception.UserNotFoundException;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityPostImageRepository imageRepository;

    //게시글 생성
    @Transactional
    public CommunityPostCreateResponseDto createCommunityPost(CommunityPostCreateRequestDto dto, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));

        Position position = (dto.getLatitude() != null) ? new Position(dto.getLatitude(), dto.getLongitude()) : null;
        CommunityPost post = CommunityPost.createPost(user, dto.getTitle(), dto.getContent(), dto.getCategory(), dto.getLocation(), position);
        post.updateImages(dto.getImageUrls());

        return new CommunityPostCreateResponseDto(postRepository.save(post));
    }

    //게시글 상세 조회
    @Transactional
    public CommunityPostReadResponseDto getCommunityPost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        post.increaseViewCount();
        return CommunityPostReadResponseDto.from(post);
    }

    //게시글 수정
    @Transactional
    public void updateCommunityPost(Long postId, CommunityPostUpdateRequestDto dto, Principal principal) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.validateAuthor(principal.getName());
        post.update(dto.getTitle(), dto.getContent(), dto.getImageUrls());
    }

    //게시글 삭제
    @Transactional
    public void deleteCommunityPost(Long postId, Principal principal) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));
        post.validateAuthor(principal.getName());
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<CommunityPostListResponseDto> getPostList(Pageable pageable, CommunityPostSearchRequestDto searchDto) {
        Page<CommunityPost> page = (searchDto.hasLocationInfo())
                ? postRepository.findWithinDistance(searchDto.getLat(), searchDto.getLng(), searchDto.getDistance(), pageable)
                : postRepository.findAll(pageable);
        return page.map(CommunityPostListResponseDto::new);
    }

    //게시글 검색
    @Transactional(readOnly = true)
    public List<CommunityPostListResponseDto> searchPosts(String keyword) {

        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword)
                .stream()
                .map(CommunityPostListResponseDto::new)
                .toList();
    }
}


