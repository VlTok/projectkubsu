package com.kubsu.project.repos;

import com.kubsu.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    User findByActivationCode(String code);
}
