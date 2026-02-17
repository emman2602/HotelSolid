package com.example.hotelsolid

data class Guest(
    val name: String,
    val DNI: String,
    val reservationHistory: List<Reservation>
)
