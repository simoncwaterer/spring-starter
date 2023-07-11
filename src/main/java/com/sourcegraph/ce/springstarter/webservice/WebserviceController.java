package com.sourcegraph.ce.springstarter.webservice;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.data.Room;
import com.sourcegraph.ce.springstarter.business.ReservationService;
import com.sourcegraph.ce.springstarter.business.RoomReservation;
import com.sourcegraph.ce.springstarter.util.DateUtils;

@RestController
@RequestMapping("/api")
public class WebserviceController {

    private final DateUtils dateUtils;
    private final ReservationService reservationService;

    public WebserviceController(DateUtils dateUtils, ReservationService reservationService) {
        this.dateUtils = dateUtils;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<RoomReservation> getReservations(@RequestParam(value = "date", required = false) String dateString) {
        Date date = this.dateUtils.createDateFromDateString(dateString);
        return reservationService.getRoomReservationsForDate(date);
    }

    @GetMapping("/guests") 
    public List<Guest> getGuests() 
    { 
        return this.reservationService.getHotelGuests();
    }

    @PostMapping("/guests")
    @ResponseStatus(HttpStatus.CREATED) 
    public void addGuest(@RequestBody Guest guest) 
    { 
        this.reservationService.addGuest(guest); 
    }



    @GetMapping("/rooms")
    List<Room> getRooms() {
        return this.reservationService.getRooms();
    }


} 

