package com.jamgu.krouterapi.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.jamgu.krouterapi.Mapping
import com.jamgu.krouterapi.Path
import java.lang.RuntimeException

/**
 * Created by jamgu on 2021/08/21
 */
object KRouters {

    private const val TAG = "KRouter"

    private val sMapping = ArrayList<Mapping>()

    @JvmStatic
    fun mapActivity(authority: String, activity: Class<out Activity>, paramTypes: ParamTypes?) {
        sMapping.add(Mapping(authority, activity, paramTypes))
    }

    @JvmStatic
    fun open(context : Context, url: String): Boolean = open(context, Uri.parse(url))

    @JvmStatic
    fun open(context: Context, url: Uri): Boolean = open(context, url, null, -1)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?) =
        open(context, url, bundle, -1)

    @JvmStatic
    fun open(context: Context, url: Uri, requestCode: Int?) =
        open(context, url, null, requestCode)

    fun open(context: Context, url: Uri, callback: IRouterCallback?) =
        open(context, url, null, callback)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, callback: IRouterCallback?) =
        open(context, url, bundle, callback, -1)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, requestCode: Int?) =
        open(context, url, bundle, null, requestCode)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, routerCallback: IRouterCallback?, requestCode: Int?) =
        open(RouterParam.Builder()
                .context(context)
                .uri(url)
                .bundle(bundle)
                .routerCallback(routerCallback)
                .requestCode(requestCode)
                .build())

    private fun open(routerParam: RouterParam): Boolean {
        val context = routerParam.getContext() ?: return false
        val uri = routerParam.getUri() ?: return false

        val bundle = routerParam.getBundle()
        val routerCallback = routerParam.getRouterCallback()
        val requestCode = routerParam.getRequestCode()

        if (routerCallback?.beforeOpen(context, uri) == true) {
            return false
        }

        var success = false
        try {
            success = doOpenInner(context, uri, bundle, requestCode)
        } catch (e: Throwable) {
            routerCallback?.onError(context, "${e.message}", e)
        }

        if (success) {
            routerCallback?.afterOpen(context, uri)
        } else {
            routerCallback?.onError(context, "$TAG: $uri not found", null)
        }

        return success
    }

    private fun doOpenInner(context: Context, uri: Uri, bundle: Bundle?, requestCode: Int?): Boolean {
        val targetPath = Path.create(uri)
        sMapping.forEach {
            if (it.match(targetPath)) {
                if (it.activity != null) {
                    val intent = Intent(context, it.activity)

                    if (bundle != null) {
                        intent.putExtras(bundle)
                    }

                    // get the params on the uri
                    intent.putExtras(it.parseExtras(uri))

                    // context type could be context of service
                    if (context !is Activity) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    Log.d(TAG, "open uri = $uri")
                    if (requestCode != null && requestCode >= 0) {
                        if (context is Activity) {
                            context.startActivityForResult(intent, requestCode)
                        } else {
                            throw RuntimeException("$TAG can not startActivityForResult" +
                                    " with context that is not subType of Activity")
                        }
                    } else {
                        context.startActivity(intent)
                    }
                    return true
                }
            }
        }
        return false
    }

}