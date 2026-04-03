package com.friendbook;
import com.friendbook.utility.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/auth/**", "/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f.disable())
                .headers(h -> h.cacheControl(c -> c.disable()))
                .logout(l -> l.logoutUrl("/auth/logout")
                        .addLogoutHandler((req, res, auth) -> {
                            Cookie c = new Cookie("jwtToken", null);
                            c.setPath("/");
                            c.setHttpOnly(true);
                            c.setMaxAge(0);
                            res.addCookie(c);
                        })
                        .logoutSuccessUrl("/login?logout").permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico");
    }
}