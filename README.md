#### [README-EN](https://github.com/jamgudev/KRouter/blob/master/README_EN.md)

#### 最新版本

| module  | krouter-core                                                 | krouter-compiler                                             | krouter-annotation                                           | krouter-plugin                                               |
| ------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| version | [![Download](https://img.shields.io/badge/krouter--core-v1.0.6-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-core/1.0.6/aar) | [![Download](https://img.shields.io/badge/krouter--compiler-v1.0.5-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-compiler/1.0.5/jar) | [![Download](https://img.shields.io/badge/krouter--annotation-v1.0.2-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-annotation/1.0.2/jar) | [![as plugin](https://img.shields.io/badge/krouter--plugin-v1.0.6-brightgreen)](https://search.maven.org/artifact/io.github.jamgudev/krouter-plugin/1.0.6/jar) |

# 特性

- 支持通过路由获取intent
- 支持通过注解配置Activity跳转
- 支持方法注解，通过路由调用方法
- 支持给fragment注解，通过路由获取fragment实例
- 支持多模块项目
- 支持增量编译，减小编译时间消耗

# 快速开始

编辑你的**project's build.gradle**文件，然后添加如下classpath依赖.

```groovy
buildscript {
    dependencies {
        classpath "io.github.jamgudev:krouter-plugin:1.0.6"
    }
}
```

在你的**Application Module**模块引用KRouter插件。

```groovy
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

**多模块支持**

如果你的项目是由多个模块组成的，并希望给你的某个子模块引用KRouter，你只需要在该模块的gradle文件中引用插件即可。

添加如下代码到你子模块的build.gradle文件中：

```groovy
plugins {
    id 'com.jamgu.krouter.plugin'
}
```

添加完毕后，KRouter插件会自动为你的子模块添加所需的库依赖，
插件会编辑子模块的build.gradle文件，并添加下面所示依赖库，自动为你引用最新版本的依赖库，所以你不需要自己添加库依赖。

```
implementation "io.github.jamgudev:krouter-core:1.0.6"
// kotlin project
kapt "io.github.jamgudev:krouter-compiler:1.0.5"

// java module, add below instead.
// annotationProcessor "io.github.jamgudev:krouter-compiler:1.0.5"
```

如果你希望自己指定依赖库的版本，可以在你项目根目录的build.gradle文件中添加配置：

```groovy
ext {
    // edit the version number you need.
	krouter_core_version = "1.0.6" 
	krouter_compiler_version = "1.0.5"
	
	// flag whether to print detail logs of compiler.
	krouter_compile_loggable = false
}
```

# 开始使用

**Activity路由**

```kotlin
@KRouter(value = [MainPage.HOST_NAME])
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btn2Home).setOnClickListener {
            // 通过 KRouter 打开标注为HomePage.HOST_NAME的 Activity，
            // 并向该Activity传递一些参数。
            KRouters.openForResult(MainActivity.this, KRouterUriBuilder("helper")
                                        .appendAuthority(HomePage.HOST_NAME)
                                        .with(HomePage.USER_ID, "12345")
                                        .with(HomePage.GAME_ID, "10001")
                                        .with(HomePage.USER_NAME, "jamgu")
                                        .build(), 10)
        }
    }

}

// 通过注解向KRouter注册一个Activity，并指定接收的参数以及参数类型
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

// 可以定义一个Schemes类，对路由进行统一管理
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

**Method路由**

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


// 调用注解为showHomePageActivity的方法，并向方法传递参数
val result = MethodRouters.invoke("showHomePageActivity", MethodMapBuilder()
                .with("aaa", "123456")
                .build())
```

**全局路由拦截器**

```kotlin

registerGlobalInterceptor(object : IRouterInterceptor {
    override fun intercept(uri: Uri, bundle: Bundle?): Boolean {
        // intercept if return true, invalid when isBlockGlobalMonitor was true
        return false
    }
})

```

**单次路由监听**

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

