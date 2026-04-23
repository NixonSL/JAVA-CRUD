package org.example.projeto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Pegar o token do cabeçalho Authorization
        String token = extractToken(request);

        // 2. Se existe token e ele é válido
        if (token != null && jwtService.validateToken(token)) {
            // 3. Extrair o email do token
            String email = jwtService.extractEmail(token);

            // 4. Buscar o usuário no banco
            UserDetails user = userDetailsService.loadUserByUsername(email);

            // 5. Criar uma autenticação para o Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // 6. Colocar o usuário como autenticado no contexto da requisição
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. Seguir com a requisição (vai para o controller ou próximo filtro)
        filterChain.doFilter(request, response);
    }

    // Metodo auxiliar: extrai o token do header "Authorization"
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // remove "Bearer " e pega só o token
        }
        return null;
    }
}