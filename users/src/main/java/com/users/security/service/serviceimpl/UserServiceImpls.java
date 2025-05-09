package com.users.security.service.serviceimpl;

import com.users.entity.Users;
import com.users.exception.EmailNotFoundException;
import com.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpls implements UserDetailsService {

    private final UserRepo customerRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> users= customerRepo.findByEmail(username);
        if(users.isPresent()){
            return users.get();
        }else {
            throw new EmailNotFoundException("User","email",username);
        }
    }

}
