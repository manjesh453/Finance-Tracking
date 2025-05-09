package com.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    private final String SECRET = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";

    private final Key jwtSigningKey = Keys.hmacShaKeyFor(hexStringToByteArray(SECRET));

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public void validateToken(final String token) {
        Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token);
    }
}
