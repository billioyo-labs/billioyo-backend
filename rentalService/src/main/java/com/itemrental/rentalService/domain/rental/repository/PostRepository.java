package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByUserId(Long userId, Pageable pageable);
  Page<Post> findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(Pageable pageable);
}
