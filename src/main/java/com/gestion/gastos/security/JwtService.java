package com.gestion.gastos.security;

import com.gestion.gastos.model.entity.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "mi_clave_supersecreta_de_256_bits_12345678901234567890"; // mínimo 256 bits

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId()); // 👈 Incluye el ID
        claims.put("nombre", usuario.getNombre()); // opcional
        // También puedes incluir el rol si lo necesitas
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getId().toString()) // sub = id estable

               /// .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
  /*  public Long extractUserId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }*/
    /** sub = userId (como String) */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

}
