package com.itemrental.billioyo.domain.payment.controller;

import com.itemrental.billioyo.domain.payment.dto.PaymentInfo;
import com.itemrental.billioyo.domain.payment.dto.PortOneDto;
import com.itemrental.billioyo.domain.payment.service.PaymentService;
import com.itemrental.billioyo.domain.payment.service.PaymentValidator;
import com.itemrental.billioyo.global.common.ApiResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "결제 처리 API")
public class PaymentController {
    private final PaymentValidator paymentValidator;
    private final PaymentService paymentService;

    @Operation(
        summary = "결제 완료 처리",
        description =
            "사용자가 결제를 완료한 후 호출되는 API입니다. 포트원(PortOne) 결제 결과를 검증한 뒤, 결제 완료 처리를 진행합니다."
    )
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<Void>> validatePayment(@RequestBody PortOneDto dto, Principal principal) throws IamportResponseException, IOException {
        PaymentInfo paymentInfo = paymentValidator.validate(dto.getImpUid());


        paymentService.processPaymentDone(paymentInfo, dto, principal.getName());

        return ResponseEntity.ok(ApiResponse.success("결제 완료"));
    }
}
