package com.users.refreshtoken;


public interface RefreshService {
    RefreshToken createRefreshToken(String username);

    RefreshToken verifyExpiration(RefreshToken token);
}
