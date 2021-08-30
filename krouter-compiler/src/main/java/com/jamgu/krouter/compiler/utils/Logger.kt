package com.jamgu.krouter.compiler.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Created by jamgu on 2021/08/24
 */
class Logger(private val msg: Messager, private val loggable: Boolean) {

    fun info(info: CharSequence) {
        if (info.isNotEmpty() && loggable) {
            println(Constants.PREFIX_OF_LOGGER + info)
//            msg.printMessage(Diagnostic.Kind.NOTE, Constants.PREFIX_OF_LOGGER + info + "\n")
        }
    }

    fun error(error: CharSequence) {
        if (error.isNotEmpty()) {
            msg.printMessage(
                Diagnostic.Kind.ERROR,
                Constants.PREFIX_OF_LOGGER + "an exception is encountered, [" + error + "]"
            )
        }
    }

    fun error(error: Throwable?) {
        if (null != error) {
            msg.printMessage(
                Diagnostic.Kind.ERROR,
                Constants.PREFIX_OF_LOGGER + "an exception is encountered, [" + error.message + "]" + "\n" + formatStackTrace(
                    error.stackTrace
                )
            )
        }
    }

    fun warning(warning: CharSequence) {
        if (warning.isNotEmpty() && loggable) {
            msg.printMessage(Diagnostic.Kind.WARNING, Constants.PREFIX_OF_LOGGER + warning)
        }
    }

    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}