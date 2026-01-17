package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.entity.RentalPostLike;
import com.itemrental.rentalService.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<RentalPostLike, Long> {

    Optional<RentalPostLike> findByUserAndRentalPost(User user, RentalPost rentalPost);

    boolean existsByUserAndRentalPost(User user, RentalPost rentalPost);

    void deleteByUserAndRentalPost(User user, RentalPost rentalPost);

    void deleteByUserIdAndRentalPostId(Long userId, Long postId);
}
