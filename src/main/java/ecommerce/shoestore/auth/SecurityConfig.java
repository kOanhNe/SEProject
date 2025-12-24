package ecommerce.shoestore.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // PUBLIC - thêm /order/mock/**
                .requestMatchers(
                    "/", "/index",
                    "/auth/**",
                    "/css/**", "/js/**", "/images/**",
                    "/error",
                    "/product/**",
                    "/user/**",
                    "/order/history", "/order/tracking/**" // Cho phép Order pages
                ).permitAll()

                // ADMIN ONLY 
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // CÒN LẠI: CẦN LOGIN 
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/j_spring_security_check")
                .defaultSuccessUrl("/")
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );
            
=======
                .csrf(csrf -> csrf.disable())
                .securityContext(context -> context
                    .requireExplicitSave(false)  // Tự động lưu SecurityContext vào session
                )
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/shoes", "/index", "/auth/**", "/css/**", "/js/**", "/images/**", "/error", "/product/**", "/user/**", "/cart/**", "/order/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/j_spring_security_check")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
                )
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // Nếu chưa login và cố truy cập trang yêu cầu auth, redirect đến login
                    response.sendRedirect("/auth/login");
                })
                )
                .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
                );

>>>>>>> main
        return http.build();
    }
}
