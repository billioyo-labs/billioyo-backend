package com.itemrental.rentalService.rental.repository;

import com.itemrental.rentalService.rental.entity.RentalPostBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkRepository extends JpaRepository<RentalPostBookmark, Long> {
}
