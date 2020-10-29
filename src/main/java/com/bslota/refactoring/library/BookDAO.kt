package com.bslota.refactoring.library

import com.bslota.refactoring.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
class BookDAO {
    fun getBookFromDatabase(bookId: Int): Book {
        throw DatabaseNotChosenYetException()
    }

    fun update(book: Book?) {}
}
