#### [README-CN](https://blog.csdn.net/qq_40987010/article/details/120190404)

#### Latest version

| module  | krouter-core                                                 | krouter-compiler                                             | krouter-annotation                                           | krouter-plugin                                               |
| ------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| version | [![Download](https://img.shields.io/badge/krouter--core-v1.0.6-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-core/1.0.6/aar) | [![Download](https://img.shields.io/badge/krouter--compiler-v1.0.5-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-compiler/1.0.5/jar) | [![Download](https://img.shields.io/badge/krouter--annotation-v1.0.2-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-annotation/1.0.2/jar) | [![as plugin](https://img.shields.io/badge/krouter--plugin-v1.0.6-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-plugin/1.0.6/jar) |

# Features

- 支持通过路由获取intent
- 支持通过注解配置Activity跳转
- 支持方法注解，通过路由调用方法
- 支持给fragment注解，通过路由获取fragment实例
- 支持多模块项目
- 支持增量编译，减小编译时间消耗

# Quick Setup

Edit your **project's build.gradle** file and add classpath below.

```groovy
buildscript {
    dependencies {
        classpath "io.github.jamgudev:krouter-plugin:1.0.6"
    }
}
```

And apply krouter-plugin on your **application module**.

```groovy
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

**Multi-module support**

If you project consists of multiple modules and you want to configure KRouter for one of them, apply krouter-plugin to your module will be enough.

Add code below to your module's build.gradle file.

```groovy
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

Krouter-plugin will automatically add dependencies for you. It will edit your Gradle file and add the following dependencies, which means you **don't** need to add them by yourself.

```
implementation "io.github.jamgudev:krouter-core:1.0.6"
// kotlin project
kapt "io.github.jamgudev:krouter-compiler:1.0.5"

// java module, add below instead.
// annotationProcessor "io.github.jamgudev:krouter-compiler:1.0.5"
```

If you want to configure the dependence version on your own, add below code to your **project's build.gradle**.

```groovy
ext {
    // edit the version number you need.
	krouter_core_version = "1.0.6" 
	krouter_compiler_version = "1.0.5"
	
	// flag whether to print detail logs of compiler.
	krouter_compile_loggable = false
}
```

# Get Started

**Activity Router**

```kotlin
@KRouter(value = [MainPage.HOST_NAME])
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btn2Home).setOnClickListener {
            KRouters.openForResult(MainActivity.this, KRouterUriBuilder("helper")
                                        .appendAuthority(HomePage.HOST_NAME)
                                        .with(HomePage.USER_ID, "12345")
                                        .with(HomePage.GAME_ID, "10001")
                                        .with(HomePage.USER_NAME, "jamgu")
                                        .build(), 10)
        }
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
        const val HOST_NAME = "home_page"
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val GAME_ID = "GAME_ID"
    }
    object MainPage {
        const val HOST_NAME = "main_page"
    }

}
```

**Method Router**

```kotlin
class HomePageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomePageActivity"

        @MethodRouter("showHomePageActivity")
        fun showHomePageActivity(map: Map<Any, Any>): Boolean {
            Log.d(TAG, "map in $TAG = $map")
            return true
        }
    }
    
}


val result = MethodRouters.invoke("showHomePageActivity", MethodMapBuilder()
                .with("aaa", "123456")
                .build())
```

**Global route interceptor**

```kotlin

registerGlobalInterceptor(object : IRouterInterceptor {
    override fun intercept(uri: Uri, bundle: Bundle?): Boolean {
        // intercept if return true, invalid when isBlockGlobalMonitor was true
        return false
    }
})

```

**Single route listener**

```kotlin

KRouters.open(context, "uri", null, object : IRouterMonitor {

    })

```

# Contact Me

[Issues](https://github.com/jamgudev/KRouter/issues) & Emails are welcomed!

Email Address: 826630153@qq.com

# License

``````
Copyright (C) jamgu, KRouter Open Source Project

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

