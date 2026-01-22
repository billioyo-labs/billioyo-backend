package com.itemrental.rentalService.global.config;

import com.itemrental.rentalService.domain.auth.repository.RefreshTokenRepository;
import com.itemrental.rentalService.domain.user.repository.UserRepository;
import com.itemrental.rentalService.global.utils.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final UserRepository userRepository;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, AuthenticationConfiguration authenticationConfiguration, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ===== 기본 보안 설정 =====
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ===== 필터 =====
            .addFilterBefore(
                new CustomLogoutFilter(jwtTokenProvider, refreshTokenRepository),
                LogoutFilter.class
            )
            .addFilterAt(
                new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                LoginFilter.class
            )
            .addFilterAt(
                new LoginFilter(
                    authenticationManager(authenticationConfiguration),
                    jwtTokenProvider,
                    refreshTokenRepository,
                    userRepository
                ),
                UsernamePasswordAuthenticationFilter.class
            )

            // ===== 인가 설정 =====
            .authorizeHttpRequests(auth -> auth

                // --- 공개 API ---
                .requestMatchers(
                    "/",
                    "/login",
                    "/error",
                    "/reissue",
                    "/auth/**",
                    "/mail/**",
                    "/actuator/**"
                ).permitAll()

                // --- 공개 조회 API ---
                .requestMatchers(HttpMethod.GET,
                    "/api/community-posts/**",
                    "/api/products/**"
                ).permitAll()

                // --- 인증 없이 허용되는 API ---
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/api/ws-stomp/**").permitAll()

                // --- 관리자 전용 ---
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // --- 나머지는 인증 필요 ---
                .anyRequest().authenticated()
            );

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(BCryptPasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);

        return provider;
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

        // 4. 자격 증명(인증 정보: 쿠키, Authorization 헤더 등) 허용gd 여부
        // true로 설정하면 setAllowedOrigins에 * (와일드카드)를 사용할 수 없습니다.
        configuration.setAllowCredentials(true);

        // 5. 클라이언트에 노출할 헤더 설정 (생략 가능)
        // configuration.setExposedHeaders(List.of("X-Auth-Token"));
        configuration.setExposedHeaders(List.of("access", "Authorization", "Set-Cookie"));

        // 6. Preflight 요청의 캐시 시간 (초 단위)
        configuration.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로(/**)에 대해 위에서 설정한 CORS 구성을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}