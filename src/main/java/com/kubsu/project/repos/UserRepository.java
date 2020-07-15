package com.kubsu.project.repos;

import com.kubsu.project.domain.Person;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Person, Integer> {
}
