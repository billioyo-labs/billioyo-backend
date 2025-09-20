package com.itemrental.rentalService.controller;

import com.itemrental.rentalService.dto.ApiResponse;
import com.itemrental.rentalService.dto.SignUpDto;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        String message = userService.signUp(signUpDto);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity<?> duplicateCheck(@RequestParam("username") String username){
        userService.duplicateCheck(username);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 아이디입니다."));
    }
}