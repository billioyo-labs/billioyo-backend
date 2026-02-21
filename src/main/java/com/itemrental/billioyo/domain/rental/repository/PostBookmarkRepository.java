package com.itemrental.billioyo.domain.rental.repository;

import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.entity.RentalPostBookmark;
import com.itemrental.billioyo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostBookmarkRepository extends JpaRepository<RentalPostBookmark, Long> {

    Optional<RentalPostBookmark> findByUserAndRentalPost(User user, RentalPost rentalPost);

    boolean existsByUserAndRentalPost(User user, RentalPost rentalPost);

    void deleteByUserAndRentalPost(User user, RentalPost rentalPost);

    void deleteByUserIdAndRentalPostId(Long userId, Long postId);
}
