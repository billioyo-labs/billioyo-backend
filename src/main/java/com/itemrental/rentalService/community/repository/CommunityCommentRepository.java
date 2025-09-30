package com.itemrental.rentalService.community.repository;

import com.itemrental.rentalService.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentRepository  extends JpaRepository<CommunityComment, Long> {
}
