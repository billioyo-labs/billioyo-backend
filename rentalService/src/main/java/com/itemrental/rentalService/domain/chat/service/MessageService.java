package com.itemrental.rentalService.domain.chat.service;

import com.itemrental.rentalService.domain.chat.dto.ChatMessage;
import com.itemrental.rentalService.domain.chat.entity.ChattingParticipant;
import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import com.itemrental.rentalService.domain.chat.entity.Message;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final UserRepository userRepository;

    @Transactional
    public void saveOfflineMessage(ChatMessage chatMessage){
        String receiver = chatMessage.getReceiver();
        String sender = chatMessage.getSender();
        String title = chatMessage.getRoomTitle();
        String content = chatMessage.getContent();
        Optional<User> opReceiver = userRepository.findByUsername(receiver);
        Optional<User> opSender = userRepository.findByUsername(sender);
        List<ChattingParticipant> senderParticipantsList = opSender.orElseThrow(() -> new RuntimeException("")).getParticipants();
        ChattingRoom chattingRoom = null;
        List<Message> messages = new ArrayList<>();
        ChattingParticipant chattingParticipant = null;
        for(ChattingParticipant participant : senderParticipantsList){
            if(participant.getChattingRoom().getTitle().equals(chatMessage.getRoomTitle())){
                chattingRoom = participant.getChattingRoom();
                break;
            }
        }
        if(chattingRoom != null){
            messages = chattingRoom.getMessages();
        }else{
            chattingRoom = ChattingRoom.builder()
                    .title(title)
                    .messages(messages)
                    .build();
            chattingParticipant = ChattingParticipant.builder()
                    .chattingRoom(chattingRoom)
                    .user(opSender.orElseThrow(() -> new RuntimeException("")))
                    .lastReadMessageId(null)
                    .unreadCount(0L)
                    .build();
            senderParticipantsList.add(chattingParticipant);
            opReceiver.orElseThrow(() -> new RuntimeException("")).getParticipants().add(chattingParticipant);
        }
        messages.add(Message.builder()
                .chattingRoom(chattingRoom)
                .user(opSender.orElseThrow(() -> new RuntimeException("")))
                .content(content)
                .build());
        userRepository.save(opSender.orElseThrow(() -> new RuntimeException("")));
        userRepository.save(opReceiver.orElseThrow(() -> new RuntimeException("")));
    }
}
