package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<RentalPostLike, Long> {
  boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
