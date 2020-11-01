package com.rmarioo.imperativeshell_functionalcore.library

import org.springframework.stereotype.Service

@Service
class BookServiceShell(
    private val bookDAO: BookDAO,
    private val customerDAO: CustomerDAO,
    private val emailService: NotificationSender,
    private val coreFunctionWithEffectsDescription: (PlaceOnHoldRequest) -> BookOnHoldResult
) {
    fun placeOnHoldShell(bookId: Int, customerId: Int, days: Int): Boolean {
        val book = bookDAO.getBookFromDatabase(bookId)
        val customer = customerDAO.getCustomerFromDatabase(customerId)

        val result: BookOnHoldResult = coreFunctionWithEffectsDescription(PlaceOnHoldRequest(book, customer, days))

        return when(result) {
            is BookOnHoldApproved -> { executeSideEffects(result); true }
            is BookOnHoldRejected -> false
        }

    }

    private fun executeSideEffects(result: BookOnHoldApproved) {
        bookDAO.update(result.bookToUpdate)
        customerDAO.update(result.customerToUpdate)
        result.emailToNotify.map { email -> emailService.sendMail(email) }
    }


}
