package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Otp;
import com.example.volunteer_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByUser(User user);
    void deleteByUser(User user);
}