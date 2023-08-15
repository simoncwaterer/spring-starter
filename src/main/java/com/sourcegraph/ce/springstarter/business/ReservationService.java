package com.sourcegraph.ce.springstarter.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.data.GuestRepository;
import com.sourcegraph.ce.springstarter.data.Reservation;
import com.sourcegraph.ce.springstarter.data.ReservationRepository;
import com.sourcegraph.ce.springstarter.data.Room;
import com.sourcegraph.ce.springstarter.data.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
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
        Iterable<Reservation> reservations = this.reservationRepository.findReservationByReservationDate(new java.sql.Date(date.getTime()));
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

    // get a list of guests sorted by lastName and then by firstName
    public List<Guest> getHotelGuests() {
        List<Guest> guests = guestRepository.findAll();
        guests.sort(new Comparator<Guest>() {
            @Override
            public int compare(Guest g1, Guest g2) {
                int lastNameComparison = g1.getLastName().compareTo(g2.getLastName());
                if (lastNameComparison == 0) {
                    return g1.getFirstName().compareTo(g2.getFirstName());
                }
                return lastNameComparison;
            }
        });
        return guests;
    }

    public void addGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null");
        }
        this.guestRepository.save(guest);
    }

    // get Rooms and sort by Room Number
    public List<Room> getRooms() {
        final Iterable<Room> rooms = this.roomRepository.findAll();
        List<Room> roomList = new ArrayList<Room>();
        rooms.forEach(roomList::add);
        roomList.sort((r1, r2) ->
        r1.getRoomNumber().compareTo(r2.getRoomNumber()));
        return roomList;
    }

    // get a list of rooms sorted by bedinfo
    public List<Room> getRoomsByBedInfo() {
        final Iterable<Room> rooms = this.roomRepository.findAll();
        List<Room> roomList = new ArrayList<>();
        rooms.forEach(roomList::add);
        roomList.sort(new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return o1.getBedInfo().compareTo(o2.getBedInfo());
            }
        });
        return roomList;
    }
}
