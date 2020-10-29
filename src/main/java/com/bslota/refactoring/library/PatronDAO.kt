package com.bslota.refactoring.library

import com.bslota.refactoring.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
class PatronDAO {
    fun getPatronFromDatabase(patronId: Int): Patron {
        throw DatabaseNotChosenYetException()
    }

    fun update(patron: Patron?) {}
}
