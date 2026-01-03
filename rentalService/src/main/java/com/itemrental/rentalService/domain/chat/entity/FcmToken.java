package com.itemrental.rentalService.domain.chat.entity;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
