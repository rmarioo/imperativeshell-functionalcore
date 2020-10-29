package com.rmarioo.imperativeshell_functionalcore.library

import org.springframework.stereotype.Service

@Service
class BookServiceShell(
    private val bookDAO: BookDAO,
    private val customerDAO: CustomerDAO,
    private val emailService: NotificationSender,
    private val coreFunctionWithEffectsDescription: (PlaceOnHoldRequest) -> ResultWithEffectsDescription
) {
    fun placeOnHoldShell(bookId: Int, customerId: Int, days: Int): Boolean {
        val book = bookDAO.getBookFromDatabase(bookId)
        val customer = customerDAO.getCustomerFromDatabase(customerId)


        val result: ResultWithEffectsDescription = coreFunctionWithEffectsDescription(
            PlaceOnHoldRequest(book, customer, days)
        )

        result.bookToUpdate.ifPresent{ bookDAO.update(it) }
        result.customerToUpdate.ifPresent { customerDAO.update(it) }
        result.customerToUpdateForLoyaltyPoints.ifPresent { customerDAO.update(it) }
        result.emailToNotify.ifPresent {  emailService.sendMail(it)}

        return result.isReserved
    }






}
