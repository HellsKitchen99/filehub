package com.MVP.Config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.MVP.Models.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JwtUtil {

    //переменная с подписью из .env
    @Value("${token.signing.key}")
    private String jwtSigningKey;
    
    //генерация jwt токена (для логина)
    public String generateJwtToken(Authentication authentication) {

        //получаем данные из authentication
        String username = authentication.getName();
        Date issuedAt = new Date();
        Date expiredAt = Date.from(Instant.now().plus(15, ChronoUnit.DAYS));
        Collection<? extends GrantedAuthority> colecctionOfRoles = authentication.getAuthorities();
        List<String> roles = colecctionOfRoles.stream().map(GrantedAuthority::getAuthority).toList();

        //делаем ключ
        Key key = Keys.hmacShaKeyFor(jwtSigningKey.getBytes());

        //строим токен
        String token = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(issuedAt)
        .setExpiration(expiredAt)
        .claim("roles", roles)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

        return token;
    }

    //достаем имя
    public String extractUsername(String token) {
        //разбираем токен
        Claims claims = decodeJwtToken(token);

        //достаем имя
        String username = claims.getSubject();

        return username;
    }

    //достаем дату создания токена
    public Date extractDateOfIssue(String token) {
        //разбираем токен
        Claims claims = decodeJwtToken(token);

        //достаем дату создания
        Date issuedAt = claims.getIssuedAt();

        return issuedAt;
    }

    //достаем дату выгорания токена
    public Date extractDateOfExpiration(String token) {
        //разбираем токен
        Claims claims = decodeJwtToken(token);

        //достаем дату истечения токена
        Date expiredAt = claims.getExpiration();

        return expiredAt;
    }

    //достаем роли
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        //разбираем токен
        Claims claims = decodeJwtToken(token);

        //достаем роли
        Object roles = claims.get("roles");

        return (List<String>) roles;
    }

    //функция декодирования JWT токена
    private Claims decodeJwtToken(String token) {
        //делаем ключ
        Key key = Keys.hmacShaKeyFor(jwtSigningKey.getBytes());

        //разбираем токен
        Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

        return claims;
    }

    //валиадция токена
    public boolean validateToken(String token, UserDetails userDetails) {
        //достаем имя   
        String username = extractUsername(token);

        //достаем дату истечения
        Date expiredAt = extractDateOfExpiration(token);

        //проверка имени и даты истечения
        if (!username.equals(userDetails.getUsername()) || expiredAt.before(new Date())) {
            return false;
        }
        return true;
    }

    //генерация токена (для регистрации)
    public String generateJwtTokenForRegistry(String username, Role role) {

        //собираем данные
        Date issuedAt = new Date();
        Date expiredAt = Date.from(Instant.now().plus(15, ChronoUnit.DAYS));

        //делаем ключ
        Key key = Keys.hmacShaKeyFor(jwtSigningKey.getBytes());

        //строим токен
        String token = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(issuedAt)
        .setExpiration(expiredAt)
        .claim("roles", List.of("ROLE_" + role.name()))
        .signWith(key)
        .compact();

        return token;
    }
}
