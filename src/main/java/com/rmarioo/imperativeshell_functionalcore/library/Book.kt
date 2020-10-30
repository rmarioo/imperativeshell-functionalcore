package com.rmarioo.imperativeshell_functionalcore.library

import java.time.Instant

class Book {
    var bookId = 0
    var reservationDate: Instant? = null
    private var reservationEndDate: Instant? = null
    private var patronId = 0
    fun setReservationEndDate(date: Instant?) {
        reservationEndDate = date
    }

    fun setPatronId(patronId: Int) {
        this.patronId = patronId
    }
}
