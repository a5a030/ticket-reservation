package com.byunsum.ticket_reservation.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {
    private JwtTokenProvider provider(String secret, long accessMs, long refreshMs) {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secret", secret);
        ReflectionTestUtils.setField(provider, "tokenValidityInMilliseconds", accessMs);
        ReflectionTestUtils.setField(provider, "refreshTokenValidTime", refreshMs);
        provider.init();
        return provider;
    }

    @Test
    void createParseAccessToken() {
        var p = provider("MySuperSecretKeyThatIsAtLeast32CharactersLong!", 3_600_000L, 86_400_000L);
        String jwt = p.createToken("user123", "USER");

        assertTrue(p.validateToken(jwt));
        assertEquals("user123", p.getName(jwt));
        assertEquals("USER", p.getRole(jwt));
    }

    @Test
    void expiredTokenShouldFailValidation() throws Exception {
        var p = provider("MySuperSecretKeyThatIsAtLeast32CharactersLong!", 200L, 1_000L);
        String jwt = p.createToken("u1", "USER");

        TimeUnit.MILLISECONDS.sleep(450); //만료대기
        assertFalse(p.validateToken(jwt));
        assertThrows(ExpiredJwtException.class, () -> {
            p.getName(jwt);
        });
    }

    @Test
    void refreshTokenGenerated() {
        var p = provider("MySuperSecretKeyThatIsAtLeast32CharactersLong!", 3_600_000L, 86_400_000L);
        String refresh = p.createRefreshToken("user123", "USER");
        assertTrue(p.validateToken(refresh));
        assertEquals("user123", p.getName(refresh));
    }
}
