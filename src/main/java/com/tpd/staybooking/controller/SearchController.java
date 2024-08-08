package com.tpd.staybooking.controller;

import com.tpd.staybooking.exception.InvalidSearchDateException;
import com.tpd.staybooking.model.Stay;
import com.tpd.staybooking.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*This code defines a Spring MVC controller named SearchController responsible for handling search requests related to stays.
The controller processes input parameters for guest number, check-in and check-out dates, geographical coordinates (latitude and longitude),
and an optional distance parameter. It communicates with the SearchService to perform the actual search and filtering of stays.*/
@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = "/search")
    public List<Stay> searchStays(
            @RequestParam(name = "guest_number") int guestNumber,
            @RequestParam(name = "checkin_date") String start, // After Spring 4.0, you can directly use the LocalDate
                                                               // class to record time without extra parsing.
            @RequestParam(name = "checkout_date") String end,
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lon") double lon,
            @RequestParam(name = "distance", required = false) String distance) {
        LocalDate checkinDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate checkoutDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (checkinDate.equals(checkoutDate) || checkinDate.isAfter(checkoutDate)
                || checkinDate.isBefore(LocalDate.now())) {
            throw new InvalidSearchDateException("Invalid date for search");
        }
        return searchService.search(guestNumber, checkinDate, checkoutDate, lat, lon, distance);
    }
}