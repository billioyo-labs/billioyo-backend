package com.itemrental.billioyo.domain.rental.repository;

import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<RentalPost, Long> {
    Page<RentalPost> findByUserId(Long userId, Pageable pageable);

    Page<RentalPost> findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT * FROM rental_post p " +
        "WHERE ST_Distance_Sphere(POINT(:currentLng, :currentLat), POINT(p.longitude, p.latitude)) <= :distance",
        countQuery = "SELECT count(*) FROM rental_post p WHERE ST_Distance_Sphere(POINT(:currentLng, :currentLat), POINT(p.longitude, p.latitude)) <= :distance",
        nativeQuery = true)
    Page<RentalPost> findWithinDistance(@Param("currentLat") Double lat,
                                        @Param("currentLng") Double lng,
                                        @Param("distance") Double distance,
                                        Pageable pageable);

    @Query("SELECT l.rentalPost FROM RentalPostLike l WHERE l.user.id = :userId")
    Page<RentalPost> findByLikesUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM RentalPost p JOIN RentalPostBookmark b WHERE b.user.id = :userId")
    Page<RentalPost> findByBookmarksUserId(@Param("userId") Long userId, Pageable pageable);
}
