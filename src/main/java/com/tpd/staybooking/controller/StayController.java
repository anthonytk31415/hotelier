package com.tpd.staybooking.controller;

import com.tpd.staybooking.model.Reservation;
import com.tpd.staybooking.model.Stay;
import com.tpd.staybooking.model.User;
import com.tpd.staybooking.service.ReservationService;
import com.tpd.staybooking.service.StayService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/*
The controller interacts with the StayService and ReservationService to perform operations such as listing stays,
getting stay details, adding stays, deleting stays, and listing reservations associated with a specific sta

This class defines endpoints for performing CRUD (Create, Read, Update, Delete) operations on stays and reservations.
It uses the provided services to interact with the underlying data and responds to incoming HTTP requests with appropriate
data and actions. - Cannot change, can only add, read, remove.*/
@RestController // This means its a Spring MVC controller responsible for handling HTTP requests
                // and generating responses.
public class StayController {

    private final StayService stayService;
    private final ReservationService reservationService;

    public StayController(StayService stayService, ReservationService reservationService) {

        this.stayService = stayService;
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/stays")
    public List<Stay> listStays(Principal principal) { // Principal object represents an authenticated user
        return stayService.listByUser(principal.getName());
    }

    @GetMapping(value = "/stays/{stayId}")
    public Stay getStay(@PathVariable Long stayId, Principal principal) { // Previously, we used @RequestParam to
                                                                          // directly pass the hostname. We changed it
                                                                          // to Principal because it is more secure.
                                                                          // With @RequestParam, if someone inputs
                                                                          // another user's username, they can directly
                                                                          // get the information. However, with
                                                                          // Principal, you can only get the username
                                                                          // using an authorized token, so you can only
                                                                          // access your own information and not someone
                                                                          // else's. It's more secure.
        return stayService.findByIdAndHost(stayId, principal.getName());
    }

    // This API allows authenticated users to add new stays to the system
    @PostMapping("/stays")
    public void addStay(
            @RequestParam("name") String name, // @RequestParam annotations to extract input parameters from the
                                               // request, such as name, address, description, guest number, images, and
                                               // the authenticated user's principal.
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("guest_number") int guestNumber,
            @RequestParam("images") MultipartFile[] images,
            Principal principal) {

        Stay stay = new Stay.Builder() // The Stay.Builder class is used to facilitate the construction of the Stay
                                       // object.
                .setName(name)
                .setAddress(address)
                .setDescription(description)
                .setGuestNumber(guestNumber)
                .setHost(new User.Builder().setUsername(principal.getName()).build()) // The authenticated user is
                                                                                      // considered the host of the
                                                                                      // stay. A new User object is
                                                                                      // created using the builder
                                                                                      // pattern, setting the username
                                                                                      // as the principal's name. This
                                                                                      // ensures that the stay is
                                                                                      // associated with the user who is
                                                                                      // creating it.
                .build();
        stayService.add(stay, images); // This method is responsible for adding the stay to the system and associating
                                       // the images with it.
    }

    @DeleteMapping("/stays/{stayId}")
    public void deleteStay(@PathVariable Long stayId, Principal principal) {
        stayService.delete(stayId, principal.getName());
    }

    @GetMapping(value = "/stays/reservations/{stayId}")
    public List<Reservation> listReservations(@PathVariable Long stayId) {
        return reservationService.listByStay(stayId);
    }
}