package com.sourcegraph.ce.springstarter.business.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sourcegraph.ce.springstarter.business.ReservationService;
import com.sourcegraph.ce.springstarter.data.RoomRepository;
import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.data.GuestRepository;
import com.sourcegraph.ce.springstarter.data.Reservation;
import com.sourcegraph.ce.springstarter.data.ReservationRepository;
import com.sourcegraph.ce.springstarter.data.Room;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceTest.class);

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void testGetRoomsByRoomNumber_validRoom() {
        Room room = new Room();
        room.setRoomNumber("101");

        when(roomRepository.findAll()).thenReturn(List.of(room));

        Room result = reservationService.getRoomsByRoomNumber("101");

        assertEquals("101", result.getRoomNumber());
    }

    @Test
    public void testGetRoomsByRoomNumber_invalidRoom() {
        Room room = new Room();
        room.setRoomNumber("101");

        when(roomRepository.findAll()).thenReturn(List.of(room));

        Room result = reservationService.getRoomsByRoomNumber("102");

        assertNull(result);
    }

    @Test
    public void testGetRoomsByRoomNumber_nullRoom() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(null);
        when(roomRepository.findAll()).thenReturn(rooms);
        Room result = reservationService.getRoomsByRoomNumber("101");

        assertNull(result);
    }

    @Test
    public void testGetGuestsWithReservationOnDate_validDate() {
        Date date = new Date();
        Guest guest1 = new Guest();
        guest1.setFirstName("John");
        guest1.setLastName("Doe");
        Guest guest2 = new Guest();
        guest2.setFirstName("Jane");
        guest2.setLastName("Smith");

        Reservation reservation1 = new Reservation();
        reservation1.setReservationDate(new java.sql.Date(date.getTime()));
        reservation1.setGuestId(1L);
        Reservation reservation2 = new Reservation();
        reservation2.setReservationDate(new java.sql.Date(date.getTime()));
        reservation2.setGuestId(2L);

        when(reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime())))
                .thenReturn(List.of(reservation1, reservation2));
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest1));
        when(guestRepository.findById(2L)).thenReturn(Optional.of(guest2));

        List<Guest> result = reservationService.getGuestsWithReservationOnDate(date);

        assertEquals(2, result.size());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Smith", result.get(1).getLastName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    public void testGetGuestsWithReservationOnDate_noReservations() {
        Date date = new Date();

        when(reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime())))
                .thenReturn(Collections.emptyList());

        List<Guest> result = reservationService.getGuestsWithReservationOnDate(date);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGuestsWithReservationOnDate_guestNotFound() {
        Date date = new Date();

        Reservation reservation = new Reservation();
        reservation.setReservationDate(new java.sql.Date(date.getTime()));
        reservation.setGuestId(1L);

        when(reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime())))
                .thenReturn(List.of(reservation));
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        List<Guest> result = reservationService.getGuestsWithReservationOnDate(date);

        assertTrue(result.isEmpty());
    }
}
