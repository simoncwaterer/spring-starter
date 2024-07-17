package com.sourcegraph.ce.springstarter.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Guest findGuestByFirstNameAndLastName(String firstName, String lastName);
}