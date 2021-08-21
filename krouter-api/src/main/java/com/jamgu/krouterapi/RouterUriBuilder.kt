package com.jamgu.krouterapi

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.jamgu.krouterapi.constants.KROUTER_SCHEME_NAME

/**
 * Created by jamgu on 2021/08/21
 */
open class RouterUriBuilder
    @JvmOverloads constructor(scheme: String = KROUTER_SCHEME_NAME) {

    companion object {
        const val TAG = "RouterUriBuilder"
    }

    private val builder = Uri.Builder()

    private var hasAuthority = false

    init {
        this.builder.scheme(scheme)
    }

    fun appendAuthority(authority: String?): RouterUriBuilder {
        if (!TextUtils.isEmpty(authority)) {
            hasAuthority = true
            // The part after the routing question mark is out of scope
            if (authority?.contains("?") == true) {
                this.builder.authority(authority.substring(0, authority.indexOf("?")))
            } else this.builder.authority(authority)
        }
        return this
    }

    fun appendPath(path: String?): RouterUriBuilder {
        path?.split("/")?.forEach {
            if (hasAuthority) {
                this.builder.appendPath(it)
            } else {
                appendAuthority(it)
            }
        }
        return this
    }

    fun with(key: String, value: Byte?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Short?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Char?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Boolean?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Double?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Float?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Long?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Int?): RouterUriBuilder = withAny(key, value)

    fun with(key: String, value: CharSequence?): RouterUriBuilder = withAny(key, value)

    private fun withAny(key: String, value: Any?): RouterUriBuilder {
        if (value == null) {
            Log.d(TAG, "value is null, ignore this key.")
        }

        this.builder.appendQueryParameter(key, value.toString())
        return this
    }

    fun build(): Uri {
        return this.builder.build()
    }

}