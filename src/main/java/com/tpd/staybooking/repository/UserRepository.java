package com.tpd.staybooking.repository;

import com.tpd.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Spring Data JPA is a specification proposed by Hibernate. Hibernate is an implementation of JPA. Hibernate exposes all the necessary APIs through an interface so that others can also use them.
// Here, because JpaRepository is used, the table is automatically created, so there's no need to create a separate table under resources.
// What is JpaRepository? It extends PagingAndSortingRepository, which in turn extends CRUDRepository."
@Repository
public interface UserRepository extends JpaRepository<User, String> { // 这里的string是primary key
}

// By extending this interface, the UserRepository inherits various methods,
// including methods for
// saving, deleting, updating, and querying User objects. It also provides
// pagination and sorting capabilities.