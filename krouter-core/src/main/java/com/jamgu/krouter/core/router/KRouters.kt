package com.jamgu.krouter.core.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.jamgu.krouter.core.Mapping
import com.jamgu.krouter.core.Path
import com.jamgu.krouter.core.RouterMappingInitiator

/**
 * Created by jamgu on 2021/08/21
 */
object KRouters {

    private val TAG = this.javaClass.simpleName

    private val sMapping = ArrayList<Mapping>()

    @JvmStatic
    fun mapActivity(authority: String, activity: Class<out Activity>, paramTypes: ParamTypes?) {
        sMapping.add(Mapping(authority, null, activity, paramTypes))
    }

    @JvmStatic
    fun mapFragment(authority: String, fragment: Class<out Fragment>, paramTypes: ParamTypes?) {
        sMapping.add(Mapping(authority, fragment, null, paramTypes))
    }

    @JvmStatic
    fun open(context: Context, url: String): Boolean = open(context, Uri.parse(url))

    @JvmStatic
    fun open(context: Context, url: Uri): Boolean = open(context, url, null, null)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?) =
        open(context, url, bundle, null)

    @JvmStatic
    fun open(context: Context, url: Uri, callback: IRouterCallback?) =
        open(context, url, null, callback)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, callback: IRouterCallback?) =
        open(context, url, bundle, callback, -1)

    @JvmStatic
    fun openForResult(context: Context, url: Uri, requestCode: Int?) =
        openForResult(context, url, null, requestCode)

    @JvmStatic
    fun openForResult(context: Context, url: Uri, bundle: Bundle?, requestCode: Int?) =
        openForResult(context, url, bundle, requestCode, null)

    @JvmStatic
    fun openForResult(context: Context, url: Uri, bundle: Bundle?, requestCode: Int?, callback: IRouterCallback?) =
        open(context, url, bundle, callback, requestCode)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, routerCallback: IRouterCallback?, requestCode: Int?) =
        open(
            RouterParam.Builder()
                    .context(context)
                    .uri(url)
                    .bundle(bundle)
                    .routerCallback(routerCallback)
                    .requestCode(requestCode)
                    .build()
        )

    @JvmStatic
    fun createFragment(context: Context, url: String) = createFragment(context, Uri.parse(url))

    @JvmStatic
    fun createFragment(context: Context, url: Uri) = createFragment(context, url, null)

    @JvmStatic
    fun createFragment(context: Context, url: Uri, bundle: Bundle?): Fragment? =
        createFragmentInner(context, url, bundle)

    private fun createFragmentInner(context: Context, url: Uri, bundle: Bundle?): Fragment? {
        if (!RouterMappingInitiator.isInitialised()) {
            RouterMappingInitiator.init(context.applicationContext)
        }

        val path = Path.create(url)
        sMapping.forEach {
            if (it.match(path)) {
                if (it.fragment != null) {
                    val fragment = it.fragment.newInstance()
                    val extraBundle = it.parseExtras(url)
                    if (bundle != null) {
                        extraBundle.putAll(bundle)
                    }

                    fragment.arguments = extraBundle
                    return fragment
                }
            }
        }

        Log.e(TAG, "didn't find any url that matched the authority \"${url.authority}\"")

        return null
    }

    private fun open(routerParam: RouterParam): Boolean {
        val context = routerParam.getContext() ?: return false
        val uri = routerParam.getUri() ?: return false

        if (!RouterMappingInitiator.isInitialised()) {
            RouterMappingInitiator.init(context.applicationContext)
        }

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
                            throw RuntimeException(
                                "$TAG can not startActivityForResult" +
                                        " with context that is not subType of Activity"
                            )
                        }
                    } else {
                        context.startActivity(intent)
                    }
                    return true
                }
            }
        }

        Log.e(TAG, "didn't find any url that matched the authority \"${uri.authority}\"")

        return false
    }

}