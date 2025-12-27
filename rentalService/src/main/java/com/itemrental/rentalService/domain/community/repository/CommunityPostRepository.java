
package com.itemrental.rentalService.domain.community.repository;

import com.itemrental.rentalService.domain.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  List<CommunityPost> findByTitleContainingOrContentContaining(String title, String content);
}
