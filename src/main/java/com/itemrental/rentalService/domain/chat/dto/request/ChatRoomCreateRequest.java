package com.itemrental.rentalService.domain.chat.dto.request;

import com.itemrental.rentalService.domain.chat.entity.ChattingRoom;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequest {
    @Size(max = 20, message = "채팅방 이름은 20자 이내여야 합니다.")
    private String title;

    @NotEmpty(message = "채팅 참여자는 최소 1명 이상이어야 합니다.")
    private List<Long> userIds;

    @Builder
    private ChatRoomCreateRequest(String title, List<Long> userIds) {
        this.title = title;
        this.userIds = userIds;
    }

    public ChattingRoom toEntity() {
        return ChattingRoom.create(this.title);
    }
}
