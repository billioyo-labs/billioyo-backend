
package com.itemrental.rentalService.community.repository;

import com.itemrental.rentalService.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  Page<CommunityPost> findByTitleContainingOrContentContaining(String title, String content);
}
