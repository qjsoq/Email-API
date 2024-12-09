package com.example.emailapp.repository;

import com.example.emailapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLoginIgnoreCase(String login);

    Boolean existsByLogin(String email);
}
