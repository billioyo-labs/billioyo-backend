package com.itemrental.billioyo.domain.rental.service;

import com.itemrental.billioyo.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.billioyo.domain.rental.entity.RentalPost;
import com.itemrental.billioyo.domain.rental.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistanceSearchStrategy implements PostSearchStrategy {
    private final PostRepository postRepository;

    @Override
    public Page<RentalPost> search(RentalPostSearchRequestDto cond, Pageable pageable){
        return postRepository.findWithinDistance(cond.getLat(), cond.getLng(), cond.getDistance(), pageable);
    }
}
