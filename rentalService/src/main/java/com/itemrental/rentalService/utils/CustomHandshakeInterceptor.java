package com.itemrental.rentalService.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception{
        String accessToken = extractTokenFromHeader(request);
        if(accessToken != null){
            try{
                jwtTokenProvider.validateToken(accessToken);
            }catch (ExpiredJwtException e) {
                log.info("ACCESS TOKEN EXPIRED");
                return false;
            } catch (UnsupportedJwtException e) {
                log.info("UNSUPPORTED JWT TOKEN");
                return false;
            } catch (MalformedJwtException | SecurityException | DecodingException e) {
                log.info("INVALID JWT TOKEN");
                return false;
            } catch (IllegalArgumentException e) {
                log.info("JWT CLAIMS STRING IS EMPTY");
                return false;
            } catch (JwtException e) {
                log.info("INVALID ACCESS TOKEN");
                return false;
            }
            if (!"access".equals(jwtTokenProvider.getCategory(accessToken))) {
                log.info("INVALID ACCESS TOKEN");
                return false;
            }
            String username = jwtTokenProvider.getUserName(accessToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            attributes.put("principal", auth);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){

    }

    private String extractTokenFromHeader(ServerHttpRequest request){
        List<String> authorizationHeaders = request.getHeaders().get("Authorization");
        if(authorizationHeaders == null || authorizationHeaders.isEmpty()){
            return null;
        }

        String header = authorizationHeaders.getFirst();

        if(header != null && header.startsWith("Bearer")){
            return header.substring(7);
        }
        return null;
    }
}
