package com.bslota.refactoring.library

import com.bslota.refactoring.library.NotificationSender.Email
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class BookService(
    private val bookDAO: BookDAO,
    private val customerDAO: CustomerDAO,
    private val emailService: NotificationSender
) {
    fun placeOnHold(bookId: Int, customerId: Int, days: Int): Boolean {
        val book = bookDAO.getBookFromDatabase(bookId)
        val customer = customerDAO.getCustomerFromDatabase(customerId)

        var isReserved = false
        if (book != null && customer != null) {
            if (customer.holds.size < 5) {
                val reservationDate = book.reservationDate
                if (reservationDate == null) {
                    customer.holds.add(bookId)
                    book.reservationDate = Instant.now()
                    book.setReservationEndDate(Instant.now().plus(days.toLong(), ChronoUnit.DAYS))
                    book.setPatronId(customerId)
                    bookDAO.update(book)
                    customerDAO.update(customer)
                    isReserved = true
                }
            }
        }
        if (isReserved) {
            addLoyaltyPoints(customer)
        }
        if (canHaveAFreeBook(isReserved, customer)) {
            val email = createEmail(customer.points, customer.email)
            emailService.sendMail(email)
        }
        return isReserved
    }

    private fun createEmail(points: Int, emailAddress: String?): Email {
        val (title, body) = createEmailTitleAndText(points)
        val email = Email(arrayOf(emailAddress), "contact@your-library.com", title, body)
        return email
    }


    private fun canHaveAFreeBook(
        isReserved: Boolean,
        customer: Customer
    ) = isReserved && customer.isQualifiesForFreeBook

    private fun createEmailTitleAndText(points: Int): Pair<String, String> {
        val title = "[REWARD] Free book waiting for you!"
        val body = """
                    Dear Sir/Madame, 
                    we are pleased to inform you, that the number of loyalty points you have gathered is $points. 
                    It means we have a reward for you! A free book is waiting at your local library branch!
                    """.trimIndent()
        return Pair(title, body)
    }

    private fun addLoyaltyPoints(customer: Customer) {
        when (customer.type) {
            0 -> customer.points = customer.points + 1
            1 -> customer.points = customer.points + 5
            2 -> {
                val newPoints: Int = if (customer.points == 0) {
                    100
                } else {
                    customer.points * 2
                }
                customer.points = newPoints
            }
            else -> { }
        }
        if (customer.points > 10000) {
            customer.isQualifiesForFreeBook = true
        }
        customerDAO.update(customer)
    }
}
