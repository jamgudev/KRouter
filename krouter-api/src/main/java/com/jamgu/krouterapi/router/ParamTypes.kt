package com.jamgu.krouterapi.router

/**
 * Created by jamgu on 2021/08/21
 */
class ParamTypes {
    companion object {
        val STRING = -1
        val INT = 1
        val LONG = 2
        val BOOL = 3
        val SHORT = 4
        val FLOAT = 5
        val DOUBLE = 6
        val BYTE = 7
        val CHAR = 8
    }
    var intExtra: Array<String>? = null
    var longExtra: Array<String>? = null
    var booleanExtra: Array<String>? = null
    var shortExtra: Array<String>? = null
    var floatExtra: Array<String>? = null
    var doubleExtra: Array<String>? = null
    var byteExtra: Array<String>? = null
    var charExtra: Array<String>? = null

    fun getType(name: String): Int {
        if (arrayContain(intExtra, name)) {
            return INT
        }
        if (arrayContain(longExtra, name)) {
            return LONG
        }
        if (arrayContain(booleanExtra, name)) {
            return BOOL
        }
        if (arrayContain(shortExtra, name)) {
            return SHORT
        }
        if (arrayContain(floatExtra, name)) {
            return FLOAT
        }
        if (arrayContain(doubleExtra, name)) {
            return DOUBLE
        }
        if (arrayContain(byteExtra, name)) {
            return BYTE
        }
        if (arrayContain(charExtra, name)) {
            return CHAR
        }
        return STRING
    }

    private fun arrayContain(array: Array<String>?, value: String): Boolean {
        if (array == null) {
            return false
        }
        for (s in array) {
            if (s == value) {
                return true
            }
        }
        return false
    }


}