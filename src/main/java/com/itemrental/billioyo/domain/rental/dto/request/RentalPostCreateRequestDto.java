package com.itemrental.billioyo.domain.rental.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RentalPostCreateRequestDto {
    private String title;
    private String description;
    private Long price;
    private String location;
    private Double latitude;
    private Double longitude;
    private String category;
    private List<String> imageUrls;
}
