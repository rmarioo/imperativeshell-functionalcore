package com.bslota.refactoring.library

import com.bslota.refactoring.util.DatabaseNotChosenYetException
import org.springframework.stereotype.Repository

@Repository
class CustomerDAO {
    fun getCustomerFromDatabase(customerid: Int): Customer {
        throw DatabaseNotChosenYetException()
    }

    fun update(customer: Customer?) {}
}
