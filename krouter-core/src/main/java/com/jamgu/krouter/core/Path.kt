package com.jamgu.krouter.core

import android.net.Uri
import android.text.TextUtils
import com.jamgu.krouter.core.constants.KROUTER_SCHEME_NAME
import java.util.Locale

/**
 * Created by jamgu on 2021/08/21
 */
class Path(
    val value: String,
    var next: Path?
) {

    companion object {

        fun create(uri: Uri): Path {
            val fixUri = if (!TextUtils.isEmpty(uri.scheme)) uri else {
                Uri.parse("$KROUTER_SCHEME_NAME://$uri")
            }
            val path = Path("${fixUri.scheme}://", null)

            var urlPath = fixUri.path ?: ""
            if (urlPath.endsWith("/")) {
                urlPath = urlPath.subSequence(0, urlPath.length - 1).toString()
            }

            parseUrl(path, fixUri?.host + urlPath)
            return path
        }

        /**
         * sort to: scheme -> authority -> path0 -> path1
         */
        private fun parseUrl(scheme: Path, pathUrl: String) {
            var currentPath = scheme
            pathUrl.split("/").forEach {
                currentPath.next = Path(it, null)
                currentPath = currentPath.next!!
            }
        }

        /**
         * compare one by one, return true if they are excatly the same,
         * return false otherwise
         */
        fun match(original: Path?, target: Path?): Boolean {
            if (original == null || target == null) return false

            var aPath: Path? = original
            var bPath: Path? = target
            while (aPath != null && bPath != null) {
                if (aPath.value != bPath.value) {
                    return false
                }

                aPath = aPath.next
                bPath = bPath.next
            }

            if (aPath != null || bPath != null) {
                return false
            }

            return true
        }

    }


    fun isHttp(): Boolean {
        return value.toLowerCase(Locale.getDefault()).startsWith("http://")
                || value.toLowerCase(Locale.getDefault()).startsWith("https://")
    }

}