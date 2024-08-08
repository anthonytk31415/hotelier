package com.tpd.staybooking.controller;

import com.tpd.staybooking.model.User;
import com.tpd.staybooking.model.UserRole;
import com.tpd.staybooking.service.RegisterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // This annotation marks the class as a Spring MVC controller that handles
                // RESTful web requests
// and automatically serializes/deserializes the response/request objects
// to/from JSON.
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register/guest")
    public void addGuest(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_GUEST);
    }

    @PostMapping("/register/host")
    public void addHost(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_HOST);
    }
}

// public void addHost(@RequestBody User user): This is the method that handles
// the host user registration.
// It takes a User object as a parameter, which is received from the request
// body (@RequestBody annotation).
// The user data (in JSON format) will be automatically converted to a User
// object by Spring.