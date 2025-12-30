package com.itemrental.rentalService.domain.rental.repository;

import com.itemrental.rentalService.domain.rental.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByUserId(Long userId, Pageable pageable);
  Page<Post> findTop5ByStatusTrueOrderByLikeCountDescViewCountDescCreatedAtDesc(Pageable pageable);

  @Query(value = "SELECT * FROM post p " +
          "WHERE ST_Distance_Sphere(POINT(:currentLng, :currentLat), POINT(p.longitude, p.latitude)) <= :distance " +
          "ORDER BY ST_Distance_Sphere(POINT(:currentLng, :currentLat), POINT(p.longitude, p.latitude)) ASC",
          countQuery = "SELECT count(*) FROM post p WHERE ST_Distance_Sphere(POINT(:currentLng, :currentLat), POINT(p.longitude, p.latitude)) <= :distance",
          nativeQuery = true)
  Page<Post> findWithinDistance(@Param("currentLat") Double lat,
                                @Param("currentLng") Double lng,
                                @Param("distance") Double distance,
                                Pageable pageable);
}
