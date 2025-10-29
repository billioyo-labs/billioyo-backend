package com.itemrental.rentalService.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationUtil jwtAuthenticationUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String token = accessor.getFirstNativeHeader("Authorization");

            if(token != null && token.startsWith("Bearer ")){
                String jwt = token.substring(7);
                if(jwtTokenProvider.validateToken(token)){
                    String username = jwtTokenProvider.getUserName(token);
                    if(username != null){
                        Authentication authentication = jwtAuthenticationUtil.createAuthentication(username);

                        accessor.setUser(authentication);
                    }
                }

            }
        }
        return message;
    }
}
