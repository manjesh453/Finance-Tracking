package com.users.security.service.serviceimpl;

import com.users.entity.Account;
import com.users.entity.Users;
import com.users.refreshtoken.RefreshRepo;
import com.users.refreshtoken.RefreshService;
import com.users.refreshtoken.RefreshToken;
import com.users.repo.AccountRepo;
import com.users.repo.UserRepo;
import com.users.security.helper.JwtAuthenticationResponse;
import com.users.security.helper.SignUpRequest;
import com.users.security.helper.SigninRequest;
import com.users.security.service.AuthenticationService;
import com.users.security.service.JwtService;
import com.users.service.UserService;
import com.users.shared.Role;
import com.users.shared.Status;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo customerRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserService customerService;

    private final RefreshRepo refreshRepo;

    private final RefreshService refreshService;

    private final AccountRepo accountRepo;

    @Override
    public String signup(SignUpRequest request, String siteURL) throws MessagingException, UnsupportedEncodingException {
        var user = Users.builder().fullname(request.getFirstname() + " " + request.getLastname())
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).status(Status.UNVERIFIED)
                .lastPasswordChanged(new Date())
                .contactNumber(request.getContactNumber())
                .verificationCode(new Random().nextInt(999999))
                .build();
        Optional<Users> customer = customerRepo.findByEmail(user.getEmail());
        if (customer.isEmpty()) {
            Account account = new Account();
            account.setUsers(user);
            customerRepo.save(user);
            accountRepo.save(account);
            customerService.sendVerification(user, siteURL);
            return "User register successful";
        } else {
            return "Sorry this email has been already register";
        }
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Users user = customerRepo.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        if(user.getStatus()==Status.UNVERIFIED || user.getStatus()==Status.INACTIVE){
            throw new UsernameNotFoundException("Sorry your account might be in inactive status");
        }
        RefreshToken refreshToken = refreshRepo.findByCustomer(user);
        if (refreshToken == null) {
            refreshToken = refreshService.createRefreshToken(request.getEmail());
        }
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt)
                .refreshToken(refreshToken.getToken())
                .userId(user.getId())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    @Override
    public String forgetPassword(SigninRequest request, String siteURL) throws MessagingException, UnsupportedEncodingException {
        Users customer = customerRepo.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (customer != null) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setLastPasswordChanged(new Date());
            customer.setVerificationCode(new Random().nextInt(999999));
            customer.setStatus(Status.INACTIVE);
            customerRepo.save(customer);
            customerService.sendVerification(customer, siteURL);
            return "Verify link send in Email";
        } else {
            return "Sorry invalid Email";
        }
    }

    @Override
    public String changeUserPassword(String request, String siteURL) throws MessagingException, UnsupportedEncodingException {
        Users users = customerRepo.findByEmail(request).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        users.setPassword(passwordEncoder.encode(users.getFullname()));
        users.setStatus(Status.INACTIVE);
        users.setVerificationCode(new Random().nextInt(999999));
        users.setLastPasswordChanged(new Date());
        customerRepo.save(users);
        customerService.sendVerification(users, siteURL);
        return "User Password Change successful";
    }
}
