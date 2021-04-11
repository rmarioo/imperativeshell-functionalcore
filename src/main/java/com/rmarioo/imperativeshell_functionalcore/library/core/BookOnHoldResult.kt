package com.rmarioo.imperativeshell_functionalcore.library.core

import com.rmarioo.imperativeshell_functionalcore.library.Book
import com.rmarioo.imperativeshell_functionalcore.library.Customer
import com.rmarioo.imperativeshell_functionalcore.library.NotificationSender
import java.util.Optional


sealed class BookOnHoldResult

data class BookOnHoldApproved(val bookToUpdate: Book, val customerToUpdate: Customer) : BookOnHoldResult() {
    var emailToNotify: Optional<NotificationSender.Email> = Optional.empty()
}
object BookOnHoldRejected : BookOnHoldResult()
