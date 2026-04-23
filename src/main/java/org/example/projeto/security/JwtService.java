package org.example.projeto.security;

import org.example.projeto.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 1. Gerar o token a partir do usuário
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getEmail())          // email fica dentro do token
                .setIssuedAt(now)                     // quando foi criado
                .setExpiration(expiryDate)            // quando expira
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // assina com a chave
                .compact();
    }

    // 2. Extrair o email (subject) do token
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 3. Validar se o token é válido (não expirou, não foi adulterado)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // token inválido ou expirado
        }
    }

    // Metodo auxiliar: converte a string secreta em uma chave criptográfica
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}