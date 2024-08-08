package com.tpd.staybooking.service;

import com.tpd.staybooking.exception.ReservationCollisionException;
import com.tpd.staybooking.exception.ReservationNotFoundException;
import com.tpd.staybooking.model.*;
import com.tpd.staybooking.repository.ReservationRepository;
import com.tpd.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    // @Autowired automatically injects the dependencies (reservationRepository and
    // stayReservationDateRepository) when the service is instantiated. You asked me
    // to automatically connect with it. However, the fields in the current code are
    // all final, so even if not explicitly written, the connection will be
    // automatically provided.
    public ReservationService(ReservationRepository reservationRepository,
            StayReservationDateRepository stayReservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    // This method retrieves a list of reservations associated with a guest's
    // username using the reservationRepository.
    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest_Username(username);
        // return reservationRepository.findByGuest(new
        // User.Builder().setUsername(username).build()); =>
        // This is not good because the newly created user is not complete. A User has
        // additional fields besides the username, such as password and other
        // attributes. Currently, the code just creates it and immediately uses the
        // username, but in the future, if someone uses this User for other operations,
        // it can easily cause issues. It's not a good practice.
    }

    // This method retrieves a list of reservations associated with a particular
    // stay ID using the reservationRepository.
    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay_Id(stayId);
        // return reservationRepository.findByStay(new
        // Stay.Builder().setId(stayId).build());
    }

    // This method is used to add a new reservation. It checks for any collision
    // with existing reservations using the
    // stayReservationDateRepository, and if no collisions are found, it saves the
    // reservation and updates the stay reservation dates accordingly.
    @Transactional
    public void add(Reservation reservation) throws ReservationCollisionException {
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween( // Pass in 3 args
                List.of(reservation.getStay().getId()), // id Construct it into a list.
                                                        // Because in StayReservationDateRepository, stayIds is a list.
                reservation.getCheckinDate(), // The check-in and check-out of the reservation also need to be passed
                                              // in.
                reservation.getCheckoutDate().minusDays(1) // The minusDays means that check-in and check-out can be on
                                                           // the same day.
        );
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }

        List<StayReservedDate> reservedDates = new ArrayList<>();
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            StayReservedDateKey id = new StayReservedDateKey(reservation.getStay().getId(), date); // stay reserved date
                                                                                                   // is a composite
                                                                                                   // key, therefore add
                                                                                                   // two factors needed
                                                                                                   // to build a key
                                                                                                   // first
            StayReservedDate reservedDate = new StayReservedDate(id, reservation.getStay()); // and add the key to the
                                                                                             // list
            reservedDates.add(reservedDate);
        }
        stayReservationDateRepository.saveAll(reservedDates);
        reservationRepository.save(reservation);
    }

    // This method is used to delete a reservation. It retrieves the reservation
    // using the reservationRepository and username, then deletes the reservation
    // and updates the stay reservation dates
    @Transactional
    public void delete(Long reservationId, String username) {
        Reservation reservation = reservationRepository.findByIdAndGuest_Username(reservationId, username);
        // Reservation reservation =
        // reservationRepository.findByIdAndGuest(reservationId, new
        // User.Builder().setUsername(username).build());
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }
        reservationRepository.deleteById(reservationId);
    }
}
