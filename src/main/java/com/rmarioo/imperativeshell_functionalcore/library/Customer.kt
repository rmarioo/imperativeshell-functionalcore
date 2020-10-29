package com.rmarioo.imperativeshell_functionalcore.library

data class Customer( var patronId: Int = 0,
                var type: Int = 0,
                var points: Int = 0,
                var email: String? = null,
                var isQualifiesForFreeBook: Boolean = false,
                var holds: MutableList<Int> = mutableListOf())
