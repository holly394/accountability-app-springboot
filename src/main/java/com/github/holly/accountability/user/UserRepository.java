package com.github.holly.accountability.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameIgnoreCase(String username);

    User findUserById(Long userId);

    List<User> findUsersByUsernameContainsIgnoreCase(String username);

    @Query(
        """
        SELECT u FROM User u
        WHERE (u.id != :userId)
        """
    )
    Page<User> getAllExceptCurrentUser(Long userId, Pageable pageable);

}
