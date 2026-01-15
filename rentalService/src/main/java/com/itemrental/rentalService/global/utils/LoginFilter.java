package com.itemrental.rentalService.global.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itemrental.rentalService.domain.auth.entity.RefreshToken;
import com.itemrental.rentalService.domain.auth.repository.RefreshTokenRepository;
import com.itemrental.rentalService.domain.user.dto.LoginSuccessDto;
import com.itemrental.rentalService.domain.user.entity.User;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String userEmail = null;
        String password = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(request.getInputStream());

            userEmail = jsonNode.get("userId").asText();
            password = jsonNode.get("password").asText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, password, null);

        return authenticationManager.authenticate(authToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        String userEmail = user.getEmail();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        LoginSuccessDto loginSuccessDto = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("이메일에 해당하는 사용자가 없음")).toLoginSuccessDto();
        log.info(userEmail);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(loginSuccessDto);

        String role = auth.getAuthority();
        //10분
        String accessToken = jwtTokenProvider.createJwt("access", userEmail, 600000L);
        //24시간
        String refreshToken = jwtTokenProvider.createJwt("refresh", userEmail, 86400000L);

        addRefreshEntity(user.getId(), refreshToken);

        //띄어쓰기 필수

        //Authorization: Bearer 인증토큰string
        //response.addHeader("Authorization", "Bearer " + token);
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonResponse);

    }

    private void addRefreshEntity(Long userId, String refreshToken) {

        RefreshToken refreshEntity = new RefreshToken(refreshToken, userId);

        refreshTokenRepository.save(refreshEntity);
    }

    //로그인 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        //쿠키의 생명주기
        cookie.setMaxAge(24 * 60 * 60);
        //쿠키 적용될 범위
        //cookie.setPath("/");
        //https통신을 위해
        //cookie.setSecure(true);
        //자바스크립트로 쿠키에 접근하지 못하도록 함으로써, xss 공격 방지
        cookie.setHttpOnly(true);

        return cookie;

    }
}