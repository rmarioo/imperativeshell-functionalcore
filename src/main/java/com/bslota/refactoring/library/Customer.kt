package com.bslota.refactoring.library

class Customer {
    var patronId = 0
    var type = 0
    var points = 0
    var email: String? = null
    var isQualifiesForFreeBook = false
    var holds: MutableList<Int> = mutableListOf()
}
