package com.itemrental.rentalService.domain.rental.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalPostSearchRequestDto {
    private Double lat;
    private Double lng;
    private Double distance;
}