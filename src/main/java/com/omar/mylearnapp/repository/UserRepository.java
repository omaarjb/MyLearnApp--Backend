package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkId(String clerkId);
    boolean existsByClerkId(String clerkId);
    void deleteByClerkId(String clerkId);

}
