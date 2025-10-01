package com.itemrental.rentalService.community.service;


import com.itemrental.rentalService.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.community.dto.response.CommentResponseDto;
import com.itemrental.rentalService.community.entity.CommunityComment;
import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.repository.CommunityCommentRepository;
import com.itemrental.rentalService.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.entity.Post;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
  private final UserRepository userRepository;
  private final CommunityCommentRepository commentRepo;
  private final CommunityPostRepository postRepo;



  //커뮤니티 댓글 생성
  @Transactional
  public void createCommunityComment(CommentCreateRequestDto dto, Long postId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username).get();

    CommunityPost post = postRepo.findById(postId).get();
    post.setCommentCount(post.getCommentCount()+1);
    CommunityComment comment = new CommunityComment();
    comment.setUser(user);
    comment.setPost(postRepo.findById(postId).get());
    comment.setComment(dto.getComment());
    commentRepo.save(comment);
  }

//  커뮤니티 댓글 조회
//  @Transactional
//  public List<CommentResponseDto> getComments(Long postId) {
//    List<CommunityComment> comments = commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
//    return comments.stream().map(comment -> new CommentResponseDto(
//      comment.getId(),
//      comment.getUser().getUsername(),
//      comment.getComment(),
//      comment.getCreatedAt()
//    )).toList();
//  }



}
