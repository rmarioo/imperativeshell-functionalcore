package com.bslota.refactoring.library

import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class BookService(
    private val bookDAO: BookDAO,
    private val patronDAO: PatronDAO,
    private val emailService: NotificationSender
) {
    fun placeOnHold(bookId: Int, patronId: Int, days: Int): Boolean {
        val book = bookDAO.getBookFromDatabase(bookId)
        val patron = patronDAO.getPatronFromDatabase(patronId)
        var flag = false
        if (book != null && patron != null) {
            if (patron.holds.size < 5) {
                val reservationDate = book.reservationDate
                if (reservationDate == null) {
                    patron.holds.add(bookId)
                    book.reservationDate = Instant.now()
                    book.setReservationEndDate(Instant.now().plus(days.toLong(), ChronoUnit.DAYS))
                    book.setPatronId(patronId)
                    bookDAO.update(book)
                    patronDAO.update(patron)
                    flag = true
                }
            }
        }
        if (flag) {
            addLoyaltyPoints(patron)
        }
        if (flag && patron.isQualifiesForFreeBook) {
            val title = "[REWARD] Free book waiting for you!"
            val body = """
                Dear Sir/Madame, 
                we are pleased to inform you, that the number of loyalty points you have gathered is ${patron.points}. 
                It means we have a reward for you! A free book is waiting at your local library branch!
                """.trimIndent()
            val email = patron.email
            emailService.sendMail(arrayOf(email), "contact@your-library.com", title, body)
        }
        return flag
    }

    private fun addLoyaltyPoints(patron: Patron) {
        when (patron.type) {
            0 -> patron.points = patron.points + 1
            1 -> patron.points = patron.points + 5
            2 -> {
                val newPoints: Int = if (patron.points == 0) {
                    100
                } else {
                    patron.points * 2
                }
                patron.points = newPoints
            }
            else -> { }
        }
        if (patron.points > 10000) {
            patron.isQualifiesForFreeBook = true
        }
        patronDAO.update(patron)
    }
}
