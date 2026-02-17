package com.example.hotelsolid

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class BookingService (
    private val roomRepository: RoomRepository,
    private val reservationRepository: ReservationRepository,
    private val priceCalculator: PriceCalculator,
    private val availableService: RoomAvailableService
){
    fun createReservation(guest: Guest, room: Room, checkIn: LocalDate, checkOut: LocalDate): Reservation{
        if(!availableService.isAvailable(room.numRoom,checkIn, checkOut)){
            throw IllegalStateException("Room is not available for these dates")
        }
        val room = roomRepository.getAllRooms().first {it.numRoom == room.numRoom}
        val nights = ChronoUnit.DAYS.between(checkIn, checkOut)
        val totalPrice = priceCalculator.calculate(room, nights)

        val reservation = Reservation(
            id = UUID.randomUUID().toString(),
            room = room,
            guest = guest,
            checkIn = checkIn,
            checkOut = checkOut,
            total = totalPrice
        )

        reservationRepository.save(reservation)
        println("Reservation created. Total cost: ${totalPrice}")
        return reservation
    }

    fun cancelReservation(reservationId: String){
        val reservation = reservationRepository.findId(reservationId)
            ?: throw IllegalArgumentException("Reservation not found")

        reservationRepository.delete(reservationId)
        println("Reservation $reservationId cancelled successfully")
    }
}