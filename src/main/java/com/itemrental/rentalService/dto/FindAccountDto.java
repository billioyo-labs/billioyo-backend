package com.itemrental.rentalService.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FindAccountDto {
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
            message = "010-0000-0000 형식으로 입력해주세요.")
    private String phoneNumber;
}
