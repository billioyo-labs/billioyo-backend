package com.itemrental.rentalService.domain.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityPostSearchRequestDto {
    private Double lat;
    private Double lng;
    private Double distance;
}