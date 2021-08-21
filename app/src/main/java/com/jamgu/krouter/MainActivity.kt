package com.jamgu.krouter

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jamgu.krouterapi.RouterUriBuilder
import com.jamgu.krouterapi.router.ParamTypes
import com.jamgu.krouterapi.router.KRouter
import com.jamgu.krouterapi.router.IRouterCallback

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
        KRouter.mapActivity("test_activity", TestActivity::class.java, paramTypes)


        KRouter.open(this, RouterUriBuilder("jamgu")
                .appendAuthority("test_activity")
                .with(INT, "28692")
                .with(BOOLEAN, true)
                .with(LONG, "100001")
                .with(STRING, "科男")
                .with(FLOAT, 2016.0)
                .with(DOUBLE, 16.toDouble())
                .with(CHAR, 'C')
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