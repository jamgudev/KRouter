package com.jamgu.krouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jamgu.krouter.MainActivity.Companion.CHAR
import com.jamgu.krouter.MainActivity.Companion.LONG
import com.jamgu.krouter.MainActivity.Companion.BOOLEAN
import com.jamgu.krouter.MainActivity.Companion.DOUBLE
import com.jamgu.krouter.MainActivity.Companion.INT
import com.jamgu.krouter.MainActivity.Companion.STRING
import com.jamgu.krouter.MainActivity.Companion.FLOAT

class TestActivity : AppCompatActivity() {

    companion object {
        const val TAG = "TestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Log.d(TAG, intent.extras?.getInt(INT).toString())
        Log.d(TAG, intent.extras?.getString(STRING).toString())
        Log.d(TAG, intent.extras?.getLong(LONG).toString())
        Log.d(TAG, intent.extras?.getDouble(DOUBLE).toString())
        Log.d(TAG, intent.extras?.getFloat(FLOAT).toString())
        Log.d(TAG, intent.extras?.getChar(CHAR).toString())
        Log.d(TAG, intent.extras?.getBoolean(BOOLEAN).toString())
    }

}