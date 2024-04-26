package spartacodingclub.nbcamp.kotlinspring.assignment.core.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Reservation(
    val name: String,
    var roomNumber: Int,
    var dateCheckingIn: LocalDate, var dateCheckingOut: LocalDate,
    val reservationCost: Int
) {

    companion object {
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val dateFormatRich: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMdd")
    }


    override fun toString(): String {
        return String.format("Name: %8s, Room Number: %d, Check-in: %s, Check-out: %s", name, roomNumber, dateCheckingIn.format(
            dateFormat
        ), dateCheckingOut.format(dateFormat))
    }

    fun toSimplerString(): String {
        return String.format("Room Number: %d, Check-in: %s, Check-out: %s", roomNumber, dateCheckingIn.format(
            dateFormatRich
        ), dateCheckingOut.format(dateFormatRich))
    }
}