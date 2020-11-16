package com.rmarioo.imperativeshell_functionalcore.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class FunctionalCoreTest {

    @Test
    fun `no books`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = null)

        val result = placeOnHoldCore(placeOnHoldRequest)

        assertThat(result is BookOnHoldRejected)
    }

    @Test
    fun `max customer holds reached`() {

        val placeOnHoldRequest =
            PlaceOnHoldRequest(customer = Customer(holds = mutableListOf(1, 2, 3, 4, 5, 6)))

        val result = placeOnHoldCore(placeOnHoldRequest)


        assertThat(result is BookOnHoldRejected)
    }

    @Test
    fun `already reserved book`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = Book(reservationDate = Instant.now()))

        val result = placeOnHoldCore(placeOnHoldRequest)

        assertThat(result is BookOnHoldRejected)
    }

    @Test
    fun `reservation is ok but no notification for free book sent`() {

        val now = Instant.now()
        val inputCustomer = Customer()
        val result =
            placeOnHoldCore(PlaceOnHoldRequest(customer = inputCustomer,
                book = Book(reservationDate = null),
                now = now))

        assertThat(result is BookOnHoldApproved)

        val bookOnHoldApproved = result as BookOnHoldApproved

        assertThat(bookOnHoldApproved.bookToUpdate.reservationDate).isEqualTo(now)
        assertThat(bookOnHoldApproved.customerToUpdate).isEqualTo(inputCustomer.copy(points = 1))
        assertThat(bookOnHoldApproved.emailToNotify).`as`("notification not sent").isNotPresent
    }

    @Test
    fun `reservation is pure function it returns the same result if called multiple times with same input `() {

        val now = Instant.now()
        val request = PlaceOnHoldRequest(book = Book(reservationDate = null), now = now)

        val r1 = placeOnHoldCore(request) as BookOnHoldApproved

        val r2 = placeOnHoldCore(request) as BookOnHoldApproved

        assertThat(r1).isEqualTo(r2)
    }


    @Test
    fun `reservation is ok and  notification for free book is sent`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = Book(reservationDate = null),
                                                    customer = Customer(isQualifiesForFreeBook = true)
        )
        val result = placeOnHoldCore(placeOnHoldRequest)

        val bookOnHoldApproved = result as BookOnHoldApproved
        assertThat(bookOnHoldApproved.emailToNotify).`as`("notification not sent").isPresent
    }
}
