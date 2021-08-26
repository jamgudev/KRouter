package com.jamgu.krouter.core

import android.content.Context

/**
 * Created by jamgu on 2021/08/23
 */
internal object RouterMappingInitiator {

    private var hasInit: Boolean = false

    @JvmStatic
    fun init(context: Context) {
        hasInit = true
    }

    fun isInitialised(): Boolean = hasInit

}