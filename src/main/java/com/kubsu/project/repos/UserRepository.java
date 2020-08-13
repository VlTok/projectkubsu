package com.kubsu.project.repos;

import com.kubsu.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);

    User findByUsernameAndEmail(String  username,String email);

    User findByActivationCode(String code);
}
