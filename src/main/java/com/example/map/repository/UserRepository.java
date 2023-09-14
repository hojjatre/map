package com.example.map.repository;

import com.example.map.model.UserImp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserImp, Long> {
    UserImp findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
