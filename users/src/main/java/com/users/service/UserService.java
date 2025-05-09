package com.users.service;

import com.users.entity.Users;
import com.users.helper.ChangePasswordDto;
import com.users.helper.UserDto;
import com.users.helper.UserResponseDto;
import com.users.shared.Status;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService {

    String updateUser(UserDto userDto, Long userId);

    UserResponseDto getUserById(Long userId);

    UserResponseDto getUserByEmail(String email);

    String deleteUser(Long userId);

    List<UserResponseDto> getAllUsers();

    String changeStatus(Long cid);

    List<UserResponseDto> findByStatus(Status status);

    List<UserResponseDto> findUserByTime(Date startDate, Date endDate);

    void sendVerification(Users customer, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verify(String code);

    String changePassword(Long userId, ChangePasswordDto request);

    Map<String,Integer> getAnaytics();
}
