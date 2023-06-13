package com.simonw.sg.springstarter.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.simonw.sg.springstarter.business.ReservationService;
import com.simonw.sg.springstarter.data.Guest;

@Controller
@RequestMapping("/guests")
public class GuestsController {

    private final ReservationService reservationService;

    public GuestsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getGuests(Model model) {
        List<Guest> guests = reservationService.getHotelGuests();
        model.addAttribute("guests", guests);
        return "hotel-guests";
    }
}

