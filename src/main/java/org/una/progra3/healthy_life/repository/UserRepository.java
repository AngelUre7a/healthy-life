package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.User;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    
    @Modifying
    @Query("DELETE FROM User u WHERE u.email LIKE %:emailPattern%")
    void deleteByEmailContaining(@Param("emailPattern") String emailPattern);
    
    List<User> findByEmailContaining(String emailPattern);
}
