package com.example.map.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private String token;
    private String username;
    private String email;
    private List<String> roles;
}
