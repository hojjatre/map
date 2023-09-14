package com.example.map.controller.user;

import com.example.map.dto.user.LoginRequest;
import com.example.map.dto.user.RegistrationRequest;
import com.example.map.model.UserImp;
import com.example.map.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registration(@RequestBody RegistrationRequest registrationRequest){
        return userService.registration(registrationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest){
        return userService.login(loginRequest);
    }
}
