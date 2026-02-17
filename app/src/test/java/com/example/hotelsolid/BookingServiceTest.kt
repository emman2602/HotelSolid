package com.example.hotelsolid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate



class BookingServiceTest {

    private lateinit var bookingService: BookingService
    private lateinit var reservationRepo: InMemoryReservationRepository
    private lateinit var availabilityService: RoomAvailableService
    private lateinit var priceCalculator: StandardPriceCalculator
    private lateinit var roomRepository: RoomRepository

    @BeforeEach
    fun setup() {
        // Inicializamos implementaciones reales en memoria (Fakes)
        reservationRepo = InMemoryReservationRepository()
        availabilityService = RoomAvailableService(reservationRepo)
        priceCalculator = StandardPriceCalculator()

        bookingService = BookingService(
            roomRepository,
            reservationRepo,
            priceCalculator,
            availabilityService
        )
    }

    @Test
    fun `should create reservation when room is available`() {
        // Arrange (Preparar)
        val room = Room(101, RoomType.BASIC, 100.0)
        val guest = Guest("Dulce""9192929", )
        val checkIn = LocalDate.of(2026, 6, 1)
        val checkOut = LocalDate.of(2026, 6, 5)

        // Act (Actuar)
        val result = bookingService.createReservation(guest, room, checkIn, checkOut)

        // Assert (Verificar)
        assertNotNull(result.id)
        assertEquals(101, result.room.numRoom)
        assertEquals(1, reservationRepo.count())
    }

    @Test
    fun `should throw exception when room is already booked for overlapping dates`() {
        // Arrange
        val room = Room(101, RoomType.BASIC, 100.0)
        val guest = Guest("Emmanuel", "142155")

        // Primera reserva: del 1 al 5 de junio
        bookingService.createReservation(guest, room, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5))

        // Act & Assert
        // Intentar reservar del 4 al 6 de junio (traslape el día 4)
        assertThrows(IllegalStateException::class.java) {
            bookingService.createReservation(guest, room, LocalDate.of(2026, 6, 4), LocalDate.of(2026, 6, 6))
        }
    }

    @Test
    fun `should throw exception when room physical availability is false`() {
        // Arrange
        val roomUnderMaintenance = Room(202, RoomType.SUITE, 200.0)
        val guest = Guest("999", "Maintenance Guy")

        // Act & Assert
        assertThrows(IllegalStateException::class.java) {
            bookingService.createReservation(guest, roomUnderMaintenance, LocalDate.now(), LocalDate.now().plusDays(1))
        }
    }
}