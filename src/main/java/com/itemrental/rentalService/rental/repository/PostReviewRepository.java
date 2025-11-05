package com.itemrental.rentalService.rental.repository;


import com.itemrental.rentalService.rental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReviewRepository extends JpaRepository<Review, Long> {
}
