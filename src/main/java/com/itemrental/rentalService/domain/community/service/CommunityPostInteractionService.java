package com.itemrental.rentalService.domain.community.service;


import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import com.itemrental.rentalService.domain.community.entity.CommunityPostBookmark;
import com.itemrental.rentalService.domain.community.entity.CommunityPostLike;
import com.itemrental.rentalService.domain.community.repository.CommunityPostBookmarkRepository;
import com.itemrental.rentalService.domain.community.repository.CommunityPostLikeRepository;
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
public class CommunityPostInteractionService {

    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityPostLikeRepository likeRepo;
    private final CommunityPostBookmarkRepository bmRepo;


    //게시글 좋아요
    @Transactional
    public int toggleLike(Long postId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (likeRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
            likeRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
            post.removeLike();
        } else {
            likeRepo.save(new CommunityPostLike(user, post));
            post.addLike();
        }
        return post.getLikeCount();
    }

    //게시글 북마크
    @Transactional
    public String toggleBookmark(Long postId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UserNotFoundException(principal.getName()));

        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));

        if (bmRepo.existsByUser_IdAndPost_Id(user.getId(), post.getId())) {
            bmRepo.deleteByUser_IdAndPost_Id(user.getId(), post.getId());
            return "북마크 취소";
        } else {
            bmRepo.save(new CommunityPostBookmark(user, post));
            return "북마크";
        }
    }

}
