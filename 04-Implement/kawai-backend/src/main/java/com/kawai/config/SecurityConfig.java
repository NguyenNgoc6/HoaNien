package com.kawai.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- CẤU HÌNH BẢO MẬT: CHẶN ĐĂNG NHẬP STAFF TỪ IP LẠ ---
                        .requestMatchers("/ops-login")
                        .access(new org.springframework.security.web.access.expression.WebExpressionAuthorizationManager(
                                "hasIpAddress('192.168.1.0/24')"))

                        // Các URL public của hệ thống
                        .requestMatchers("/", "/booking", "/auth/register", "/auth/login",
                                "/h2-console/**", "/css/**", "/js/**", "/guest/**", "/living", "/wellbeing", "/dining",
                                "/experiences", "/tours", "/tours/**", "/profile", "/AnhTour/**",
                                "/api/menu-items/**", "/api/rooms/**",
                                "/api/tour-bookings", "/api/tour-bookings/**", "/api/faceid/**", "/error")
                        .permitAll()

                        // API đặt món (cả khách và staff đều gọi chung API này nên chỉ cần đăng nhập)
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/pos/orders").authenticated()

                        // F&B Staff phải đăng nhập với role FB_STAFF (hoặc ADMIN/MANAGER)
                        .requestMatchers("/fbStaff/**", "/f&bStaff/**", "/api/pos/**")
                        .hasAnyRole("FB_STAFF", "ADMIN", "MANAGER")

                        // Phân quyền các Role hệ thống
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/staff/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/tourguide/**").hasRole("TOURGUIDE")
                        .requestMatchers("/profile/**", "/order-food", "/order-success").authenticated()
                        .anyRequest().authenticated())

                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                // Trả 401 JSON cho API thay vì redirect HTML
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getRequestURI();
                            if (path.startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Vui long dang nhap de tiep tuc\"}");
                            } else {
                                response.sendRedirect("/ops-login");
                            }
                        }))
                .formLogin(form -> form
                        .loginPage("/ops-login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(roleBasedSuccessHandler())
                        .failureUrl("/ops-login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/booking")
                        .defaultSuccessUrl("/", true))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                String redirect = "/";
                for (var authz : authentication.getAuthorities()) {
                    String role = authz.getAuthority();
                    if (role.equals("ROLE_ADMIN")) {
                        redirect = "/admin/dashboard";
                        break;
                    } else if (role.equals("ROLE_MANAGER")) {
                        redirect = "/manager/dashboard";
                        break;
                    } else if (role.equals("ROLE_RECEPTIONIST") || role.equals("ROLE_STAFF")) {
                        redirect = "/receptionist/dashboard";
                        break;
                    } else if (role.equals("ROLE_FB_STAFF")) {
                        redirect = "/fbStaff/dashboard";
                        break;
                    } else if (role.equals("ROLE_TOURGUIDE")) {
                        redirect = "/tourguide/dashboard";
                        break;
                    } else if (role.equals("ROLE_GUEST") || role.equals("ROLE_CUSTOMER")) {
                        redirect = "/";
                        break;
                    }
                }
                response.sendRedirect(redirect);
            }
        };
    }
}
