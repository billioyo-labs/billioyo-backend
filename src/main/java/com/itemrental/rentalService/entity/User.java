package com.itemrental.rentalService.entity;

import com.itemrental.rentalService.community.entity.CommunityPost;
import com.itemrental.rentalService.community.entity.CommunityPostBookmark;
import com.itemrental.rentalService.community.entity.CommunityPostLike;
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
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id")
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
    private UserState userState = UserState.UNVERIFIED;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notificationId")
    private Notification notification;



    //외래키 관리
    @OneToMany(mappedBy = "user")
    private List<ChattingParticipant> participants;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<InterestPost> interestPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommunityPost> communityPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommunityPostLike> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<CommunityPostBookmark> bookmarks;



    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum UserState{
        UNVERIFIED,
        PENDING_PROFILE_SETUP,
        ACTIVE
    }
}


