package com.jamgu.krouter.core.router

import android.content.Context
import android.net.Uri

/**
 * Created by jamgu on 2021/08/21
 */
interface IRouterMonitor {

    fun beforeOpen(context: Context, uri: Uri): Boolean = false

    fun afterOpen(context: Context, uri: Uri) {}

    fun onError(context: Context, msg: String, e: Throwable?) {}
}
