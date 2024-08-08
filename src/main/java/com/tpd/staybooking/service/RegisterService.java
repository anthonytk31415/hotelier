package com.tpd.staybooking.service;

import com.tpd.staybooking.exception.UserAlreadyExistException;
import com.tpd.staybooking.model.Authority;
import com.tpd.staybooking.model.User;
import com.tpd.staybooking.model.UserRole;
import com.tpd.staybooking.repository.AuthorityRepository;
import com.tpd.staybooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService { // service class responsible for handling user registration logic.
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Method overloading is not possible here; create a new constructor

    @Transactional // Because there are two write operations inside, if one fails, all will fail.
    public void add(User user, UserRole role) {
        if (userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistException("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));
    }
}
