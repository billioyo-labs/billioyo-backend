package com.itemrental.rentalService.domain.user.entity;

import com.itemrental.rentalService.domain.notification.Notification;
import com.itemrental.rentalService.domain.user.dto.response.LoginSuccessResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private String username = "guest";

    @Column(nullable = false)
    @Builder.Default
    private String nickName = "guest";

    @Column(nullable = false)
    @Builder.Default
    private String password = "00000000";

    @Column(nullable = false)
    @Builder.Default
    private String birthDate = "000000";

    @Column(nullable = false)
    @Builder.Default
    private String phoneNumber = "00";

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserState userState = UserState.UNVERIFIED;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notificationId")
    private Notification notification;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { return userState != UserState.BANNED; }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public enum UserState {
        UNVERIFIED,
        PENDING_PROFILE_SETUP,
        ACTIVE,
        BANNED
    }

    public LoginSuccessResponseDto toLoginSuccessDto() {
        LoginSuccessResponseDto loginSuccessResponseDto = new LoginSuccessResponseDto();
        loginSuccessResponseDto.setId(this.getId());
        loginSuccessResponseDto.setEmail(this.getEmail());
        loginSuccessResponseDto.setUsername(this.getUsername());
        loginSuccessResponseDto.setBirthDate(this.getBirthDate());
        loginSuccessResponseDto.setPhoneNumber(this.getPhoneNumber());
        loginSuccessResponseDto.setNickName(this.getNickName());
        return loginSuccessResponseDto;
    }

    public void updateProfile(String name, String nickName, String phoneNumber, String birthDate) {
        this.username = name;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public void completeRegistration(String encodedPassword, String username, String nickName, String phoneNumber, String birthDate, List<String> roles) {
        this.password = encodedPassword;
        updateProfile(username, nickName, phoneNumber, birthDate);
        this.roles = new ArrayList<>(roles);
        this.userState = UserState.ACTIVE;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeState(UserState newState) {
        this.userState = newState;
    }

}


