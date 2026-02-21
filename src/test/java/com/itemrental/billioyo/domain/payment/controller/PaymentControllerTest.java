package com.itemrental.billioyo.domain.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.billioyo.domain.payment.dto.PaymentInfo;
import com.itemrental.billioyo.domain.payment.dto.PortOneDto;
import com.itemrental.billioyo.domain.payment.service.PaymentService;
import com.itemrental.billioyo.domain.payment.service.PaymentValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentValidator paymentValidator;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @WithMockUser
    @DisplayName("결제 완료 요청 시 성공 응답을 반환한다")
    void validatePayment_Success() throws Exception {
        // given
        PortOneDto dto = new PortOneDto("imp_123", "merchant_456", 10000L, 1L);

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .impUid("imp_123")
                .merchantUid("merchant_456")
                .amount(10000L)
                .status("paid")
                .build();

        given(paymentValidator.validate(any())).willReturn(paymentInfo);

        // when & then
        mockMvc.perform(post("/api/payment/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("결제 완료"));
    }
}