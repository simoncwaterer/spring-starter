package com.sourcegraph.ce.springstarter.data;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Iterable<Reservation> findReservationByCheckInDate(Date date);

    Reservation findReservationByGuestIdAndCheckInDate(long guestId, Date checkInDate);

    Reservation findReservationByRoomIdAndCheckInDate(long roomId, Date checkInDate);
}