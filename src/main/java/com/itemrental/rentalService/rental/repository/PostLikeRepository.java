package com.itemrental.rentalService.rental.repository;

import com.itemrental.rentalService.rental.entity.RentalPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<RentalPostLike, Long> {
}
