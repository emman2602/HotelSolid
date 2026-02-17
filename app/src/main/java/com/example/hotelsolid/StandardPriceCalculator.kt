package com.example.hotelsolid

class StandardPriceCalculator: PriceCalculator {
    override fun calculate(room: Room, nights: Long): Double {
        return room.price*nights
    }
}