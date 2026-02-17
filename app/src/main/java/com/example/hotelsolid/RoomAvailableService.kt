package com.example.hotelsolid

import java.time.LocalDate

class RoomAvailableService(
    private val reservationRepository: ReservationRepository
) {

    fun isAvailable(numRoom:Int, checkIn: LocalDate, checkOut: LocalDate): Boolean{
         val roomReservations = reservationRepository.findByRoom(numRoom)
         return roomReservations.none { res ->
             checkIn < res.checkOut && checkOut > res.checkIn
         }
    }
}