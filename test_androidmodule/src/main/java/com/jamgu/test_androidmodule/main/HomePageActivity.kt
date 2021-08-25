package com.jamgu.test_androidmodule.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jamgu.krouter.annotation.KRouter
import com.jamgu.test_androidmodule.R

@KRouter(["home_page_activity"])
class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
    }
}