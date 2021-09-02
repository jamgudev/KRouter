package com.jamgu.krouter.core.method

import android.net.Uri

/**
 * Created by jamgu on 2021/08/31
 */
internal class MethodRouterParam<T> private constructor(private val builder: Builder<T>){

    fun getUri() = this.builder.uri

    fun getMap() = this.builder.map

    fun getMonitor() = this.builder.monitor

    fun getCallback() = this.builder.callback

    internal class Builder<T> {
        var uri: Uri? = null
        var map: HashMap<Any, Any>? = null
        var monitor: IMethodRouterMonitor? = null
        var callback: IAsyncMethodCallback<T>? = null

        fun uri(uri: Uri): Builder<T> {
            this.uri = uri
            return this
        }

        fun map(map: HashMap<Any, Any>?): Builder<T> {
            this.map = map
            return this
        }

        fun monitor(monitor: IMethodRouterMonitor?): Builder<T> {
            this.monitor = monitor
            return this
        }

        fun callback(callback: IAsyncMethodCallback<T>?): Builder<T> {
            this.callback = callback
            return this
        }

        fun build(): MethodRouterParam<T> {
            return MethodRouterParam(this)
        }

    }

}