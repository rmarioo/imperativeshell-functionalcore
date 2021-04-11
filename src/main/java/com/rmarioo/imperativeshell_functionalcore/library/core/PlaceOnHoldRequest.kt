package com.rmarioo.imperativeshell_functionalcore.library.core

import com.rmarioo.imperativeshell_functionalcore.library.Book
import com.rmarioo.imperativeshell_functionalcore.library.Customer
import java.time.Instant

data class PlaceOnHoldRequest(
    val book: Book? = Book(),
    val customer: Customer = Customer(),
    val days: Int = 1,
    val now: Instant = Instant.now()
)
