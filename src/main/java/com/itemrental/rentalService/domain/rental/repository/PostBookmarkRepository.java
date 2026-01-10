package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.RentalPostBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkRepository extends JpaRepository<RentalPostBookmark, Long> {
  boolean existsByUser_IdAndRentalPost_Id(Long userId, Long postId);
  void deleteByUser_IdAndRentalPost_Id(Long userId, Long postId);
}
