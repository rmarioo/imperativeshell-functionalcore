package com.rmarioo.imperativeshell_functionalcore.library

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional


sealed class BookOnHoldResult

data class BookOnHoldApproved(val bookToUpdate: Book, val customerToUpdate: Customer) : BookOnHoldResult() {
    var emailToNotify: Optional<NotificationSender.Email> = Optional.empty()
}
object BookOnHoldRejected : BookOnHoldResult()


data class PlaceOnHoldRequest(
    val book: Book? = Book(),
    val customer: Customer = Customer(),
    val days: Int = 1,
    val now: Instant = Instant.now()
)

fun placeOnHoldCore(placeOnHoldRequestInput: PlaceOnHoldRequest): BookOnHoldResult {

    val placeOnHoldRequest = placeOnHoldRequestInput.deepCopy()
    val (book, customer, days,now) = placeOnHoldRequest

    var result:BookOnHoldResult = BookOnHoldRejected
    if (book != null && customer != null) {
        if (customer.holds.size < 5) {
            val reservationDate = book.reservationDate
            if (reservationDate == null) {

                customer.holds.add(book.bookId)
                book.reservationDate = now
                book.reservationEndDate = now.plus(days.toLong(), ChronoUnit.DAYS)
                book.patronId = customer.patronId
                result = BookOnHoldApproved(book, customer)
                addLoyaltyPoints(customer)
            }
        }
    }

    if (canHaveAFreeBook(result, customer)) {
        val email = createEmail(customer.points, customer.email)
        when(result) {
            is BookOnHoldApproved -> result.emailToNotify = Optional.of(email)
            is BookOnHoldRejected -> {}
        }

    }

    return result
}

val gson = GsonBuilder().serializeNulls().create()

private fun PlaceOnHoldRequest.deepCopy(): PlaceOnHoldRequest {
    val JSON = gson.toJson(this)
    return gson.fromJson(JSON, PlaceOnHoldRequest::class.java)
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
    result: BookOnHoldResult,
    customer: Customer
) = result is BookOnHoldApproved && customer.isQualifiesForFreeBook

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

