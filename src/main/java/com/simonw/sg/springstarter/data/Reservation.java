package com.simonw.sg.springstarter.data;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="RESERVATION")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="RESERVATION_ID")
    private long reservationId;
    @Column(name="ROOM_ID")
    private long roomId;    
    @Column(name="GUEST_ID")
    private long guestId;
    @Column(name="RES_DATE")
    private Date reservationDate;
    
    public long getReservationId() {
        return reservationId;
    }
    public long getRoomId() {
        return roomId;
    }
    public long getGuestId() {
        return guestId;
    }
    public Date getReservationDate() {
        return reservationDate;
    }
    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }
    public void setGuestId(long guestId) {
        this.guestId = guestId;
    }
    public void setReservationDate(Date date) {
        this.reservationDate = date;
    }

    @Override
    public String toString() {
        return "Reservation [reservationId=" + reservationId + ", roomId=" + roomId + ", guestId=" + guestId + ", date="
                + reservationDate + "]";
    }
    
    
    
}
