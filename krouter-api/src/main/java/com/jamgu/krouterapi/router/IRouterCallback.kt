package com.jamgu.krouterapi.router

import android.content.Context
import android.net.Uri

/**
 * Created by jamgu on 2021/08/21
 */
interface IRouterCallback {

    fun beforeOpen(context: Context, uri: Uri): Boolean = false

    fun afterOpen(context: Context, uri: Uri) {}

    fun onError(context: Context, msg: String, e: Throwable?) {}
}
