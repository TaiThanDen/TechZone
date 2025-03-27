package org.asm_java6.asm_java6.Security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.asm_java6.asm_java6.Repository.UserRepository;
import org.asm_java6.asm_java6.entity.User;
import org.asm_java6.asm_java6.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public SecurityConfig(CustomUserDetailsService userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) -> {

            HttpSession session = request.getSession();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                // Lưu user vào session
                session.setAttribute("LOGGED_IN_USER", user);
                System.out.println("Đăng nhập thành công, đã lưu user vào session: " + user.getEmail());

                // Xóa cookie userId cũ nếu có
                Cookie clearOld = new Cookie("userId", null);
                clearOld.setPath("/");
                clearOld.setMaxAge(0);
                response.addCookie(clearOld);

                // Tạo cookie mới
                Cookie userCookie = new Cookie("userId", user.getId().toString());
                userCookie.setMaxAge(86400); // 1 ngày
                userCookie.setPath("/");
                response.addCookie(userCookie);
            } else {
                System.err.println("User not found with email: " + email);
            }

            // Điều hướng sau khi đăng nhập
            response.sendRedirect("/");
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            System.err.println("Authentication failed: " + exception.getMessage());
            response.sendRedirect("/login?error");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/cart/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(customSuccessHandler())
                        .failureHandler(customFailureHandler())
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(86400)
                );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("userId") // Xoá cookie userId khi logout
                .permitAll()
        );

        http.exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                )
                .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new AntPathRequestMatcher("/**")
                )
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
