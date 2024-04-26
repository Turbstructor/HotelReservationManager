package spartacodingclub.nbcamp.kotlinspring.assignment.core

import spartacodingclub.nbcamp.kotlinspring.assignment.core.data.Customer
import spartacodingclub.nbcamp.kotlinspring.assignment.core.data.Reservation
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class InteractiveManager {
    var menu: Int = 0
        private set

    private var customers: ArrayList<Customer> = arrayListOf()
    private var reservations: ArrayList<Reservation> = arrayListOf()

    fun showMenu() {
        println("---------- [Reservation Management Menu] ----------")
        println("1. Reserve room")
        println("2. List reservations")
        println("3. List reservations (sorted by check-in date)")
        println("4. Exit")
        println("5. Print expense log of customer")
        println("6. Revise/Cancel reservation")
        println("---------------------------------------------------")
        println()
    }

    fun selectMenu() {
        while (true) {
            try {
                print("Select menu to perform: ")
                menu = readln().toInt()

                if(menu !in 1..6) throw NumberFormatException()

                break
            } catch (e: NumberFormatException) {
                println("Invalid input given as menu. Please try again with numbers in range [1, 6].")
            }
        }
    }

    fun perform() {
        when(menu) {
            1 -> addReservation()
            2 -> listReservations()
            3 -> listReservationsSorted()
            // 4 skipped since this is handled at main
            5 -> queryCustomer()
            6 -> modifyReservation()
        }
    }

    private fun makeReservationInstance(name: String, excludingReservationIndex: Int): Reservation {
        var roomNumber: Int
        var dateCheckingIn: LocalDate
        var dateCheckingOut: LocalDate

        val dateToday: LocalDate = LocalDate.now()

        while (true) {
            try {
                print("Enter room number: ")
                roomNumber = readln().toInt()

                if(roomNumber !in 100..999) throw IllegalArgumentException()

                break
            } catch (e: NumberFormatException) {
                println("Invalid input given: Please try again with numbers in range [100, 999].")
            } catch (e: IllegalArgumentException) {
                println("Invalid room number given: Please try again with numbers in range [100, 999].")
            }
        }

        while (true) {
            try {
                print("Enter check-in date with format \'YYYYMMDD\': ")
                dateCheckingIn = LocalDate.parse(readln(), Reservation.dateFormat)

                if (dateCheckingIn.isBefore(dateToday)) throw IllegalArgumentException("Invalid date given: check-in date should be equal to or later than today (${dateToday.format(Reservation.dateFormat)}")

                var isRoomOccupied = false
                for ((index, reservation) in reservations.withIndex()) {
                    if(index == excludingReservationIndex) continue

                    if (reservation.roomNumber == roomNumber) isRoomOccupied = (!dateCheckingIn.isBefore(reservation.dateCheckingIn) && !dateCheckingIn.isAfter(reservation.dateCheckingOut))
                    if (isRoomOccupied) break
                }

                if (isRoomOccupied) throw IllegalArgumentException("Invalid date given: the room is already occupied by other customer that day")

                break
            } catch (e: DateTimeParseException) {
                println("Invalid input given: Please try again with format \'YYYYMMDD\'.")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }

        while (true) {
            try {
                print("Enter check-out date with format \'YYYYMMDD\': ")
                dateCheckingOut = LocalDate.parse(readln(), Reservation.dateFormat)

                if (!dateCheckingOut.isAfter(dateCheckingIn)) throw IllegalArgumentException("Invalid date given: check-out date should be later than check-in date (${dateCheckingIn.format(Reservation.dateFormat)}")

                var isRoomOccupied = false
                for ((index, reservation) in reservations.withIndex()) {
                    if(index == excludingReservationIndex) continue

                    if (reservation.roomNumber == roomNumber) {
                        isRoomOccupied = (!dateCheckingOut.isBefore(reservation.dateCheckingIn) && !dateCheckingOut.isAfter(reservation.dateCheckingOut))
                        isRoomOccupied = isRoomOccupied || (dateCheckingIn.isBefore(reservation.dateCheckingIn) && dateCheckingOut.isAfter(reservation.dateCheckingOut))

                        if(isRoomOccupied) break
                    }
                }

                if (isRoomOccupied) throw IllegalArgumentException("Invalid date given: the room is already occupied by other customer that day")

                break
            } catch (e: DateTimeParseException) {
                println("Invalid date given: Please try again with format \'YYYYMMDD\'.")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }

        return Reservation(name, roomNumber, dateCheckingIn, dateCheckingOut, Random.nextInt(30000, 150000))
    }

    private fun addReservation() {
        print("Enter name of the customer who wants to modify reservation: ")
        val name = readln()

        val newReservation = makeReservationInstance(name, -1)

        var customer = customers.firstOrNull { it.name == name }
        when (customer) {
            null -> { customer = Customer(name); customers.add(customer) }
            else -> {
                customer.addUsage(newReservation.reservationCost, true, "for reserving room")
            }
        }

        reservations.add(newReservation)
        println("Reservation successfully added.")
    }

    private fun listReservations() {
        println("Querying reservations...")
        for (eachReservation in reservations) println(eachReservation)
    }

    private fun listReservationsSorted() {
        println("Querying reservations...")
        for (eachReservation in reservations.sortedWith(compareBy { it.dateCheckingIn })) println(eachReservation)
    }

    private fun queryCustomer() {
        print("Enter name of the customer to look for: ")
        val name: String = readln()

        val customer = customers.firstOrNull { it.name == name }
        when (customer) {
            null -> println("Cannot query customer with name ${name}.")
            else -> customer.printExpenseLog()
        }
    }


    private fun modifyReservation() {
        print("Enter name of the customer: ")
        val name = readln()

        val dateToday: LocalDate = LocalDate.now()
        val reservationByCustomer = reservations.toList().filter { it.name == name && !it.dateCheckingIn.isBefore(dateToday) }

        if (reservationByCustomer.isEmpty()){
            println("Cannot query reservations with given customer name: check if the customer made future reservations.")
            return
        }

        var selection = 0
        while (selection !in 1..reservationByCustomer.size) {
            try {
                println("Reservations made by ${name}:")
                var reservationsMade = 1
                for (eachReservation in reservationByCustomer) println("${reservationsMade++}. ${eachReservation.toSimplerString()}")

                println()
                print("Enter number of reservation to modify: ")
                selection = readln().toInt()

                if(selection !in 1..reservationByCustomer.size) throw IllegalArgumentException("Invalid reservation number given: Please try again with numbers in range [1, ${reservationByCustomer.size}].")
            } catch(e: NumberFormatException) {
                println("Invalid input given: Please try again with numbers in range [1, ${reservationByCustomer.size}].")
            } catch(e: IllegalArgumentException) {
                println(e.message)
            }
        }

        var targetReservationIndex = 0
        var selectionIndex = 0

        for ((index, reservation) in reservations.withIndex()) {
            if(reservation.name == name) {
                selectionIndex ++
                if(selectionIndex == selection){ targetReservationIndex = index; break }
            }
        }

        val revisionMenu: Int
        try {
            println("What do you want to do? (Revise: 1), (Cancel: 2), (Return to menu: any other input): ")
            revisionMenu = readln().toInt()
        } catch(e: NumberFormatException) {
            println("Returning to main menu...")
            return
        }

        when (revisionMenu) {
            1 -> {
                val newReservation = makeReservationInstance(name, targetReservationIndex)

                reservations[targetReservationIndex].let { target ->
                    newReservation.let { new ->
                        target.roomNumber = new.roomNumber
                        target.dateCheckingIn = new.dateCheckingIn
                        target.dateCheckingOut = new.dateCheckingOut
                    }
                }

                println("Reservation successfully updated.")
            }
            2 -> {
                val targetIndexFromCustomers = customers.indexOfFirst { it.name == name }

                println("Note: Cancelling reservation")
                println("-   No refund within  3 days before check-in")
                println("-  30% refund within  5 days before check-in")
                println("-  50% refund within  7 days before check-in")
                println("-  80% refund within 14 days before check-in")
                println("- 100% refund within 30 days before check-in")
                // ... what if we cancel reservation within more than 30 days? Shouldn't it be the same?

                val dayDifference = ChronoUnit.DAYS.between(dateToday, reservations[targetReservationIndex].dateCheckingIn)
                val refundRate: Double = when {
                    dayDifference <= 3 -> 0.0
                    dayDifference <= 5 -> 0.3
                    dayDifference <= 7 -> 0.5
                    dayDifference <= 14 -> 0.8
                    dayDifference <= 30 -> 1.0
                    else -> 1.0
                }

                val refundCost: Int = (reservations[targetReservationIndex].reservationCost * refundRate).toInt()
                reservations.removeAt(targetReservationIndex)
                if(refundCost != 0) customers[targetIndexFromCustomers].addUsage(refundCost, false, "for cancelling reservation")

                println("Reservation successfully cancelled.")
            }
            else -> println("Returning to main menu...")
        }
    }
}