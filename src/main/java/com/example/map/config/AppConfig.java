package com.example.map.config;

import com.example.map.model.ERole;
import com.example.map.model.Role;
import com.example.map.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public AppConfig(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        Role admin = new Role(ERole.ROLE_ADMIN);
//        roleRepository.save(admin);
//        Role user = new Role(ERole.ROLE_USER);
//        roleRepository.save(user);
    }
}
