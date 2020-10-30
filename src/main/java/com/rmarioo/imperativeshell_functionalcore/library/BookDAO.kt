package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
class BookDAO {
    fun getBookFromDatabase(bookId: Int): Book {
        throw DatabaseNotChosenYetException()
    }

    fun update(book: Book?) {}
}
