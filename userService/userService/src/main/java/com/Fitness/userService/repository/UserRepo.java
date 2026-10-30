package com.Fitness.userService.repository;

import com.Fitness.userService.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {

    boolean existsByEmail(@NotNull(message="  EMIL IS REQUIRED") @Email(message="enter the valid mail") String email);

    Boolean existsBykeycloakId(String userId);

    User findByEmail(@NotNull(message="  email IS REQUIRED") @Email(message="enter the valid mail") String email);
}
