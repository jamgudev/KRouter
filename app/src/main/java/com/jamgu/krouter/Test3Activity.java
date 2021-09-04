package com.jamgu.krouter;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.jamgu.krouter.annotation.KRouter;
import com.jamgu.krouter.annotation.MethodRouter;
import com.jamgu.krouter.core.method.IAsyncMethodCallback;
import com.jamgu.krouter.core.method.IMethodInvoker;
import com.jamgu.krouter.core.method.MethodRouters;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

@KRouter(value = "test3_activity")
public class Test3Activity extends AppCompatActivity {

    private static final String TAG = "Test3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        MethodRouters.mapMethod("sss", new IMethodInvoker() {
            @Override
            public @SuppressWarnings("ALL") Boolean invoke(@Nullable Map map, @Nullable IAsyncMethodCallback iMethodCallback) {
                return null;
            }
        });
    }

    @MethodRouter("add_some")
    public static boolean addSomething(IAsyncMethodCallback<Boolean> callback, Map<Object, Object> map) {
        Log.d(TAG, "map = " + map);
        return false;
    }
}