package com.itemrental.rentalService.community.service;


import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.entity.CommunityPostLike;
import com.itemrental.rentalService.community.repository.CommunityPostLikeRepository;
import com.itemrental.rentalService.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostLikeService {

  private final CommunityPostRepository repository;
  private final UserRepository userRepository;
  private final CommunityPostLikeRepository postLikeRepository;


  //게시글 좋아요
  @Transactional
  public void toggleLike(Long postId){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    CommunityPost post = repository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    ;
    if (postLikeRepository.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
      // 이미 좋아요 → 삭제
      postLikeRepository.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
    } else {
      // 없으니까 추가
      CommunityPostLike postLike = new CommunityPostLike();
      postLike.setUser(user);
      postLike.setPost(post);
      postLikeRepository.save(postLike);
    }
  }
}
