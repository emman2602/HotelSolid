package com.example.hotelsolid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class BookingServiceTest {

    private lateinit var bookingService: BookingService
    private lateinit var reservationRepo: InMemoryReservationRepository
    private lateinit var availabilityService: RoomAvailableService
    private lateinit var priceCalculator: StandardPriceCalculator
    private lateinit var roomRepository: RoomRepository

    @BeforeEach
    fun setup() {
        reservationRepo = InMemoryReservationRepository()
        availabilityService = RoomAvailableService(reservationRepo)
        priceCalculator = StandardPriceCalculator()

        // Mocking the repository to provide the necessary rooms
        roomRepository = object : RoomRepository {
            override fun findAvailableRooms(checkIn: LocalDate, checkOut: LocalDate): List<Room> = emptyList()

            override fun getAllRooms(): List<Room> {
                return listOf(
                    Room(101, RoomType.BASIC, 50.0),
                    Room(102, RoomType.DOUBLE, 100.0),
                    Room(103, RoomType.SUITE, 200.0)
                )
            }
        }

        bookingService = BookingService(
            roomRepository,
            reservationRepo,
            priceCalculator,
            availabilityService
        )
    }

    @Test
    fun `should create reservation when room is available`() {
        // Arrange
        val room = Room(101, RoomType.BASIC, 50.0)
        val guest = Guest("John Doe", "12345678", emptyList())
        val checkIn = LocalDate.of(2026, 8, 1)
        val checkOut = LocalDate.of(2026, 8, 5)

        // Act
        val reservation = bookingService.createReservation(guest, room, checkIn, checkOut)

        // Assert
        assertNotNull(reservation, "Reservation should not be null")
        assertEquals(101, reservation.room.numRoom, "Room number should match")
        assertNotNull(reservationRepo.findId(reservation.id), "Reservation should be saved in repository")
    }

    @Test
    fun `should throw exception when room is already booked for overlapping dates`() {
        // Arrange
        val room = Room(101, RoomType.BASIC, 50.0)
        val guest1 = Guest("John Doe", "12345678", emptyList())
        val checkIn1 = LocalDate.of(2026, 8, 1)
        val checkOut1 = LocalDate.of(2026, 8, 5)

        // Create the first reservation
        bookingService.createReservation(guest1, room, checkIn1, checkOut1)

        val guest2 = Guest("Jane Smith", "87654321", emptyList())
        val checkIn2 = LocalDate.of(2026, 8, 4) // This date overlaps with the first reservation
        val checkOut2 = LocalDate.of(2026, 8, 10)

        // Act & Assert
        val exception = assertThrows<IllegalStateException> {
            bookingService.createReservation(guest2, room, checkIn2, checkOut2)
        }

        assertEquals("Room is not available for these dates", exception.message)
    }

    @Test
    fun `should calculate total cost based on nights and room type`() {

        val room = Room(103, RoomType.SUITE, 200.0) // 200.0 per night
        val guest = Guest("Alice", "11223344", emptyList())
        val checkIn = LocalDate.of(2026, 10, 1)
        val checkOut = LocalDate.of(2026, 10, 5) // 4 nights total


        val reservation = bookingService.createReservation(guest, room, checkIn, checkOut)


        // Expected total: 4 nights * 200.0 = 800.0
        assertEquals(800.0, reservation.total, "The total cost should be 800.0")
    }

    @Test
    fun `should cancel reservation and free the room`() {

        val room = Room(102, RoomType.DOUBLE, 100.0)
        val guest = Guest("Bob", "44332211", emptyList())
        val checkIn = LocalDate.of(2026, 12, 1)
        val checkOut = LocalDate.of(2026, 12, 10)

        val reservation = bookingService.createReservation(guest, room, checkIn, checkOut)


        assertNotNull(reservationRepo.findId(reservation.id), "Reservation should exist initially")


        bookingService.cancelReservation(reservation.id)


        assertNull(reservationRepo.findId(reservation.id), "Reservation should be removed after cancellation")
        assertTrue(availabilityService.isAvailable(room.numRoom, checkIn, checkOut), "Room should be available again")
    }
}