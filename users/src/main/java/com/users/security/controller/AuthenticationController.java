package com.users.security.controller;


import com.users.security.helper.JwtAuthenticationResponse;
import com.users.security.helper.SignUpRequest;
import com.users.security.helper.SigninRequest;
import com.users.security.service.AuthenticationService;
import com.users.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final UserService customerService;

    @PostMapping("/sign-up")
    public String signup(@Valid @RequestBody SignUpRequest request, HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.signup(request, getSiteURL(httpServletRequest));
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signin(@RequestBody SigninRequest request) {
        return authenticationService.signin(request);
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestBody SigninRequest request, HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.forgetPassword(request,getSiteURL(httpServletRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/changeUserPassword/{email}")
    public String changeUserPassword(@PathVariable String email,HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.changeUserPassword(email,getSiteURL(request));
    }

    private String getSiteURL(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    @GetMapping("/verify")
    public String verifyEmployee(@Param("code") String code) {
        if (customerService.verify(code)) {
            return "Verify sucessfully";
        } else {
            return "verify denied";
        }
    }
}
