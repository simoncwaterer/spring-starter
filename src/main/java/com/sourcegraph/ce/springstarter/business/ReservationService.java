package com.sourcegraph.ce.springstarter.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.data.GuestRepository;
import com.sourcegraph.ce.springstarter.data.Reservation;
import com.sourcegraph.ce.springstarter.data.ReservationRepository;
import com.sourcegraph.ce.springstarter.data.Room;
import com.sourcegraph.ce.springstarter.data.RoomRepository;
import org.springframework.stereotype.Service;

@Service
/**
 * Service class that provides business logic related to reservations, rooms and
 * guests.
 * Includes methods for:
 * - Getting room reservation details for a date
 * - Getting guests with reservations on a date
 * - Getting a sorted list of all guests
 * - Adding a new guest
 * - Getting a sorted list of all rooms
 * - Getting a sorted list of rooms by bed info
 * - Getting a room by room number
 */
public class ReservationService {

    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository,
            ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date) {
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getRoomNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });
        Iterable<Reservation> reservations = this.reservationRepository
                .findReservationByReservationDate(new java.sql.Date(date.getTime()));
        reservations.forEach(reservation -> {
            RoomReservation roomReservation = roomReservationMap.get(reservation.getRoomId());
            roomReservation.setDate(date);
            Guest guest = this.guestRepository.findById(reservation.getGuestId()).get();
            roomReservation.setFirstName(guest.getFirstName());
            roomReservation.setLastName(guest.getLastName());
            roomReservation.setGuestId(guest.getGuestId());
        });
        List<RoomReservation> roomReservations = new ArrayList<>();
        for (Long id : roomReservationMap.keySet()) {
            roomReservations.add(roomReservationMap.get(id));
        }
        roomReservations.sort(new Comparator<RoomReservation>() {
            @Override
            public int compare(RoomReservation o1, RoomReservation o2) {
                if (o1.getRoomName().equals(o2.getRoomName())) {
                    return o1.getRoomNumber().compareTo(o2.getRoomNumber());
                }
                return o1.getRoomName().compareTo(o2.getRoomName());
            }
        });
        return roomReservations;
    }

    // get a list of guests that have reservations on the given date
    public List<Guest> getGuestsWithReservationOnDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        List<Guest> guests = new ArrayList<>();
        Iterable<Reservation> reservations = this.reservationRepository
                .findReservationByReservationDate(new java.sql.Date(date.getTime()));

        if (!reservations.iterator().hasNext()) {
            // No reservations found for the given date
            return guests;
        }

        reservations.forEach(reservation -> {
            Optional<Guest> guestOptional = this.guestRepository.findById(reservation.getGuestId());
            if (guestOptional.isPresent()) {
                Guest guest = guestOptional.get();
                if (!guests.contains(guest)) {
                    guests.add(guest);
                }
            } else {
                // Handle case where guest is not found for the reservation
                // Log an error or take appropriate action based on your application's
                // requirements
                System.out.println("Guest not found for reservation: " + reservation.getReservationId());
            }
        });

        guests.sort(Comparator.comparing(Guest::getLastName).thenComparing(Guest::getFirstName));
        return guests;
    }

    /**
     * Returns a sorted list of all guests from the guest repository.
     * The list is sorted by last name and then first name.
     */
    // get a list of guests sorted by lastName and then by firstName
    public List<Guest> getHotelGuests() {
        final Iterable<Guest> guests = this.guestRepository.findAll();
        List<Guest> guestList = new ArrayList<>();
        guests.forEach(guestList::add);
        guestList.sort(Comparator.comparing(Guest::getLastName).thenComparing(Guest::getFirstName));
        return guestList;
    }

    public void addGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null");
        }
        this.guestRepository.save(guest);
    }

    /**
     * Returns a sorted list of all rooms from the room repository.
     * The list is sorted by room number.
     */
    // get Rooms and sort by Room Number
    public List<Room> getRooms() {
        final Iterable<Room> rooms = this.roomRepository.findAll();
        List<Room> roomList = new ArrayList<Room>();
        rooms.forEach(roomList::add);
        roomList.sort(Comparator.comparing(Room::getRoomNumber));
        return roomList;
    }

    public List<Room> getRoomsByBedInfo() {
        return StreamSupport.stream(this.roomRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Room::getBedInfo))
                .collect(Collectors.toList());
    }

    /**
     * Returns the Room object with the given roomNumber,
     * or null if no Room with that roomNumber exists.
     * 
     * Gets all Rooms from the roomRepository.
     * Filters to find the Room with the given roomNumber.
     * Returns the first Room, or null if none found.
     */
    public Room getRoomsByRoomNumber(String roomNumber) {
        final Iterable<Room> rooms = this.roomRepository.findAll();
        Stream<Room> roomStream = StreamSupport.stream(rooms.spliterator(), false);

        Room room = roomStream
                .filter(r -> r != null) // filter null rooms
                .filter(r -> r.getRoomNumber() == roomNumber) // filter by room number
                .findFirst()
                .orElse(null);

        return room; // return room or null
    }

    // delete room by room number
    public void deleteRoomByRoomNumber(String roomNumber) {
        Room room = this.getRoomsByRoomNumber(roomNumber);
        this.roomRepository.delete(room);
    }

    // function to delete a reservation based on guest name and date
    public void deleteReservationByGuestNameAndDate(String guestFirstName, String guestLastName, Date date) {
        Guest guest = this.guestRepository.findGuestByFirstNameAndLastName(guestFirstName, guestLastName);

        if (guest == null) {
            System.out.println("Error: Guest not found with name " + guestFirstName + " " + guestLastName);
            return;
        }

        Reservation reservation = this.reservationRepository
                .findReservationByGuestIdAndReservationDate(guest.getGuestId(), new java.sql.Date(date.getTime()));

        if (reservation == null) {
            System.out.println("Error: Reservation not found for guest " + guestFirstName + " " + guestLastName
                    + " on date " + date);
            return;
        }

        System.out
                .println("Deleting reservation for guest " + guestFirstName + " " + guestLastName + " on date " + date);

        this.reservationRepository.delete(reservation);
    }

}
