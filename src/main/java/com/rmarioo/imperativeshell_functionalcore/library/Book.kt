package com.rmarioo.imperativeshell_functionalcore.library

import java.time.Instant

data class Book(
    var bookId: Int = 0,
    var reservationDate: Instant? = null,
    var reservationEndDate: Instant? = null,
    var patronId: Int = 0
)
