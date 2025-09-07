package com.itemrental.rentalService.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

@RedisHash(value = "refreshToken", timeToLive = 86400)  // 24시간(초 단위)
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String token;    // Redis key

    private Long memberId;   // FK 역할

    public RefreshToken(String token, Long memberId) {
        this.token    = token;
        this.memberId = memberId;
    }
}