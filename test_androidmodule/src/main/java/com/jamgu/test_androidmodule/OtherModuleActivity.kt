package com.jamgu.test_androidmodule

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jamgu.krouter.annotation.KRouter
import com.jamgu.krouter.core.router.KRouters

@KRouter(["other_module_activity"])
class OtherModuleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_module)
        findViewById<TextView>(R.id.btnJump).setOnClickListener{
            KRouters.open(this, "main_activty")
        }
    }
}