package com.itemrental.billioyo.domain.rental.service;

import com.itemrental.billioyo.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchStrategy {
    Page<RentalPost> search(RentalPostSearchRequestDto condition, Pageable pageable);
}
