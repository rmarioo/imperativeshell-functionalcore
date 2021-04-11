package com.rmarioo.imperativeshell_functionalcore.library

import com.github.jcornaz.kwik.evaluator.forAll
import com.github.jcornaz.kwik.generator.api.Generator
import com.rmarioo.imperativeshell_functionalcore.library.core.BookOnHoldApproved
import com.rmarioo.imperativeshell_functionalcore.library.core.BookOnHoldRejected
import com.rmarioo.imperativeshell_functionalcore.library.core.PlaceOnHoldRequest
import com.rmarioo.imperativeshell_functionalcore.library.core.placeOnHoldCore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.random.Random

class FunctionalCoreTest {


    @Test
    fun `customers can rent a book only if has no more than 5 on hold`() {

        forAll(randomRequests) { r: PlaceOnHoldRequest ->
            if (numberOfBooksOnHold(r) < 5) placeOnHoldCore(r) is BookOnHoldApproved
                                       else placeOnHoldCore(r) is BookOnHoldRejected
        }
    }

    @Test
    fun `customer type 2 earn more points than customer type 1 and customer type 0`() {

        forAll(randomRequests) { requests: PlaceOnHoldRequest ->

            pointsFor(requests.forCustomer(2)) > pointsFor(requests.forCustomer(1)) &&
            pointsFor(requests.forCustomer(1)) > pointsFor(requests.forCustomer(0))
        }
    }


    @Test
    fun `no books`() {

        val result = placeOnHoldCore(PlaceOnHoldRequest(book = null))

        assertThat(result is BookOnHoldRejected).isTrue
    }


    @Test
    fun `already reserved book`() {

        val result =
            placeOnHoldCore(PlaceOnHoldRequest(book = Book(reservationDate = Instant.now())))

        assertThat(result is BookOnHoldRejected).isTrue
    }

    @Test
    fun `reservation is ok but no notification for free book sent`() {

        val now = Instant.now()
        val inputCustomer = Customer()
        val result =
            placeOnHoldCore(
                PlaceOnHoldRequest(customer = inputCustomer,
                book = Book(reservationDate = null),
                now = now)
            )

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

    private val randomRequests: Generator<PlaceOnHoldRequest> = Generator { rng: Random ->

        val holds = IntRange(0, rng.nextInt(0,100)).toMutableList()
        PlaceOnHoldRequest(customer = Customer(holds = holds))

    }


    private fun pointsFor(placeOnHoldRequest: PlaceOnHoldRequest): Int {
        val bookOnHoldApproved = placeOnHoldCore(placeOnHoldRequest) as BookOnHoldApproved
        return bookOnHoldApproved.customerToUpdate.points
    }


    private fun numberOfBooksOnHold(x: PlaceOnHoldRequest) =
        x.customer.holds.size


}

private fun PlaceOnHoldRequest.forCustomer(customerType: Int): PlaceOnHoldRequest =
    this.copy(customer = Customer(this.customer.points, type = customerType))
