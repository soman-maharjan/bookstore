package com.example.bookstore.user;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
public class UserService{
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public Optional<User> getByUsername(String username) {
        return this.repository.findByUsername(username);
    }
}
