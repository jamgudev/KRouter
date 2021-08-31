package com.jamgu.krouter.core.method

import android.util.Log

/**
 * Created by jamgu on 2021/08/31
 */
open class MethodMapBuilder {

    companion object {
        private const val TAG = "MethodUriBuilder"
    }

    private val map = HashMap<Any, Any>()

    fun with(key: String, value: Byte?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Short?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Char?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Boolean?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Double?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Float?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Long?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: Int?): MethodMapBuilder = withAny(key, value)

    fun with(key: String, value: CharSequence?): MethodMapBuilder = withAny(key, value)


    private fun withAny(key: String, value: Any?): MethodMapBuilder {
        if (value == null) {
            Log.d(TAG, "value is null, ignore this key@$key.")
        }

        this.map[key] = value.toString()
        return this
    }

    fun build(): HashMap<Any, Any> {
        return map
    }
    
}