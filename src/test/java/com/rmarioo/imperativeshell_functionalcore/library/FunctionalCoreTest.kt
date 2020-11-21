package com.rmarioo.imperativeshell_functionalcore.library

import com.github.jcornaz.kwik.evaluator.forAll
import com.github.jcornaz.kwik.generator.api.Generator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.random.Random

class FunctionalCoreTest {


    @Test
    fun `customers can rent a book only if has no more than 5 on hold`() {

        forAll(customerWithSomeHolds) { r: PlaceOnHoldRequest ->
            if (numberOfBooksOnHold(r) < 5) placeOnHoldCore(r) is BookOnHoldApproved
                                       else placeOnHoldCore(r) is BookOnHoldRejected
        }
    }

    @Test
    fun `customer type 2 earn more points than customer type 1 and customer type 0`() {
        forAll(requestFromDifferentCustomerTypes) { requests: MultipleRequests ->

            pointsFor(requests.customerType2) > pointsFor(requests.customerType1) &&
            pointsFor(requests.customerType1) > pointsFor(requests.customerType0)
        }
    }

    private val customerWithSomeHolds: Generator<PlaceOnHoldRequest> = Generator { rng: Random ->

        val holds = IntRange(0, rng.nextInt(0,100)).toMutableList()
        PlaceOnHoldRequest(customer = Customer(holds = holds))

    }

    private val requestFromDifferentCustomerTypes: Generator<MultipleRequests> =
        Generator { rng: Random ->
            val points = rng.nextInt(0, 10000)
            MultipleRequests(PlaceOnHoldRequest(customer = Customer(points = points, type = 2 )),
                   PlaceOnHoldRequest(customer = Customer(points = points, type = 1)),
                   PlaceOnHoldRequest(customer = Customer(points = points, type = 0))
            )
        }

    data class MultipleRequests(val customerType2: PlaceOnHoldRequest,
                                val customerType1: PlaceOnHoldRequest,
                                val customerType0: PlaceOnHoldRequest)


    private fun pointsFor(placeOnHoldRequest: PlaceOnHoldRequest): Int {
        val bookOnHoldApproved = placeOnHoldCore(placeOnHoldRequest) as BookOnHoldApproved
        return bookOnHoldApproved.customerToUpdate.points
    }


    private fun numberOfBooksOnHold(x: PlaceOnHoldRequest) =
        x.customer.holds.size


    @Test
    fun `no books`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = null)

        val result = placeOnHoldCore(placeOnHoldRequest)

        assertThat(result is BookOnHoldRejected).isTrue
    }


    @Test
    fun `already reserved book`() {

        val placeOnHoldRequest = PlaceOnHoldRequest(book = Book(reservationDate = Instant.now()))

        val result = placeOnHoldCore(placeOnHoldRequest)

        assertThat(result is BookOnHoldRejected).isTrue
    }

    @Test
    fun `reservation is ok but no notification for free book sent`() {

        val now = Instant.now()
        val inputCustomer = Customer()
        val result =
            placeOnHoldCore(PlaceOnHoldRequest(customer = inputCustomer,
                book = Book(reservationDate = null),
                now = now))

        assertThat(result is BookOnHoldApproved).isTrue

        val bookOnHoldApproved = result as BookOnHoldApproved

        assertThat(bookOnHoldApproved.bookToUpdate.reservationDate).isEqualTo(now)
        assertThat(bookOnHoldApproved.customerToUpdate).isEqualTo(Customer(points = 1,holds = mutableListOf(0)))
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
