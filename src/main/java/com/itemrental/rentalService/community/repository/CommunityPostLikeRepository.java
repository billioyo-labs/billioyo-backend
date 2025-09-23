package com.itemrental.rentalService.community.repository;
import com.itemrental.rentalService.community.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {
  boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
