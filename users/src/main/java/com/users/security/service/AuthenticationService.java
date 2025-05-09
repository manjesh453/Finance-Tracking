package com.users.security.service;

import com.users.security.helper.JwtAuthenticationResponse;
import com.users.security.helper.SignUpRequest;
import com.users.security.helper.SigninRequest;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface AuthenticationService {
    String signup(SignUpRequest request, String siteURL) throws MessagingException, UnsupportedEncodingException;

    JwtAuthenticationResponse signin(SigninRequest request);

    String forgetPassword(SigninRequest request, String siteURL) throws MessagingException, UnsupportedEncodingException;

    String changeUserPassword(String email, String siteURL) throws MessagingException, UnsupportedEncodingException;
}
