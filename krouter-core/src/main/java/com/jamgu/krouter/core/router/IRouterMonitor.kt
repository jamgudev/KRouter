package com.jamgu.krouter.core.router

import android.content.Context
import android.net.Uri

/**
 * Created by jamgu on 2021/08/21
 *
 * monitor that will be called before/after router open,
 * call onError() if uri not found or error caught.
 */
interface IRouterMonitor {

    /**
     * intercept if return true, return false by default.
     */
    fun beforeOpen(context: Context, uri: Uri): Boolean = false

    fun afterOpen(context: Context, uri: Uri) {}

    fun onError(context: Context, msg: String, e: Throwable?) {}
}
