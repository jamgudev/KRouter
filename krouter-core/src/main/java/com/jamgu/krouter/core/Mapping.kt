package com.jamgu.krouter.core

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.jamgu.krouter.core.constants.KROUTER_SCHEME_NAME
import com.jamgu.krouter.core.method.IMethodInvoker
import com.jamgu.krouter.core.router.ParamTypes
import java.util.Locale

/**
 * Created by jamgu on 2021/08/21
 */
internal class Mapping(
    var authority: String?,
    val activity: Class<out Activity>?,
    val methodInvoker: IMethodInvoker?,
    val paramTypes: ParamTypes?,
) {

    companion object {
        const val TAG = "Mapping"
    }

    /**
     * formatPath is the property that used for match
     */
    var formatPath: Path? = null

    init {
        fixAuthority()?.let {
            val lowerCaseAuthority = it.toLowerCase(Locale.getDefault())
            formatPath = if (lowerCaseAuthority.startsWith("http://")
                    || lowerCaseAuthority.startsWith("https://")
            ) {
                Path.create(Uri.parse(it))
            } else {
                Path.create(Uri.parse("$KROUTER_SCHEME_NAME://$it"))
            }
        }
    }

    /**
     * Get rid of the question mark "?" if authority has.
     */
    private fun fixAuthority(): String? {
        var fixAuthority = authority ?: return null

        authority?.let {
            val idx = it.indexOf("?")
            if (idx > 0) {
                fixAuthority = it.substring(0, idx)
            }
        }

        return fixAuthority
    }

    constructor(authority: String?, activity: Class<out Activity>?, paramTypes: ParamTypes?) :
            this(authority, activity, null, paramTypes)

    constructor(authority: String?, methodInvoker: IMethodInvoker?, paramTypes: ParamTypes?) :
            this(authority, null, methodInvoker, paramTypes)


    /**
     * Compare without scheme if it's not start with http/https.
     */
    fun match(targetPath: Path): Boolean {
        return if (formatPath?.isHttp() == true) {
            Path.match(formatPath, targetPath)
        } else {
            Path.match(formatPath?.next, targetPath.next)
        }
    }

    /**
     * Parse the parameters on the uri.
     */
    fun parseExtras(uri: Uri): Bundle {
        val bundle = Bundle()
        // parameter
        val names: Set<String> = uri.queryParameterNames
        for (name in names) {
            val value = uri.getQueryParameter(name)
            put(bundle, name, value)
        }

        return bundle
    }

    private fun put(bundle: Bundle, name: String, value: String?) {
        paramTypes ?: return
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "key $name's value is empty, ignored.")
            return
        }

        value ?: return
        when (paramTypes.getType(name)) {
            ParamTypes.INT -> bundle.putInt(name, value.toInt())
            ParamTypes.LONG -> bundle.putLong(name, value.toLong())
            ParamTypes.BOOL -> bundle.putBoolean(name, value.toBoolean())
            ParamTypes.SHORT -> bundle.putShort(name, value.toShort())
            ParamTypes.FLOAT -> bundle.putFloat(name, value.toFloat())
            ParamTypes.DOUBLE -> bundle.putDouble(name, value.toDouble())
            ParamTypes.BYTE -> bundle.putByte(name, value.toByte())
            ParamTypes.CHAR -> bundle.putChar(name, value[0])
            else -> bundle.putString(name, value)
        }
    }

}