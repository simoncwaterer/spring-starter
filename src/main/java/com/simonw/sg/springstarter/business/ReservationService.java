package com.simonw.sg.springstarter.business;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simonw.sg.springstarter.data.Guest;
import com.simonw.sg.springstarter.data.GuestRepository;
import com.simonw.sg.springstarter.data.Reservation;
import com.simonw.sg.springstarter.data.ReservationRepository;
import com.simonw.sg.springstarter.data.Room;
import com.simonw.sg.springstarter.data.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        Map<Long, RoomReservation> roomReservationMap = new HashMap();
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

    public List<Guest> getHotelGuests() {
        final Iterable<Guest> guests = this.guestRepository.findAll();
        List<Guest> guestList = new ArrayList<Guest>();
        guests.forEach(guestList::add);
        guestList.sort((g1, g2) -> {
            int lastNameComparison = g1.getLastName().compareTo(g2.getLastName());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            }
            return g1.getFirstName().compareTo(g2.getFirstName());
        });

        return guestList;
    }

    public void addGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null");
        }
        this.guestRepository.save(guest);
    }


    public List<Room> getRooms() {
        final Iterable<Room> rooms = this.roomRepository.findAll();
        List<Room> roomList = new ArrayList<Room>();
        rooms.forEach(roomList::add);
        roomList.sort((r1, r2) ->
        r1.getRoomNumber().compareTo(r2.getRoomNumber()));
        return roomList;
    }

}

