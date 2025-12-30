package com.itemrental.rentalService.domain.rental.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalPostSearchRequestDto {
    private Double lat;
    private Double lng;
    private Double distance;
}