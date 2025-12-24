package com.itemrental.rentalService.community.service;


import com.itemrental.rentalService.community.dto.request.CommunityPostReportRequestDto;
import com.itemrental.rentalService.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.community.dto.response.CommunityPostReadResponseDto;
import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.entity.CommunityPostBookmark;
import com.itemrental.rentalService.community.entity.CommunityPostLike;
import com.itemrental.rentalService.community.entity.CommunityReport;
import com.itemrental.rentalService.community.repository.CommunityPostBookmarkRepository;
import com.itemrental.rentalService.community.repository.CommunityPostLikeRepository;
import com.itemrental.rentalService.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.community.repository.CommunityReportRepository;
import com.itemrental.rentalService.user.entity.User;
import com.itemrental.rentalService.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostInteractionService {

  private final CommunityPostRepository repository;
  private final UserRepository userRepository;
  private final CommunityPostLikeRepository likeRepo;
  private final CommunityPostBookmarkRepository bmRepo;
  private final CommunityReportRepository postReportRepository;


  //게시글 좋아요
  @Transactional
  public int toggleLike(Long postId){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    if (likeRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
      // 이미 좋아요 → 삭제
      likeRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
      post.setLikeCount(post.getLikeCount() - 1);
    } else {
      // 없으니까 추가
      CommunityPostLike postLike = new CommunityPostLike();
      postLike.setUser(user);
      postLike.setPost(post);
      likeRepo.save(postLike);
      post.setLikeCount(post.getLikeCount() + 1);
    }
  return post.getLikeCount();
  }

  //게시글 북마크
  @Transactional
  public String toggleBookmark(Long postId){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    if (bmRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
      // 이미 좋아요 → 삭제
      bmRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
      return "북마크 취소";
    } else {
      // 없으니까 추가
      CommunityPostBookmark postBm = new CommunityPostBookmark();
      postBm.setUser(user);
      postBm.setPost(post);
      bmRepo.save(postBm);
      return "북마크";
    }
  }

  // 좋아요한 게시글
  @Transactional(readOnly = true)
  public List<CommunityPostReadResponseDto> getLikedPosts() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));



    return likeRepo.findAllByUser(user).stream()
      .map(like -> {
        CommunityPost post = like.getPost();
        List<CommentResponseDto> comments = post.getComments().stream().map(comment -> new CommentResponseDto(
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
            comments
        );
      }).toList();
  }
  //북마크한 게시글
  @Transactional
  public List<CommunityPostReadResponseDto> getBmPosts(){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(username)
      .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    return bmRepo.findAllByUser(user).stream()
      .map(bm -> {
        CommunityPost post = bm.getPost();
        List<CommentResponseDto> comments = post.getComments().stream().map(comment -> new CommentResponseDto(
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
          comments
        );
      }).toList();

  }
  //커뮤니티 게시글 신고
  @Transactional
  public void reportPost(Long postId, CommunityPostReportRequestDto dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User reporter = userRepository.findByEmail(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

    CommunityReport report = CommunityReport.builder()
        .post(post)
        .reporter(reporter)
        .reason(dto.getReason())
        .description(dto.getDescription())
        .build();

    postReportRepository.save(report);

  }






}
