package com.example.map.dto.user;

import com.example.map.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RegistrationRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    private Set<String> role;
    @NotBlank
    private String password;
}
