package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IamPortValidator implements PaymentValidator{
    private final IamportClient iamportClient;

    @Override
    public PaymentInfo validate(String impUid) {
        try {
            Payment payment = iamportClient.paymentByImpUid(impUid).getResponse();
            if (payment == null) throw new IllegalStateException("결제 내역이 없습니다.");

            return PaymentInfo.builder()
                    .impUid(payment.getImpUid())
                    .merchantUid(payment.getMerchantUid())
                    .amount(payment.getAmount().longValue())
                    .status(payment.getStatus())
                    .payMethod(payment.getPayMethod())
                    .pgProvider(payment.getPgProvider())
                    .name(payment.getName())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("결제 검증 중 오류 발생", e);
        }
    }
}
