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
    @JvmOverloads
    fun open(context: Context, url: String, bundle: Bundle? = null, monitor: IRouterMonitor? = null) =
        open(context, Uri.parse(url), bundle, monitor)

    @JvmStatic
    @JvmOverloads
    fun open(context: Context, url: Uri, bundle: Bundle? = null, monitor: IRouterMonitor? = null) =
        open(context, url, bundle, -1, monitor)

    @JvmStatic
    @JvmOverloads
    fun openForResult(context: Context, url: String, requestCode: Int?, bundle: Bundle? = null, monitor: IRouterMonitor? = null) =
        open(context, Uri.parse(url), bundle, requestCode, monitor)

    @JvmStatic
    @JvmOverloads
    fun openForResult(context: Context, uri: Uri, requestCode: Int?, bundle: Bundle? = null, monitor: IRouterMonitor? = null) =
        open(context, uri, bundle, requestCode, monitor)

    @JvmStatic
    fun open(context: Context, url: Uri, bundle: Bundle?, requestCode: Int?, monitor: IRouterMonitor?) =
        open(
            RouterParam.Builder()
                    .context(context)
                    .uri(url)
                    .bundle(bundle)
                    .routerMonitor(monitor)
                    .requestCode(requestCode)
                    .build()
        )

    @JvmStatic
    @JvmOverloads
    fun getRawIntent(context: Context, url: String, bundle: Bundle? = null) = getRawIntent(context, Uri.parse(url), bundle)

    @JvmStatic
    @JvmOverloads
    fun getRawIntent(context: Context, uri: Uri, bundle: Bundle? = null): Intent? = getRawIntentInner(context, uri, bundle)

    @JvmStatic
    @JvmOverloads
    fun createFragment(context: Context, url: String, bundle: Bundle? = null) = createFragment(context, Uri.parse(url), bundle)

    @JvmStatic
    @JvmOverloads
    fun createFragment(context: Context, url: Uri, bundle: Bundle? = null): Fragment? =
        createFragmentInner(context, url, bundle)

    private fun getRawIntentInner(context: Context, uri: Uri, bundle: Bundle?): Intent? {
        initializeIfNeed(context)

        val path = Path.create(uri)
        sMapping.forEach {
            if (it.match(path)) {
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

                    return intent
                }
            }
        }
        return null
    }

    private fun createFragmentInner(context: Context, url: Uri, bundle: Bundle?): Fragment? {
        initializeIfNeed(context)

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
            return success
        }

        if (success) {
            routerCallback?.afterOpen(context, uri)
        } else {
            routerCallback?.onError(context, "$TAG: $uri not found", null)
        }

        return success
    }

    private fun doOpenInner(context: Context, uri: Uri, bundle: Bundle?, requestCode: Int?): Boolean {
        initializeIfNeed(context)

        val intent = getRawIntent(context, uri, bundle)
        if (intent != null) {
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
        } else {
            Log.e(TAG, "didn't find any url that matched the authority \"${uri.authority}\"")
            return false
        }
    }

    /**
     * do necessary work before everything
     */
    private fun initializeIfNeed(context: Context) {
        if (!RouterMappingInitiator.isInitialised()) {
            RouterMappingInitiator.init(context.applicationContext)
        }
    }

}