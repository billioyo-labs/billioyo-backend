
package com.itemrental.rentalService.domain.community.repository;

import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  List<CommunityPost> findByTitleContainingOrContentContaining(String title, String content);
  @Query(value = "SELECT * FROM community_post p " +
          "WHERE (ST_Distance_Sphere(POINT(p.longitude, p.latitude), POINT(:lng, :lat)) <= :distance * 1000)",
          countQuery = "SELECT count(*) FROM community_post p " +
                  "WHERE (:lat IS NULL OR :lng IS NULL OR " +
                  "ST_Distance_Sphere(POINT(p.longitude, p.latitude), POINT(:lng, :lat)) <= :distance * 1000)",
          nativeQuery = true)
  Page<CommunityPost> findWithinDistance(@Param("lat") Double lat,
                                         @Param("lng") Double lng,
                                         @Param("distance") Double distance,
                                         Pageable pageable);
}
