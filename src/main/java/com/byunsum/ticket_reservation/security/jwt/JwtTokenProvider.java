package com.byunsum.ticket_reservation.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long tokenValidityInMilliseconds = 1000 * 60 * 60  * 2; //2시간
    private final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일


    public JwtTokenProvider() {
        //시크릿키는 256비트 이상 권장(Basee64 인코딩)
        String secret = "MySuperSecretKeyThatIsAtLeast32CharactersLong!";
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //토큰 생성
    public String createToken(String name, String role) {

        Claims  claims = Jwts.claims().setSubject(name);
        claims.setSubject(name);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry) //유효기간 명시
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다.: " + e.getMessage());
        } catch (JwtException e) {
            System.out.println("유효하지 않은 토큰입니다.: " + e.getMessage());
        }

        return false;
    }

    // 내부용 토큰 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) //jws 전용
                .getBody();
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String role = getRole(token);
        return List.of(new SimpleGrantedAuthority("ROLE_"+role));
    }

    public String createRefreshToken(String name, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);

        return Jwts.builder()
                .setSubject(name)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
