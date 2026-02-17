package com.example.hotelsolid

import java.time.LocalDate


data class Reservation(
    val id: String,
    val room: Room,
    val guest: Guest,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val total: Double
)