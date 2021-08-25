package com.jamgu.krouter

import android.app.Application
import com.jamgu.krouter.core.KRouterInit

/**
 * Created by jamgu on 2021/08/23
 */
class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        KRouterInit.init(applicationContext)
    }

}