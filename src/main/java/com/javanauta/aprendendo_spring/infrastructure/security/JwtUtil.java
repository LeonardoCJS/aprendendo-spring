package com.javanauta.aprendendo_spring.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {

    // Chave secreta usada para assinar e vereficar tokens JWT
    private final SecretKey secretKey;

    // Construtor que gera uma chave secreta segura para assinatura usando o algoritmo HS256
    public JwtUtil() {
        // Gera uma chave secreta para o algoritmo de assinatura HS256
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Gera um token JWT com o nome de usuario e validade de 1 hora
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)// Define o nome de usuario como o assunto do token
                .setIssuedAt(new Date())// Define a data e hora de emissao do token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Define a data e hora de expiração(1h a partir da emissao)
                .signWith(secretKey)// Assina o token com a chave secreta
                .compact();// Constroi o token JWT
    }

    // Extrai as claims do token JWT (informaçoes adicionais do token)
    public Claims extractClaim(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // Define a chave secreta para validar a assinatura do token
                .build()
                .parseClaimsJws(token)// Analisa o token JWT e obtem as claims
                .getBody(); // Retorna o corpo das claims
    }

    // Extrai o nome de usuario de token JWT
    public String extractUsername(String token) {
        // Obtem o assunto (nome de usuario) das claims do token
        return extractClaim(token).getSubject();
    }

    // Verifica se o token JWT esta expirado
    private boolean isTokenExpired(String token) {
        // Compara a data de expiração de token com a data atual
        return extractClaim(token).getExpiration().before(new Date());
    }

    // Valida o token JWT verificando o nome de usuario e se o token nao esta expirado
    public boolean isTokenValid(String token, String username) {
        // Extrai o nome de usuario do token
        final String extractUsername = extractUsername(token);
        // Verifica se o nome de usuario do token corresponde ao fornecido e se o token nao esta expirado
        return (extractUsername.equals(username) && !isTokenExpired(token));
    }
}