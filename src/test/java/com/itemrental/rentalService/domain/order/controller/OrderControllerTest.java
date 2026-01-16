package com.itemrental.rentalService.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.rentalService.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("주문 생성 요청 시 200 OK와 생성된 주문 정보를 반환한다")
    void createOrder_Success() throws Exception {
        // given
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(10L, 5000L);
        OrderCreateResponseDto responseDto = OrderCreateResponseDto.builder()
                .orderId(1L)
                .merchantUid("order_uuid123")
                .amount(5000L)
                .status(Order.OrderStatus.CREATED)
                .build();

        given(orderService.createOrder(any(OrderCreateRequestDto.class), anyString()))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/order")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문 생성 완료"))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.merchantUid").value("order_uuid123"));
    }
}
