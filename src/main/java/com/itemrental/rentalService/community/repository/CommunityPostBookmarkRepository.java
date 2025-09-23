package com.itemrental.rentalService.community.repository;
import com.itemrental.rentalService.community.entity.CommunityPostBookmark;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityPostBookmarkRepository extends JpaRepository<CommunityPostBookmark, Long> {
  boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
