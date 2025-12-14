package ecommerce.shoestore.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ❌ Tắt CSRF cho dễ làm đồ án
            .csrf(csrf -> csrf.disable())

            // ===== PHÂN QUYỀN =====
            .authorizeHttpRequests(auth -> auth

                // ===== PUBLIC =====
                .requestMatchers(
                    "/", "/index",
                    "/auth/**",
                    "/css/**", "/js/**", "/images/**",
                    "/error",
                    "/product/**",
                    "/user/**"
                ).permitAll()

                // ===== ADMIN ONLY (🔥 DÒNG QUAN TRỌNG) =====
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ===== CÒN LẠI: CHỈ CẦN LOGIN =====
                .anyRequest().authenticated()
            )

            // ===== FORM LOGIN (GIỮ NGUYÊN CODE BẠN BẠN) =====
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/j_spring_security_check")
                .defaultSuccessUrl("/")
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )

            // ===== LOGOUT =====
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
