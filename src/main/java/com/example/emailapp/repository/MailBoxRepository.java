package com.example.emailapp.repository;

import com.example.emailapp.domain.MailBox;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailBoxRepository extends JpaRepository<MailBox, Integer> {
    Optional<MailBox> findByEmailAddress(String email);
}
