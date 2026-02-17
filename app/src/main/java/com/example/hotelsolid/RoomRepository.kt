package com.example.hotelsolid

import java.time.LocalDate

interface RoomRepository {
    fun findAvailableRooms(checkIn: LocalDate, checkOut: LocalDate): List<Room>
    fun getAllRooms(): List<Room>
}