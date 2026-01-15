package com.itemrental.rentalService.domain.payment.controller;

import com.itemrental.rentalService.domain.payment.dto.PortOneDto;
import com.itemrental.rentalService.domain.payment.service.PaymentService;
import com.itemrental.rentalService.global.response.ApiResponse;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private IamportClient iamportClient;
    private final PaymentService paymentService;


    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<Void>> validatePayment(@RequestBody PortOneDto dto) throws IamportResponseException, IOException {
        IamportResponse<Payment> iamportRes = iamportClient.paymentByImpUid(dto.getImpUid());


        paymentService.processPaymentDone(iamportRes.getResponse(), dto);

        return ResponseEntity.ok(ApiResponse.success("결제 완료"));
    }
}
