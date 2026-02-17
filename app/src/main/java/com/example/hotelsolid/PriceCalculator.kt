package com.example.hotelsolid

import java.time.LocalDate

interface PriceCalculator {
    fun calculate(room: Room, nights:Long): Double
}