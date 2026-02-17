package com.example.hotelsolid

class InMemoryReservationRepository: ReservationRepository {
    private val reservations = mutableListOf<Reservation>()
    override fun save(reservation: Reservation) {
        reservations.add(reservation)
    }

    override fun delete(reservationId: String) {
        reservations.removeIf { it.id == reservationId }
    }

    override fun findId(reservationId: String): Reservation? =
        reservations.find { it.id == reservationId }

    override fun findByRoom(numRoom: Int): List<Reservation> {
        val reservations = reservations.filter { it.room.numRoom == numRoom }
        return reservations
    }

}