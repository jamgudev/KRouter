package com.jamgu.krouter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jamgu.krouter.annotation.KRouter;

@KRouter(value = "test3_activity")
public class Test3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
    }
}