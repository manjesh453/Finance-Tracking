package com.users.helper;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String status;
    private float income;
    private float expenses;
}
