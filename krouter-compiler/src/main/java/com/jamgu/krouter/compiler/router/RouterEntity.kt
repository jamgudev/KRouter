package com.jamgu.krouter.compiler.router

import com.squareup.javapoet.ClassName
import javax.lang.model.type.TypeMirror

/**
 * Created by jamgu on 2021/08/22
 */
internal open class RouterEntity {
    var value: Array<String>? = null

    var stringParams: Array<String>? = null
    var intParams: Array<String>? = null
    var longParams: Array<String>? = null
    var booleanParams: Array<String>? = null
    var shortParams: Array<String>? = null
    var floatParams: Array<String>? = null
    var doubleParams: Array<String>? = null
    var byteParams: Array<String>? = null
    var charParams: Array<String>? = null

    var className: ClassName? = null
    var classNameTypeMirror: TypeMirror? = null

    private fun isEmptyArray(array: Array<String>?): Boolean {
        if (array == null || array.isEmpty()) {
            return true
        }
        for (item in array) {
            if (item.isNotEmpty()) {
                return false
            }
        }
        return true
    }

    private fun isNotEmptyArray(array: Array<String>?): Boolean {
        return !isEmptyArray(array)
    }


    fun hasExtraData(): Boolean {
        return hasNonStringParams()
    }

    fun hasNonStringParams(): Boolean {
        return (isNotEmptyArray(intParams) || isNotEmptyArray(longParams)
                || isNotEmptyArray(booleanParams) || isNotEmptyArray(shortParams)
                || isNotEmptyArray(floatParams) || isNotEmptyArray(doubleParams)
                || isNotEmptyArray(byteParams) || isNotEmptyArray(charParams))
    }
}