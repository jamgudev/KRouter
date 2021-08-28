# Features

- 支持通过注解配置Activity跳转
- 支持多模块项目
- 支持增量编译，减小编译时间消耗

# Quick Setup

Edit your **project's build.gradle** file and add classpath below.

```
buildscript {
    dependencies {
        classpath "io.github.jamgudev:krouter-plugin:1.0.3"
    }
}
```

And apply krouter-plugin on your **application module**.

```
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

**Multi-module support**

If you project consists of multiple modules and you want to configure KRouter for one of them, apply krouter-plugin to your module will be enough.

Add code below to your module's build.gradle file.

```
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

Krouter-plugin will automatically add dependencies for you. It will edit your Gradle file and add the following dependencies, which means you **don't** need to add them by yourself.

```
implementation "io.github.jamgudev:krouter-core:1.0.1"
kapt/annotationProcessor "io.github.jamgudev:krouter-compiler:1.0.1"
```

If you want to configure the dependence version on your own, add below code to your **project's build.gradle**.

```
ext {
	krouter_core_version = 1.0.1 // edit the version number you need
	krouter_compiler_version = 1.0.1
	
	// flag whether to print compiler logs 
	krouter_compiler_version = false
}
```

# Get Started

```kotlin
@KRouter(MainPage.HOST_NAME)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn2Home).setOnClickListener(v ->
                KRouters.open(MainActivity.this, new KRouterUriBuilder("helper")
                        .appendAuthority(HomePage.HOST_NAME)
                        .with(HomePage.USER_ID, "12345")
                        .with(HomePage.GAME_ID, "10001")
                        .with(HomePage.USER_NAME, "jamgu")
                        .build(), 10)
        );
    }

}

@KRouter(
    [HOST_NAME], longParams = [USER_ID, GAME_ID],
    stringParams = [USER_NAME]
)
class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        intent?.extras?.apply {
            tvGameId.text = getLong(GAME_ID, 0).toString()
            tvUserId.text = getLong(USER_ID, 0).toString()
            tvUserName.text = getString(USER_NAME, "")
        }

        btn2Main.setOnClickListener {
            KRouters.open(this, Schemes.MainPage.HOST_NAME)
        }

    }
}

class Schemes {
    object HomePage {
        const val HOST_NAME = "home/home_page"
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val GAME_ID = "GAME_ID"
    }
    object MainPage {
        const val HOST_NAME = "app/main_page"
    }

}
```

# License

``````
Copyright (C) jamgu, Krouter Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
``````

