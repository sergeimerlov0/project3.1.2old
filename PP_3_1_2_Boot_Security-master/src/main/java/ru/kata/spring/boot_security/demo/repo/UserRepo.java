package ru.kata.spring.boot_security.demo.repo;

import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.User;

public interface UserRepo extends CrudRepository<User, Long> {
    User findByUsername(String name);
}
