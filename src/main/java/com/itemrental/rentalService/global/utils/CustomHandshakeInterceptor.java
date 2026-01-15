package com.itemrental.rentalService.global.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String query = request.getURI().getQuery();
        String accessToken = null;

        if (query != null && query.contains("token=")) {
            accessToken = query.split("token=")[1].split("&")[0];
        }

        if (accessToken == null) {
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String header = authHeaders.get(0);
                if (header.startsWith("Bearer ")) {
                    accessToken = header.substring(7);
                }
            }
        }

        if (accessToken != null) {
            try {
                jwtTokenProvider.validateToken(accessToken);
                String username = jwtTokenProvider.getUserName(accessToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                attributes.put("principal", auth);
                System.out.println("🔌 신규 연결 유저 Principal Name: " + (auth != null ? auth.getName() : "null"));
                return true;
            } catch (Exception e) {
                log.warn("WebSocket Handshake JWT failed: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String extractTokenFromHeader(ServerHttpRequest request) {
        List<String> authorizationHeaders = request.getHeaders().get("Authorization");
        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            return null;
        }

        String header = authorizationHeaders.getFirst();

        if (header != null && header.startsWith("Bearer")) {
            return header.substring(7);
        }
        return null;
    }
}
