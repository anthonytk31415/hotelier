package com.tpd.staybooking.repository;

import com.tpd.staybooking.model.StayReservedDate;
import com.tpd.staybooking.model.StayReservedDateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

//StayReservationDateRepository:  This interface extends the JpaRepository<StayReservedDate, StayReservedDateKey> interface.
// By extending this interface, the repository gains access to a wide range of predefined methods for CRUD (Create, Read, Update, Delete)
// operations on the StayReservedDate entity.
@Repository
public interface StayReservationDateRepository extends JpaRepository<StayReservedDate, StayReservedDateKey> {

    // @Query - This annotation is used to define a custom query that goes beyond
    // the standard methods provided by JpaRepository.
    // The query is written in JPQL (Java Persistence Query Language), a language
    // similar to SQL but specific to JPA
    @Query(value = "SELECT srd.id.stay_id FROM StayReservedDate srd WHERE srd.id.stay_id IN ?1 AND srd.id.date BETWEEN ?2 AND ?3 GROUP BY srd.id.stay_id") // Hibernate
    // Query(value = "SELECT stay_id FROM stay_reserved_date WHERE id IN (:stayIds)
    // date BETWEEN ?1 AND ?2", nativeQuery = true) // It can also be written like
    // this.
    Set<Long> findByIdInAndDateBetween(List<Long> stayIds, LocalDate startDate, LocalDate endDate);
}
