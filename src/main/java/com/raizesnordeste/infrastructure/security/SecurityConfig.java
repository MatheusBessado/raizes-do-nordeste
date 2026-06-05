package com.raizesnordeste.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Configuracao de segurança da aplicacao
// Referencia: https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private static final String[] PUBLIC_ROUTES = {
        "/auth/login",
        "/auth/cadastro",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/api-docs/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/error"
        // "/h2-console/**"  // so pra dev, nao colocar em producao
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ROUTES).permitAll()
                .requestMatchers(HttpMethod.GET, "/unidades/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/unidades/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/unidades/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/unidades/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/produtos/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/produtos/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers(HttpMethod.PUT, "/produtos/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers("/estoque/**").hasAnyRole("ADMIN", "GERENTE", "COZINHA")
                .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("CLIENTE", "ATENDENTE")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasAnyRole("COZINHA", "ATENDENTE", "ADMIN", "GERENTE")
                .requestMatchers(HttpMethod.GET, "/pedidos/**").authenticated()
                .requestMatchers("/pagamentos/callback").permitAll()
                .requestMatchers("/fidelidade/**").authenticated()
                .requestMatchers("/relatorios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/usuarios/**").authenticated()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
