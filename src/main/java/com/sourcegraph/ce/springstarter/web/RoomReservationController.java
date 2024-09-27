package com.sourcegraph.ce.springstarter.web;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sourcegraph.ce.springstarter.business.ReservationService;
import com.sourcegraph.ce.springstarter.business.RoomReservation;
import com.sourcegraph.ce.springstarter.util.DateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/reservations")
public class RoomReservationController {

    private final DateUtils dateUtils;
    private final ReservationService reservationService;

    public RoomReservationController(DateUtils dateUtils, ReservationService reservationService) {
        this.dateUtils = dateUtils;
        this.reservationService = reservationService;
    }

    @GetMapping
    public String getReservations(@RequestParam(value = "date", required = false) String dateString, Model model) {

        Date date = this.dateUtils.createDateFromDateString(dateString);
        List<RoomReservation> roomReservations = reservationService.getRoomReservationsForDate(date);
        model.addAttribute("roomReservations", roomReservations);
        return "roomres";
    }

    @PostMapping("/book")
    public String bookReservation(@RequestParam("roomId") long roomId,
            @RequestParam("guestId") long guestId,
            @RequestParam("checkInDate") String checkInDateString,
            @RequestParam("checkOutDate") String checkOutDateString,
            Model model) {
        Date checkInDate = this.dateUtils.createDateFromDateString(checkInDateString);
        Date checkOutDate = this.dateUtils.createDateFromDateString(checkOutDateString);

        try {
            reservationService.bookReservation(roomId, guestId, checkInDate, checkOutDate);
            model.addAttribute("message", "Reservation booked successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to book reservation: " + e.getMessage());
        }

        // Redirect to the reservations page to show updated list
        return "redirect:/reservations?date=" + checkInDateString;
    }

    @GetMapping("/book")
    public String showBookReservationForm(Model model) {
        model.addAttribute("rooms", reservationService.getRooms());
        model.addAttribute("guests", reservationService.getHotelGuests());
        return "book-reservation";
    }

}
