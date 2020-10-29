package com.rmarioo.imperativeshell_functionalcore.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class FunctionalCoreTest {

    @Test
    fun `no books`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = null)

        val (isReserved) = placeOnHoldCore(placeOnHoldRequest)

        assertThat(isReserved).isFalse
    }

    @Test
    fun `max customer holds reached`() {

        val placeOnHoldRequest =
            PlaceOnHoldRequest(customer = Customer(holds = mutableListOf(1, 2, 3, 4, 5, 6)))

        val (isReserved) = placeOnHoldCore(placeOnHoldRequest)

        assertThat(isReserved).isFalse
    }

    @Test
    fun `already reserved book`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = Book(reservationDate = Instant.now()))

        val (isReserved) = placeOnHoldCore(placeOnHoldRequest)

        assertThat(isReserved).isFalse
    }

    @Test
    fun `reservation is ok but no notification for free book sent`() {

        val (isReserved,
            updateBook,
            updateCustomer,
            updateCustomerLoyaltyPoints,
            sendEmail) = placeOnHoldCore(
            PlaceOnHoldRequest(book = Book(reservationDate = null))
        )

        assertThat(isReserved).isTrue()

        assertThat(updateBook.isPresent).isTrue()
        assertThat(updateCustomer.isPresent).isTrue()
        assertThat(updateCustomerLoyaltyPoints.isPresent).isTrue()
        assertThat(sendEmail.isPresent).`as`("notification not sent").isFalse()
    }


    @Test
    fun `reservation is ok and  notification for free book is sent`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = Book(reservationDate = null),
                                                    customer = Customer(isQualifiesForFreeBook = true)
        )
        val (isReserved,
            updateBook,
            updateCustomer,
            updateCustomerLoyaltyPoints,
            sendEmail) = placeOnHoldCore(placeOnHoldRequest)

        assertThat(isReserved).isTrue()

        assertThat(updateBook.isPresent).isTrue()
        assertThat(updateCustomer.isPresent).isTrue()
        assertThat(updateCustomerLoyaltyPoints.isPresent).isTrue()
        assertThat(sendEmail.isPresent).`as`("notification is sent").isTrue()
    }
}
