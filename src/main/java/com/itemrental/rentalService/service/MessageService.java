package com.itemrental.rentalService.service;

import com.itemrental.rentalService.dto.ChatMessage;
import com.itemrental.rentalService.entity.ChattingParticipant;
import com.itemrental.rentalService.entity.ChattingRoom;
import com.itemrental.rentalService.entity.Message;
import com.itemrental.rentalService.entity.User;
import com.itemrental.rentalService.repository.ChattingParticipantRepository;
import com.itemrental.rentalService.repository.ChattingRoomRepository;
import com.itemrental.rentalService.repository.MessageRepository;
import com.itemrental.rentalService.repository.UserRepository;
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
