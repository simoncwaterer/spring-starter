package com.sourcegraph.ce.springstarter.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sourcegraph.ce.springstarter.data.Guest;
import com.sourcegraph.ce.springstarter.business.ReservationService;

@Controller
@RequestMapping("/guests")
public class GuestsController {

    private final ReservationService reservationService;

    public GuestsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String getGuests(Model model) {
        List<Guest> guests = reservationService.getHotelGuests();
        model.addAttribute("guests", guests);
        return "hotel-guests";
    }
}

