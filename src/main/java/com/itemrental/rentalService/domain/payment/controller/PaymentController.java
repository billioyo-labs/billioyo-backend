package com.itemrental.rentalService.domain.payment.controller;

import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;
import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.service.PaymentService;
import com.itemrental.rentalService.domain.payment.service.PaymentValidator;
import com.itemrental.rentalService.global.response.ApiResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentValidator paymentValidator;
    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<Void>> validatePayment(@RequestBody PortOneDto dto) throws IamportResponseException, IOException {
        PaymentInfo paymentInfo = paymentValidator.validate(dto.getImpUid());


        paymentService.processPaymentDone(paymentInfo, dto);

        return ResponseEntity.ok(ApiResponse.success("결제 완료"));
    }
}
