package org.example.projeto.config;

import org.example.projeto.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;  // ← NOVO

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilitar CSRF (APIs REST stateless não precisam)
                .csrf(csrf -> csrf.disable())

                // 2. Gerenciamento de sessão: STATELESS (não guarda sessão)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Tratamento de exceções de autenticação (NOVO)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // 4. Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()      // /auth/register, /auth/login são públicos
                        .requestMatchers("/user/**").authenticated()  // /user/qualquercoisa exige login
                        .requestMatchers("/api/cart/**").authenticated() // ← CARRINHO requer autenticação
                        .anyRequest().permitAll()                     // por padrão, libera
                )

                // 5. Adicionar nosso filtro JWT antes do filtro padrão de usuário/senha
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para gerenciar autenticação (usado no controller de login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean para criptografar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}