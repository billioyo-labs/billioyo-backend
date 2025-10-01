package com.itemrental.rentalService.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "resetToken", timeToLive = 86400)
@Getter
@Setter
@NoArgsConstructor
public class ResetToken {
    @Id
    private String token;

    @Indexed
    private String email;

    public ResetToken(String token, String email){
        this.email = email;
        this.token = token;
    }
}
