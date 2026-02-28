package com.venediktov.carservice.config;

import com.venediktov.carservice.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final com.venediktov.carservice.services.AuthService authService;

    public SecurityConfig(CustomUserDetailsService userDetailsService, com.venediktov.carservice.services.AuthService authService) {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().disable().and() 
            .sessionManagement().sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/img/**", "/auth/register", "/auth/login", "/h2-console/**").permitAll()
                .antMatchers(org.springframework.http.HttpMethod.GET, "/car/**").permitAll()
                .antMatchers("/car/**").hasRole("ADMIN")
                .antMatchers("/rentals/**").authenticated()
                .antMatchers("/auth/me", "/auth/logout").authenticated()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(new TokenAuthenticationFilter(authService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
