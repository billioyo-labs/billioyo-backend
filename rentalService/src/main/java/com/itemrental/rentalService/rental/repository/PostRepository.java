package com.itemrental.rentalService.rental.repository;

import com.itemrental.rentalService.rental.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
