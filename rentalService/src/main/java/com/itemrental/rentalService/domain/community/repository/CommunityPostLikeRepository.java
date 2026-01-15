package com.itemrental.rentalService.domain.community.repository;

import com.itemrental.rentalService.domain.community.entity.CommunityPostLike;
import com.itemrental.rentalService.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    void deleteByUser_IdAndPost_Id(Long userId, Long postId);

    List<CommunityPostLike> findAllByUser(User user);
}
