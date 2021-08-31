package com.jamgu.krouter.core.method

import android.net.Uri

/**
 * Created by jamgu on 2021/08/31
 */
interface IMethodRouterMonitor {
    fun beforeOpen(uri: Uri, map: HashMap<Any, Any>?): Boolean = false

    fun afterOpen(uri: Uri, map: HashMap<Any, Any>?) {}

    fun onError(msg: String, e: Throwable?) {}
}