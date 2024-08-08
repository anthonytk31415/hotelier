package com.tpd.staybooking.controller;

import com.tpd.staybooking.model.Token;
import com.tpd.staybooking.model.User;
import com.tpd.staybooking.model.UserRole;
import com.tpd.staybooking.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*@RestController: This annotation indicates that the class is a Spring REST controller,
responsible for handling HTTP requests and generating corresponding responses.*/
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate/guest")
    public Token authenticateGuest(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_GUEST);
    }

    @PostMapping("/authenticate/host")
    public Token authenticateHost(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_HOST);
    }

}