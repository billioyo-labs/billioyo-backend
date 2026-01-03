package com.itemrental.rentalService.domain.chat.service;

import com.google.firebase.messaging.*;
import com.itemrental.rentalService.domain.chat.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {
    private final FcmTokenRepository fcmTokenRepository;

    public void sendNotification(String targetToken, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setSound("default").build())
                        .build())
                .build();
        try{
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        }catch(FirebaseMessagingException e){
            if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
                fcmTokenRepository.deleteByFcmToken(targetToken);
                System.out.println("Invalid FCM Token deleted: "+ targetToken);
            }
            throw e;
        }


    }
}
