package com.itemrental.rentalService.domain.community.service;


import com.itemrental.rentalService.domain.community.dto.request.CommentCreateRequestDto;
import com.itemrental.rentalService.domain.community.entity.CommunityComment;
import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.repository.CommunityCommentRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostRepository;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
    private final UserRepository userRepository;
    private final CommunityCommentRepository commentRepo;
    private final CommunityPostRepository postRepo;
    private final SecurityUtil securityUtil;


    //커뮤니티 댓글 생성
    @Transactional
    public void createCommunityComment(CommentCreateRequestDto dto, Long postId) {
        String username = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(username).get();

        CommunityPost post = postRepo.findById(postId).get();
        post.setCommentCount(post.getCommentCount() + 1);
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
