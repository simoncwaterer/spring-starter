package com.sourcegraph.ce.springstarter.business.test;

import com.sourcegraph.ce.springstarter.business.ReservationService;
import com.sourcegraph.ce.springstarter.business.RoomReservation;
import com.sourcegraph.ce.springstarter.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Room room;
    private Guest guest;
    private Reservation reservation;
    private Date testDate;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(1L);
        room.setName("Test Room");
        room.setRoomNumber("101");

        guest = new Guest();
        guest.setGuestId(1L);
        guest.setFirstName("John");
        guest.setLastName("Doe");

        testDate = new Date();
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setRoomId(1L);
        reservation.setGuestId(1L);
        reservation.setCheckInDate(new java.sql.Date(testDate.getTime()));
    }

    @Test
    void testGetRoomReservationsForDate() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(reservationRepository.findReservationByCheckInDate(any()))
                .thenReturn(Collections.singletonList(reservation));
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        List<RoomReservation> result = reservationService.getRoomReservationsForDate(testDate);

        assertEquals(1, result.size());
        RoomReservation roomReservation = result.get(0);
        assertEquals("Test Room", roomReservation.getRoomName());
        assertEquals("101", roomReservation.getRoomNumber());
        assertEquals("John", roomReservation.getFirstName());
        assertEquals("Doe", roomReservation.getLastName());
    }

    @Test
    void testGetGuestsWithReservationOnDate() {
        when(reservationRepository.findReservationByCheckInDate(any()))
                .thenReturn(Collections.singletonList(reservation));
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        List<Guest> result = reservationService.getGuestsWithReservationOnDate(testDate);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
    }

    @Test
    void testGetHotelGuests() {
        Guest guest2 = new Guest();
        guest2.setGuestId(2L);
        guest2.setFirstName("Jane");
        guest2.setLastName("Smith");

        when(guestRepository.findAll()).thenReturn(Arrays.asList(guest, guest2));

        List<Guest> result = reservationService.getHotelGuests();

        assertEquals(2, result.size());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Smith", result.get(1).getLastName());
    }

    @Test
    void testAddGuest() {
        reservationService.addGuest(guest);
        verify(guestRepository, times(1)).save(guest);
    }

    @Test
    void testGetRooms() {
        Room room2 = new Room();
        room2.setId(2L);
        room2.setName("Test Room 2");
        room2.setRoomNumber("102");

        when(roomRepository.findAll()).thenReturn(Arrays.asList(room, room2));

        List<Room> result = reservationService.getRooms();

        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getRoomNumber());
        assertEquals("102", result.get(1).getRoomNumber());
    }

    @Test
    void testGetRoomsByRoomNumber() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));

        Room result = reservationService.getRoomsByRoomNumber("101");

        assertNotNull(result);
        assertEquals("101", result.getRoomNumber());
    }

    @Test
    void testDeleteRoomByRoomNumber() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));

        reservationService.deleteRoomByRoomNumber("101");

        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    void testBookReservation() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Date checkOutDate = new Date(testDate.getTime() + 86400000); // Next day
        Reservation result = reservationService.bookReservation(1L, 1L, testDate, checkOutDate);

        assertNotNull(result);
        assertEquals(1L, result.getReservationId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}