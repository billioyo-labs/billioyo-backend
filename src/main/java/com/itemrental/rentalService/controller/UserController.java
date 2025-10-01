package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.dto.ApiResponse;
import com.itemrental.rentalService.dto.FindAccountDto;
import com.itemrental.rentalService.dto.SignUpDto;
import com.itemrental.rentalService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        String message = userService.signUp(signUpDto);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity<ApiResponse<Void>> duplicateCheck(@RequestParam("nickName") String nickName){
        userService.duplicateCheck(nickName);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 아이디입니다."));
    }

    @PostMapping("/find-account")
    public ResponseEntity<ApiResponse<String>> findAccount(@Valid @RequestBody FindAccountDto findAccountDto){
        return ResponseEntity.ok(ApiResponse.success("사용자 아이디" ,userService.findAccount(findAccountDto.getPhoneNumber())));
    }
}