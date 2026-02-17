package com.example.hotelsolid

import java.time.LocalDate

interface ReservationRepository {
    fun save(reservation: Reservation)
    fun delete(reservationId: String)
    fun findId(reservationId: String): Reservation?

    fun findByRoom(numRoom: Int): List<Reservation>


}