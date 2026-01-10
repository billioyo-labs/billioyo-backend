package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.Post;
import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import com.itemrental.rentalService.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<RentalPostLike, Long> {
  boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
  boolean existsByUserAndPost(User user, Post post);
  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
