package com.rmarioo.imperativeshell_functionalcore.library

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional


sealed class BookOnHoldResult

class BookOnHoldApproved() :   BookOnHoldResult() {
        var emailToNotify: Optional<NotificationSender.Email> = Optional.empty()
        lateinit var customerToUpdate: Customer
        lateinit var bookToUpdate: Book
    }
object BookOnHoldRejected :      BookOnHoldResult()



data class PlaceOnHoldRequest(
    val book: Book? = Book(),
    val customer: Customer = Customer(),
    val days: Int = 1
)

fun placeOnHoldCore(placeOnHoldRequest: PlaceOnHoldRequest): BookOnHoldResult {

    val (book, customer, days) = placeOnHoldRequest
    var isReserved = false

    var optionalBookOnHoldApproved : Optional<BookOnHoldApproved> = Optional.empty();
    if (book != null && customer != null) {
        if (customer.holds.size < 5) {
            val reservationDate = book.reservationDate
            if (reservationDate == null) {

                customer.holds.add(book.bookId)
                book.reservationDate = Instant.now()
                book.reservationEndDate = Instant.now().plus(days.toLong(), ChronoUnit.DAYS)
                book.patronId = customer.patronId
                val resultWithEffects: BookOnHoldApproved = BookOnHoldApproved()
                resultWithEffects.apply { bookToUpdate = book }
                                 .apply { customerToUpdate = customer }
                optionalBookOnHoldApproved = Optional.of(resultWithEffects)
                isReserved = true
                addLoyaltyPoints(customer)
            }
        }
    }

    if (canHaveAFreeBook(isReserved, customer)) {
        val email = createEmail(customer.points, customer.email)
        optionalBookOnHoldApproved.map { r -> r.apply { emailToNotify = Optional.of(email) } }

    }

    return if (optionalBookOnHoldApproved.isPresent) optionalBookOnHoldApproved.get() else BookOnHoldRejected
}

private fun createEmail(points: Int, emailAddress: String?): NotificationSender.Email {
    val (title, body) = createEmailTitleAndText(points)
    return NotificationSender.Email(
        arrayOf(emailAddress),
        "contact@your-library.com",
        title,
        body
    )
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
        else -> {
        }
    }
    if (customer.points > 10000) {
        customer.isQualifiesForFreeBook = true
    }
}

