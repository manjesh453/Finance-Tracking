package com.users.controller;

import com.users.helper.ChangePasswordDto;
import com.users.helper.DateDto;
import com.users.helper.UserDto;
import com.users.helper.UserResponseDto;
import com.users.service.UserService;
import com.users.shared.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/update/{userId}")
    public String updateUser(@RequestBody UserDto user, @PathVariable Long userId) {
        return userService.updateUser(user, userId);
    }

    @GetMapping("/delete/{userId}")
    public String deleteUserStatus(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/getAll")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/verifyUser/{userId}")
    public String verifyUser(@PathVariable Long userId) {
        return userService.changeStatus(userId);
    }

    @GetMapping("/getById/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/getByStatus")
    public List<UserResponseDto> getUsersByStatus(@PathVariable String status) {
        return userService.findByStatus(Status.valueOf(status));
    }

    @PostMapping("/filterByDate/{userId}")
    public List<UserResponseDto> filterUsersByDate(@RequestBody DateDto dateDto,@PathVariable Long userId) {
        return userService.findUserByTime(dateDto.getStartDate(), dateDto.getEndDate(),userId);
    }

    @PostMapping("/changePassword/{email}")
    public String changePassword(@PathVariable String email, @RequestBody ChangePasswordDto dto) {
        return userService.changePassword(email, dto);
    }

    @GetMapping("/test")
    public String testMe() {
        return "I am user";
    }
}
