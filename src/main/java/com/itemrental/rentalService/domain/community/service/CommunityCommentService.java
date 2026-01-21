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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
    private final UserRepository userRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityPostRepository postRepository;


    //커뮤니티 댓글 생성
    @Transactional
    public void createCommunityComment(CommentCreateRequestDto dto, Long postId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.increaseCommentCount();
        CommunityComment comment = new CommunityComment(user, post, dto.getComment());

        commentRepository.save(comment);
    }
}
