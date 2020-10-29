package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
open class BookDAO {
    open fun getBookFromDatabase(bookId: Int): Book {
        throw DatabaseNotChosenYetException()
    }

    open fun update(book: Book?) {}
}
