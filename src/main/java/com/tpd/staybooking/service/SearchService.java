package com.tpd.staybooking.service;

import com.tpd.staybooking.model.Stay;
import com.tpd.staybooking.repository.LocationRepository;
import com.tpd.staybooking.repository.StayRepository;
import com.tpd.staybooking.repository.StayReservationDateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
This class that provides methods for searching and filtering stays based on various criteria like guest number, check-in and
check-out dates, and location. The service interacts with different repositories to retrieve and filter stay information.
*/
@Service
public class SearchService {
    private final StayRepository stayRepository;
    private final StayReservationDateRepository stayReservationDateRepository;
    private final LocationRepository locationRepository;

    public SearchService(StayRepository stayRepository, StayReservationDateRepository stayReservationDateRepository,
            LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon,
            String distance) {
        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        if (stayIds == null || stayIds.isEmpty()) {
            return Collections.emptyList(); // Return an empty list that everyone can use. This approach is memory
                                            // efficient, and if you try to add items to it externally, it will throw an
                                            // exception.
                                            // Exception, because this API is immutable and can only be read, not
                                            // modified.
        }
        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate,
                checkoutDate.minusDays(1));
        List<Long> filteredStayIds = stayIds.stream() // Convert stayIds into a stream.
                .filter(stayId -> !reservedStayIds.contains(stayId))
                .collect(Collectors.toList()); //
        /*
         * The frequent API in the previous line can also be written as:
         * List<Long> filteredStayIds = new ArrayList<>();
         * for (Long stayId : stayIds) {
         * if (!reservationStayIds.contains(stayId) {
         * filteredStayIds.add(stayId);
         * }
         * }
         */
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }
}
