package com.itemrental.billioyo.domain.rental.repository;

import com.itemrental.billioyo.domain.rental.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<Image, Long> {
}
