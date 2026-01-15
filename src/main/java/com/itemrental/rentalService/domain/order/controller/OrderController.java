package com.itemrental.rentalService.domain.order.controller;


import com.itemrental.rentalService.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.rentalService.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.rentalService.domain.order.service.OrderService;
import com.itemrental.rentalService.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(
        @RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        OrderCreateResponseDto dto = orderService.createOrder(orderCreateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("주문 생성 완료", dto));
    }
}
