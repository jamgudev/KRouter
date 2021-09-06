package com.jamgu.krouter.core.router

import android.net.Uri
import android.os.Bundle

/**
 * Created by jamgu on 2021/09/06
 */
interface IRouterInterceptor {

    /**
     * intercept if return true, which means KRouters will ignore this router call.
     * invalid when isBlockGlobalMonitor was true, set by [KRouters.open] or [KRouters.openForResult].
     * more details, pls see [KRouters.onGlobalIntercept].
     */
    fun intercept(uri: Uri, bundle: Bundle?): Boolean

}