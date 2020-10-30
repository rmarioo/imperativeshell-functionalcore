package com.rmarioo.imperativeshell_functionalcore.library

import com.rmarioo.imperativeshell_functionalcore.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
class CustomerDAO {
    fun getCustomerFromDatabase(customerid: Int): Customer {
        throw DatabaseNotChosenYetException()
    }

    fun update(customer: Customer?) {}
}
