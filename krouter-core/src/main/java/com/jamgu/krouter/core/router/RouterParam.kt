package com.jamgu.krouter.core.router

import android.content.Context
import android.net.Uri
import android.os.Bundle

/**
 * Created by jamgu on 2021/08/21
 */
internal class RouterParam private constructor(private val builder: Builder) {

    fun getContext(): Context? = this.builder.context

    fun getUri(): Uri? = this.builder.uri

    fun getBundle(): Bundle? = this.builder.bundle

    fun getRequestCode(): Int? = this.builder.requestCode

    fun getRouterCallback(): IRouterMonitor? = this.builder.routerMonitor

    internal class Builder {
        var context: Context? = null
        var uri: Uri? = null
        var requestCode: Int? = -1
        var routerMonitor: IRouterMonitor? = null
        var bundle: Bundle? = null

        fun context(context: Context?): Builder {
            this.context = context
            return this
        }

        fun uri(uri: Uri?): Builder {
            this.uri = uri
            return this
        }

        fun requestCode(code: Int?): Builder {
            this.requestCode = code
            return this
        }

        fun routerMonitor(monitor: IRouterMonitor?): Builder {
            this.routerMonitor = monitor
            return this
        }

        fun bundle(bundle: Bundle?): Builder {
            this.bundle = bundle
            return this
        }

        fun build(): RouterParam {
            return RouterParam(this)
        }
    }

}