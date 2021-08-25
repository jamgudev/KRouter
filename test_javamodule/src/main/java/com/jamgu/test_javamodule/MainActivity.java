package com.jamgu.test_javamodule;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jamgu.krouter.annotation.KRouter;

@KRouter("mass")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}