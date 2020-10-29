package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
open class CustomerDAO {
    open fun getCustomerFromDatabase(customerid: Int): Customer {
        throw DatabaseNotChosenYetException()
    }

    open fun update(customer: Customer?) {}
}
