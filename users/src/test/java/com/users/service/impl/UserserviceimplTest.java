package com.users.service.impl;

import com.users.entity.Account;
import com.users.entity.Users;
import com.users.exception.ResourcenotFoundException;
import com.users.helper.ChangePasswordDto;
import com.users.helper.UserDto;
import com.users.helper.UserResponseDto;
import com.users.repo.AccountRepo;
import com.users.repo.UserRepo;
import com.users.shared.Status;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserserviceimplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private AccountRepo accountRepo;
    @InjectMocks
    private Userserviceimpl userserviceimpl;
    private Users mockUser;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setFirstname("Srijan");
        mockUser.setLastname("Shrestha");
        mockUser.setEmail("srijanshrestha@gmail.com");
        mockUser.setStatus(Status.UNVERIFIED);
        mockUser.setVerificationCode(1234);

        mockAccount = new Account();
        mockAccount.setTotalIncome(1000);
        mockAccount.setTotalExpenses(500);
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto();
        userDto.setFirstname("Srijan");
        userDto.setLastname("Shrestha");
        userDto.setContactNumber("9999999999");

        lenient().when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));

        String result = userserviceimpl.updateUser(userDto, 1L);
        assertEquals("User details have been updated successfully", result);
        assertEquals("Srijan Shrestha", mockUser.getFullname());
    }

    @Test
    void getUserById() {
        UserResponseDto userResponse = new UserResponseDto();
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountRepo.findByUsers(mockUser)).thenReturn(mockAccount);
        when(modelMapper.map(mockUser, UserResponseDto.class)).thenReturn(userResponse);

        UserResponseDto result = userserviceimpl.getUserById(1L);
        assertNotNull(result);
    }

    @Test
    void getUserByEmail() {
        when(userRepo.findByEmail("srijanshrestha@gamil.com")).thenReturn(Optional.of(mockUser));
        when(accountRepo.findByUsers(mockUser)).thenReturn(mockAccount);
        when(modelMapper.map(mockUser, UserResponseDto.class)).thenReturn(new UserResponseDto());

        UserResponseDto result = userserviceimpl.getUserByEmail("srijanshrestha@gamil.com");
        assertNotNull(result);
    }

    @Test
    void deleteUser() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        String result = userserviceimpl.deleteUser(1L);
        assertEquals(Status.DELETE, mockUser.getStatus());
        assertEquals("User has been deleted successfully", result);
    }

    @Test
    void getAllUsers() {
        List<Users> userList = List.of(mockUser);
        when(userRepo.findAll()).thenReturn(userList);
        when(modelMapper.map(any(Users.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        List<UserResponseDto> result = userserviceimpl.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void changeStatus() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        String result = userserviceimpl.changeStatus(1L);
        assertEquals(Status.ACTIVE, mockUser.getStatus());
        assertEquals("User status has been changed successfully", result);
    }

    @Test
    void findByStatus() {
        List<Users> userList = List.of(mockUser);
        when(userRepo.findByStatus(Status.UNVERIFIED)).thenReturn(userList);
        when(modelMapper.map(any(Users.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        List<UserResponseDto> result = userserviceimpl.findByStatus(Status.UNVERIFIED);
        assertEquals(1, result.size());
    }

    @Test
    void findUserByTime() {
        Date start = new Date(System.currentTimeMillis() - 10000);
        Date end = new Date();
        when(userRepo.findByCreatedDateBetween(start, end)).thenReturn(List.of(mockUser));
        when(modelMapper.map(any(Users.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        List<UserResponseDto> result = userserviceimpl.findUserByTime(start, end);
        assertEquals(1, result.size());
    }

    @Test
    void testSendVerification() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        userserviceimpl.sendVerification(mockUser, "http://localhost:8080");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testVerifySuccess() {
        when(userRepo.findByVerificationCode(1234)).thenReturn(mockUser);
        boolean result = userserviceimpl.verify("1234");
        assertTrue(result);
        assertEquals(Status.ACTIVE, mockUser.getStatus());
        assertEquals(0, mockUser.getVerificationCode());
    }
    @Test
    void testVerifyFail() {
        when(userRepo.findByVerificationCode(1111)).thenReturn(null);
        boolean result = userserviceimpl.verify("1111");
        assertFalse(result);
    }

    @Test
    void getUserByIdwhenfail() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourcenotFoundException.class, () -> userserviceimpl.getUserById(1L));

    }

    @Test
    void changePassword() {
        String email = "test@example.com";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword(oldPassword);
        dto.setNewPassword(newPassword);

        Users user = new Users();
        user.setEmail(email);
        user.setPassword("hashedOldPassword");
        user.setStatus(Status.ACTIVE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedNewPassword");

        String result = userserviceimpl.changePassword(1L, dto);

        assertEquals("Password has been changed successfully", result);
        assertEquals("hashedNewPassword", user.getPassword());
        assertNotNull(user.getLastPasswordChanged());
        verify(userRepo).saveAndFlush(user);
    }
    @Test
    void changePassword_invalidOldPassword() {
        String email = "test@example.com";
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("wrongOld");
        dto.setNewPassword("newPass");

        Users user = new Users();
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setStatus(Status.ACTIVE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(false);

        String result = userserviceimpl.changePassword(1L, dto);

        assertEquals("Invalid old password", result);
        verify(userRepo, never()).saveAndFlush(any());
    }
}
