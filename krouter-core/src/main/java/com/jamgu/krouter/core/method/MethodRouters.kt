package com.jamgu.krouter.core.method

import android.net.Uri
import android.util.Log
import com.jamgu.krouter.core.Mapping
import com.jamgu.krouter.core.Path

/**
 * Created by jamgu on 2021/08/31
 */
object MethodRouters {

    private val TAG = this::class.java.simpleName

    private val sMethodMapping: ArrayList<Mapping> = ArrayList()

    @JvmStatic
    fun <T> mapMethod(authority: String, invoker: IMethodInvoker) {
        removeIfExists(authority)
        sMethodMapping.add(Mapping(authority, null, null, invoker, null))
    }

    private fun removeIfExists(authority: String) {
        var foundIdx = -1
        run outForeach@{
            sMethodMapping.forEachIndexed { idx, it ->
                if (it.authority == authority) {
                    foundIdx = idx
                    return@outForeach
                }
            }
        }

        if (foundIdx > -1) {
            val removedItem = sMethodMapping.removeAt(foundIdx)
            Log.d(TAG, "remove duplicated method router@$removedItem")
        }
    }

    @JvmStatic
    @JvmOverloads
    fun <T> invoke(url: String, map: HashMap<Any, Any>? = null, callback: IAsyncMethodCallback<T>? = null, monitor: IMethodRouterMonitor? = null) =
        invoke(Uri.parse((url)), map, callback, monitor)

    @JvmStatic
    @JvmOverloads
    fun <T> invoke(uri: Uri, map: HashMap<Any, Any>? = null, callback: IAsyncMethodCallback<T>? = null, monitor: IMethodRouterMonitor? = null) =
        invoke(MethodRouterParam.Builder<T>()
                .uri(uri)
                .map(map)
                .monitor(monitor)
                .callback(callback)
                .build())

    private fun <T> invoke(param: MethodRouterParam<T>): T? {
        val uri = param.getUri() ?: return null

        val map = param.getMap()
        val monitor = param.getMonitor()
        val callback = param.getCallback()

        if (monitor?.beforeOpen(uri, map) == true) {
            return null
        }

        var result: T? = null
        try {
            result = doInvokeMethod(uri, map, callback)
        } catch (e: Exception) {
            monitor?.onError("${e.message}", e)
            return result
        }

        monitor?.afterOpen(uri, map)

        return result
    }

    private fun <T> doInvokeMethod(uri: Uri, map: HashMap<Any, Any>?, callback: IAsyncMethodCallback<T>?): T? {
        val path = Path.create(uri)
        sMethodMapping.forEach{
            if (it.match(path)) {
                return it.methodInvoker?.invoke(map, callback)
            }
        }
        return null
    }

}