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
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/update/{userId}")
    public String updateUser(@RequestBody UserDto user, @PathVariable Long userId) {
        return userService.updateUser(user, userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/delete/{userId}")
    public String deleteUserStatus(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/getAll")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/verifyUser/{userId}")
    public String verifyUser(@PathVariable Long userId) {
        return userService.changeStatus(userId);
    }

    @GetMapping("/getById/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/getByStatus/{status}")
    public List<UserResponseDto> getUsersByStatus(@PathVariable String status) {
        return userService.findByStatus(Status.valueOf(status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/filterByDate")
    public List<UserResponseDto> filterUsersByDate(@RequestBody DateDto dateDto) {
        return userService.findUserByTime(dateDto.getStartDate(), dateDto.getEndDate());
    }

    @PostMapping("/changePassword/{userId}")
    public String changePassword(@PathVariable Long userId, @RequestBody ChangePasswordDto dto) {
        return userService.changePassword(userId, dto);
    }

    @GetMapping("/test")
    public String testMe() {
        return "I am user";
    }

    @GetMapping("/admin/analytics")
    public Map<String,Integer> getUserAnalytics(){
        return userService.getAnaytics();
    }
}
