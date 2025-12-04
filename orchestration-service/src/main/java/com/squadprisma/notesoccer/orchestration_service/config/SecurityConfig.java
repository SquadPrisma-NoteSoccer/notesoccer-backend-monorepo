//package com.squadprisma.notesoccer.orchestration_service.config;
//
//import com.squadprisma.notesoccer.orchestration_service.domain.service.JwtService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//public class SecurityConfig {
//    private final JwtService jwtService;
//
//    public SecurityConfig(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService);
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/auth/login",
//                                "/actuator/**",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }
//}
