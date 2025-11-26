package com.itemrental.rentalService.rental.repository;

import com.itemrental.rentalService.rental.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByUserId(Long userId, Pageable pageable);
  Page<Post> findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc();
}
