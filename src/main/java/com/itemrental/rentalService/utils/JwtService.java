package com.itemrental.rentalService.utils;


import com.itemrental.rentalService.entity.RefreshToken;
import com.itemrental.rentalService.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    public JwtService(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }
    public ResponseEntity<?> recreateJwt(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken==null){
            return new ResponseEntity("REFRESH TOKEN IS NULL", HttpStatus.BAD_REQUEST);
        }

        try{
            jwtTokenProvider.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("REFRESH TOKEN EXPIRED", HttpStatus.UNAUTHORIZED);
        } catch (SecurityException | MalformedJwtException | DecodingException e) {
            return new ResponseEntity<>("INVALID JWT TOKEN", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            return new ResponseEntity<>("UNSUPPORTED JWT TOKEN", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("JWT CLAIMS STRING IS EMPTY", HttpStatus.BAD_REQUEST);
        }

        String category = jwtTokenProvider.getCategory(refreshToken);
        if(!category.equals("refresh")){
            return new ResponseEntity("INVALID REFRESH TOKEN",HttpStatus.BAD_REQUEST);
        }
        RefreshToken refreshToken1 = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        String userName = jwtTokenProvider.getUserName(refreshToken);


        String newAccessToken = jwtTokenProvider.createJwt("access",userName,600000L);
        String newRefresh = jwtTokenProvider.createJwt("refresh", userName, 86400000L);

        refreshTokenRepository.deleteByRefresh(refreshToken);

        addRefreshEntity(refreshToken1.getMemberId(), newRefresh);

        response.setHeader("Authorization", newAccessToken);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key,value);
        //쿠키의 생명주기
        cookie.setMaxAge(24*60*60);
        //쿠키 적용될 범위
        //cookie.setPath("/");
        //https통신을 위해
        //cookie.setSecure(true);
        //자바스크립트로 쿠키에 접근하지 못하도록 함으로써, xss 공격 방지
        cookie.setHttpOnly(true);

        return cookie;

    }

    private void addRefreshEntity(Long userId, String refresh) {
        RefreshToken refreshEntity = new RefreshToken(refresh,userId);
        refreshTokenRepository.save(refreshEntity);
    }
}