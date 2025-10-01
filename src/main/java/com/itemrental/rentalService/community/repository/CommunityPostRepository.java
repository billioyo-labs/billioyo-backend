
package com.itemrental.rentalService.community.repository;

import com.itemrental.rentalService.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  List<CommunityPost> findByTitleContainingOrContentContaining(String title, String content);
}
