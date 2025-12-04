//package com.squadprisma.notesoccer.orchestration_service.config;
//
//import com.squadprisma.notesoccer.orchestration_service.domain.service.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    public JwtAuthenticationFilter(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//
//            if (jwtService.isValid(token)) {
//                String userId = jwtService.getUserIdFromToken(token);
//                String role = jwtService.getRoleFromToken(token);
//
//                var auth = new UsernamePasswordAuthenticationToken(
//                        userId,
//                        null,
//                        List.of(new SimpleGrantedAuthority(role))
//                );
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
