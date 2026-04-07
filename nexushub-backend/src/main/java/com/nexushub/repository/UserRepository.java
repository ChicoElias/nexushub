package com.nexushub.repository;

import com.nexushub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByAuthToken(String authToken);

    boolean existsByEmail(String email);
}
