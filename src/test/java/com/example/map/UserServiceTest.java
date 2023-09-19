package com.example.map;

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
import com.example.map.service.user.UserDetailsImpl;
import com.example.map.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RMapCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@WebMvcTest(controllers = UserController.class)
//@Import({WebSecurityConfig.class, UserService.class})
//@SpringBootTest
public class UserServiceTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtUtilities jwtUtilities;
    @Mock
    private UserCache userCache;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserService userService;
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
    @Test
    public void registrationTest(){
        when(roleRepository.findByName(any())).thenReturn(Optional.of(new Role(ERole.ROLE_ADMIN)));
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encodedPassword");

        Set<String> roles = new HashSet<>();
        roles.add("admin");
        RegistrationRequest registrationRequest = new RegistrationRequest("Test", "test@gmail.com",
                roles, "Test123");

        ResponseEntity<Object> response = userService.registration(registrationRequest);
        RegistrationRequest userImp = (RegistrationRequest) response.getBody();
        Assertions.assertThat(userImp.getUsername()).isEqualTo("Test");
    }

    @Test
    public void loginUser(){
        LoginRequest loginRequest = new LoginRequest("HojjetRE", "Hojjat123");
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);
        RMapCache<String, String> users = mock(RMapCache.class);
        when(userCache.getUserCache()).thenReturn(users);
        when(userCache.getUserCache().get(any())).thenReturn(null);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(UserDetailsImpl.build(userImp));
        ResponseEntity<Object> response = userService.login(loginRequest);
        assertThat(((UserInfoResponse) response.getBody()).getEmail()).isEqualTo("hojjat@gmail.com");
    }
}
