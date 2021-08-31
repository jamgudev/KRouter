package com.jamgu.krouter.core.router

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.jamgu.krouter.core.constants.KROUTER_SCHEME_NAME

/**
 * Created by jamgu on 2021/08/21
 */
open class KRouterUriBuilder
    @JvmOverloads constructor(scheme: String = KROUTER_SCHEME_NAME) {

    companion object {
        const val TAG = "RouterUriBuilder"
    }

    private val builder = Uri.Builder()

    private var hasAuthority = false

    init {
        this.builder.scheme(scheme)
    }

    fun appendAuthority(authority: String?): KRouterUriBuilder {
        if (!TextUtils.isEmpty(authority)) {
            hasAuthority = true
            // The part after the routing question mark is out of scope
            if (authority?.contains("?") == true) {
                this.builder.authority(authority.substring(0, authority.indexOf("?")))
            } else this.builder.authority(authority)
        }
        return this
    }

    fun appendPath(path: String?): KRouterUriBuilder {
        path?.split("/")?.forEach {
            if (hasAuthority) {
                this.builder.appendPath(it)
            } else {
                appendAuthority(it)
            }
        }
        return this
    }

    fun with(key: String, value: Byte?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Short?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Char?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Boolean?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Double?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Float?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Long?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: Int?): KRouterUriBuilder = withAny(key, value)

    fun with(key: String, value: CharSequence?): KRouterUriBuilder = withAny(key, value)

    private fun withAny(key: String, value: Any?): KRouterUriBuilder {
        if (value == null) {
            Log.d(TAG, "value is null, ignore this key@$key.")
        }

        this.builder.appendQueryParameter(key, value.toString())
        return this
    }

    fun build(): Uri {
        return this.builder.build()
    }

}