package com.itemrental.rentalService.community.repository;

import com.itemrental.rentalService.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository  extends JpaRepository<CommunityComment, Long> {
  // postId 기준으로 댓글 전체를 createdAt 내림차순 정렬해서 가져오기
  List<CommunityComment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
