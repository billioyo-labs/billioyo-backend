package com.itemrental.rentalService.config;

import com.itemrental.rentalService.repository.RefreshTokenRepository;
import com.itemrental.rentalService.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, AuthenticationConfiguration authenticationConfiguration, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws  Exception{
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        //csrf disable
        http.csrf((auth) -> auth.disable());
        //form 로그인 비활성화 - 폼 기반 로그인 기능 사용하지 않겠다
        http.formLogin((auth) -> auth.disable());
        // http basic 인증 비활성화- header에 사용자 이름과 비밀번호를 서버로 보내는 방식 비활성화
        http.httpBasic((auth) -> auth.disable());
        http.addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, refreshTokenRepository), LogoutFilter.class);
        http.addFilterAt(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService), LoginFilter.class);
        // 경로별 인가 작업 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/post/list", "/post/search", "/user/**", "/auth/**", "/mail/**", "/error").permitAll() // 누구나 접근가능
                .requestMatchers(HttpMethod.GET, "/community/**").permitAll()
                .requestMatchers("/reissue").permitAll()
                .anyRequest().authenticated());  //나머지 경로들은 다 인증된 사용자만

        //addat은 원하는 자리에 -> usernamepasswordauthenticationfilter 자리에 놓겠다.
        //LoginFilter에게 authentication manager 인스턴스를 주입해줘야 함 -> bean으로 authenticationmanager을 객체를 반환하는 메소드
        //authenticationManager함수 또한 authenticationConfiguration을 인자로 받아야함. -> 생성자 방식으로 주입받아서 전달
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtTokenProvider, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);


        //세션 셜정 - 세션사용하지 않겠다.
        http.sessionManagement((session) -> session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 허용할 출처(Origin) 설정
        // 실제 운영 환경에서는 * 대신 정확한 도메인을 명시해야 합니다.
        configuration.setAllowedOrigins(List.of("https://rental-project-billioyo.vercel.app", "http://localhost:3000"));

        // 2. 허용할 HTTP Method 설정
        // 모든 메서드 허용: List.of("*") 또는 Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. 허용할 헤더 설정
        // 모든 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));

        // 4. 자격 증명(인증 정보: 쿠키, Authorization 헤더 등) 허용 여부
        // true로 설정하면 setAllowedOrigins에 * (와일드카드)를 사용할 수 없습니다.
        configuration.setAllowCredentials(true);

        // 5. 클라이언트에 노출할 헤더 설정 (생략 가능)
        // configuration.setExposedHeaders(List.of("X-Auth-Token"));

        // 6. Preflight 요청의 캐시 시간 (초 단위)
        configuration.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로(/**)에 대해 위에서 설정한 CORS 구성을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
