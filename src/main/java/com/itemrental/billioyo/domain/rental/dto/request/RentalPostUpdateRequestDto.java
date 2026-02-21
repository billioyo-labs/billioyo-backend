package com.itemrental.billioyo.domain.rental.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RentalPostUpdateRequestDto {
    private String title;
    private String description;
    private Long price;
    private String location;
    private String category;
}
