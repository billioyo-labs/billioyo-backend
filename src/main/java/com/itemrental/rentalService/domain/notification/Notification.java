package com.itemrental.rentalService.domain.notification;

import com.itemrental.rentalService.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationId", nullable = false, unique = true, updatable = false)
    private Long id;
    @CreatedDate
    private LocalDateTime notificationTime;
    private String description;

    @OneToMany(mappedBy = "notification")
    private List<User> receivers = new ArrayList<>();

    public void addReceiver(User user){
        receivers.add(user);
        user.setNotification(this);
    }
}
