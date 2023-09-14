package com.example.map.service.user;

import com.example.map.cachemanager.user.UserCache;
import com.example.map.dto.user.LoginRequest;
import com.example.map.dto.user.RegistrationRequest;
import com.example.map.dto.user.UserInfoResponse;
import com.example.map.model.ERole;
import com.example.map.model.Role;
import com.example.map.model.UserImp;
import com.example.map.repository.RoleRepository;
import com.example.map.repository.UserRepository;
import com.example.map.service.security.JwtUtilities;
import org.redisson.api.RMapCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtilities jwtUtilities;
    private final UserCache userCache;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtUtilities jwtUtilities, UserCache userCache) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtUtilities = jwtUtilities;
        this.userCache = userCache;
    }

    public ResponseEntity<Object> registration(RegistrationRequest registrationRequest){
        if (userRepository.existsByUsername(registrationRequest.getUsername())){
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(registrationRequest.getEmail())){
            return new ResponseEntity<>("Email is taken", HttpStatus.BAD_REQUEST);
        }

        UserImp user = new UserImp(registrationRequest.getUsername(),
                registrationRequest.getEmail(),
                encoder.encode(registrationRequest.getPassword()));

        Set<String> strRoles = registrationRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role ->{
            switch (role){
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                    break;
                case "user":
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                    break;
            }
        });

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(registrationRequest);
    }

    public ResponseEntity<Object> login(LoginRequest loginRequest){
        RMapCache<String, String> users = userCache.getUserCache();
        if (users.get(loginRequest.getUsername()) != null){
            return ResponseEntity.ok(users.get(loginRequest.getUsername()));
        }
        else {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtilities.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            UserInfoResponse userInfoResponse = new UserInfoResponse(jwt,
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);
            userCache.tokenCache(userInfoResponse);
            return ResponseEntity.ok(userInfoResponse);
        }

    }
}
