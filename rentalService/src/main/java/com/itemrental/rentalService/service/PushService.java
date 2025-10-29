package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.ChatMessage;
import com.itemrental.rentalService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PushService {
    private final NotificationRepository notificationRepository;

    public void sendPushNotification(ChatMessage chatMessage){

    }
}
