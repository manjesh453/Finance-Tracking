package com.users.security.service.serviceimpl;

import com.users.entity.Users;
import com.users.repo.UserRepo;
import com.users.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserRepo userRepository;
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


    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        final Date issuedAt = extractIssuedAt(token);

        Users user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userName.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !wasPasswordChangedAfterTokenIssued(user, issuedAt);
    }

    private boolean wasPasswordChangedAfterTokenIssued(Users user, Date issuedAt) {
        Date passwordChanged = user.getLastPasswordChanged();
        return passwordChanged != null && passwordChanged.after(issuedAt);
    }


    private Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Users customer = (Users) userDetails;

        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("Role", customer.getRole())
                .claim("ID", customer.getId())
                .claim("iat",new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(jwtSigningKey, SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSigningKey).build().parseClaimsJws(token)
                .getBody();
    }
}
