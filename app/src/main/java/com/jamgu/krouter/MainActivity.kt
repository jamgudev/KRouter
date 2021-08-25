package com.jamgu.krouter

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jamgu.krouter.annotation.KRouter
import com.jamgu.krouter.core.KRouterUriBuilder
import com.jamgu.krouter.core.router.IRouterCallback
import com.jamgu.krouter.core.router.KRouters
import com.jamgu.krouter.core.router.ParamTypes

@KRouter(value = ["main_activty"], intParams = [""])
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val INT = "USER_ID"
        const val BOOLEAN = "IS_OPEN"
        const val LONG = "GAME_ID"
        const val STRING = "USER_NAME"
        const val FLOAT = "YEAR"
        const val DOUBLE = "MONEY"
        const val CHAR = "CHAR"
        const val SHORT = "SHORT"
        const val BYTE = "BYTE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val paramTypes = ParamTypes()
        paramTypes.intExtra = arrayOf(INT)
        paramTypes.booleanExtra = arrayOf(BOOLEAN)
        paramTypes.longExtra = arrayOf(LONG)
        paramTypes.floatExtra = arrayOf(FLOAT)
        paramTypes.doubleExtra = arrayOf(DOUBLE)
        paramTypes.charExtra = arrayOf(CHAR)
        paramTypes.shortExtra = arrayOf(SHORT)
        paramTypes.byteExtra = arrayOf(BYTE)
//        KRouters.mapActivity("test_activity", TestActivity::class.java, paramTypes)
//        KRouters.mapActivity("test2_activity", Test2Activity::class.java, paramTypes)


        KRouters.open(this, KRouterUriBuilder("jamgu")
                .appendAuthority("other_module_activity")
                .with(INT, "28692")
                .with(BOOLEAN, true)
                .with(LONG, "100001")
                .with(STRING, "科男")
                .with(FLOAT, 2016.0)
                .with(DOUBLE, 16.toDouble())
                .with(CHAR, 'C')
                .with(SHORT, 44.toShort())
                .with(BYTE, "4")
                .build(), object: IRouterCallback {
            override fun beforeOpen(context: Context, uri: Uri): Boolean {

                Log.d(TAG, "beforeOpen called, uri = $uri")

                return false
            }

            override fun afterOpen(context: Context, uri: Uri) {
                Log.d(TAG, "afterOpen called, uri = $uri")
            }

        })
    }
}