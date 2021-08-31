package com.jamgu.krouter.core.method

import android.net.Uri

/**
 * Created by jamgu on 2021/08/31
 */
internal class MethodRouterParam private constructor(private val builder: Builder){

    fun getUri() = this.builder.uri

    fun getMap() = this.builder.map

    fun getMonitor() = this.builder.monitor

    fun getCallback() = this.builder.callback

    internal class Builder {
        var uri: Uri? = null
        var map: HashMap<Any, Any>? = null
        var monitor: IMethodRouterMonitor? = null
        var callback: IMethodCallback<Any>? = null

        fun uri(uri: Uri): Builder {
            this.uri = uri
            return this
        }

        fun map(map: HashMap<Any, Any>?): Builder {
            this.map = map
            return this
        }

        fun monitor(monitor: IMethodRouterMonitor?): Builder {
            this.monitor = monitor
            return this
        }

        fun callback(callback: IMethodCallback<Any>?): Builder {
            this.callback = callback
            return this
        }

        fun build(): MethodRouterParam {
            return MethodRouterParam(this)
        }

    }

}