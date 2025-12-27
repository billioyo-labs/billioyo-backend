package com.itemrental.rentalService.domain.rental.repository;


import com.itemrental.rentalService.domain.rental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReviewRepository extends JpaRepository<Review, Long> {
}
