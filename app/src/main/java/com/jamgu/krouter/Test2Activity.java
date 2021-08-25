package com.jamgu.krouter;

import static com.jamgu.krouter.MainActivity.BOOLEAN;
import static com.jamgu.krouter.MainActivity.BYTE;
import static com.jamgu.krouter.MainActivity.CHAR;
import static com.jamgu.krouter.MainActivity.DOUBLE;
import static com.jamgu.krouter.MainActivity.FLOAT;
import static com.jamgu.krouter.MainActivity.INT;
import static com.jamgu.krouter.MainActivity.LONG;
import static com.jamgu.krouter.MainActivity.SHORT;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jamgu.krouter.annotation.KRouter;
import com.jamgu.krouter.core.router.ParamTypes;

@KRouter(value = "test2_activity", intParams = {"userId", "gameId"}, stringParams = {"user_name"},
        doubleParams = {"double_param"}, shortParams = {"short_param"}, byteParams = {"byte_param"},
        floatParams = {"float"}, longParams = "long", charParams = "char", booleanParams = "bool")
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        ParamTypes paramTypes = new ParamTypes();
        paramTypes.setIntExtra(new String[]{INT});
        paramTypes.setBooleanExtra(new String[]{BOOLEAN});
        paramTypes.setLongExtra(new String[]{LONG});
        paramTypes.setFloatExtra(new String[]{FLOAT});
        paramTypes.setDoubleExtra(new String[]{DOUBLE});
        paramTypes.setCharExtra(new String[]{CHAR});
        paramTypes.setShortExtra(new String[]{SHORT});
        paramTypes.setByteExtra(new String[]{BYTE});
//        KRouters.mapActivity("test_activity", TestActivity.class, paramTypes);
    }
}