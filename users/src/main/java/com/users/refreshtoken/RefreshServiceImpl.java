package com.users.refreshtoken;


import com.users.exception.NotFoundException;
import com.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshServiceImpl implements RefreshService{

    private final UserRepo customerRepo;

    private final RefreshRepo refreshRepo;

    @Override
    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken=RefreshToken.builder()
                .customer(customerRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found")))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();
        return refreshRepo.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now())<0){
            refreshRepo.delete(token);
            throw new NotFoundException("Need to Login");
        }
        return token;
    }
}
