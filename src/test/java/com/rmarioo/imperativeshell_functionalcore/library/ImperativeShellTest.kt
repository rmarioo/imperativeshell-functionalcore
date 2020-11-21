package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.library.NotificationSender.Email
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Optional


class BookServiceShellTest {


    @Test
    fun `all side effects are executed`() {

        val functionalCore = { _: PlaceOnHoldRequest ->
            BookOnHoldApproved(Book(bookId = 1),Customer(patronId = 2))
                                .apply { emailToNotify = Optional.of(Email(subject = "emailToUpdate")) }

        }


        val imperativeShell =
            BookServiceShell(spyBookDAO, spyCustomerDao, spyEmailService, functionalCore)

        val isReserved: Boolean = imperativeShell.placeOnHoldShell(1, 2, 10)

        assertEquals(true, isReserved)

        assertThat(spyBookDAO.updatedBooks).isEqualTo(listOf(Book(bookId = 1)))
        assertThat(spyCustomerDao.updatedCustomers).isEqualTo(listOf(Customer(patronId = 2)))
        assertThat(spyEmailService.notifiedEmail).isEqualTo(Email(subject = "emailToUpdate"))

    }
}
    class SpyCustomerDao(
        private val customerFromDatabase: Customer,
        var updatedCustomers: MutableList<Customer?> = mutableListOf()) : CustomerDAO() {
        override fun update(customer: Customer?) { updatedCustomers.add(customer)}
        override fun getCustomerFromDatabase(customerid: Int): Customer = customerFromDatabase
    }

    class SpyBookDao(
        private val bookFromDatabase: Book,
        var updatedBooks: MutableList<Book?> = mutableListOf()) : BookDAO() {

        override fun update(book: Book?) { updatedBooks.add(book)}
        override fun  getBookFromDatabase(bookId: Int): Book { return bookFromDatabase }
    }
    class SpyNotificationSender(var notifiedEmail: Email? = null): NotificationSender() {
        override fun sendMail(email: Email) {
            notifiedEmail = email
        }
    }

    val spyBookDAO = SpyBookDao(Book(1))
    val spyCustomerDao = SpyCustomerDao(Customer())
    val spyEmailService = SpyNotificationSender()
