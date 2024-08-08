package com.tpd.staybooking.repository;

import com.tpd.staybooking.model.Stay;
import com.tpd.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
    // save() and delete() They are built-in and do not need to be written
    // separately.
    List<Stay> findByHost(User user); // Why not use findById() directly and why add host? The ID is unique, after
                                      // all. The reason is that adding host makes it more secure. Using an ID, you
                                      // can find any information, but with host, you must be under that host to find
                                      // it. It's more of a gatekeeper to keep the information correct.

    Stay findByIdAndHost(Long id, User host);

    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);
}