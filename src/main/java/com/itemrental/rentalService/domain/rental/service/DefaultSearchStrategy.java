package com.itemrental.rentalService.domain.rental.service;

import com.itemrental.rentalService.domain.rental.dto.request.RentalPostSearchRequestDto;
import com.itemrental.rentalService.domain.rental.entity.RentalPost;
import com.itemrental.rentalService.domain.rental.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultSearchStrategy implements PostSearchStrategy {
    private PostRepository postRepository;
    @Override
    public Page<RentalPost> search(RentalPostSearchRequestDto cond, Pageable pageable){
        return postRepository.findAll((pageable));
    }
}
