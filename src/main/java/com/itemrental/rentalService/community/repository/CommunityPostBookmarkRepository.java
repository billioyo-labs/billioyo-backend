package com.itemrental.rentalService.community.repository;
import com.itemrental.rentalService.community.entity.CommunityPostBookmark;
import com.itemrental.rentalService.community.entity.CommunityPostLike;
import com.itemrental.rentalService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommunityPostBookmarkRepository extends JpaRepository<CommunityPostBookmark, Long> {
  boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
  List<CommunityPostBookmark> findAllByUser(User user);
}
