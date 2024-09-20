package com.sourcegraph.ce.springstarter.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.data.GuestRepository;
import com.sourcegraph.ce.springstarter.data.Reservation;
import com.sourcegraph.ce.springstarter.data.ReservationRepository;
import com.sourcegraph.ce.springstarter.data.Room;
import com.sourcegraph.ce.springstarter.data.RoomRepository;
import org.springframework.lang.NonNull;

@Component
public class AppStartupEvent implements ApplicationListener<ApplicationReadyEvent> {
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public AppStartupEvent(RoomRepository roomRepository, GuestRepository guestRepository,
            ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Iterable<Room> rooms = this.roomRepository.findAll();
        rooms.forEach(System.out::println);
        Iterable<Guest> guests = this.guestRepository.findAll();
        guests.forEach(System.out::println);
        Iterable<Reservation> reservations = this.reservationRepository.findAll();
        reservations.forEach(System.out::println);
    }
}
