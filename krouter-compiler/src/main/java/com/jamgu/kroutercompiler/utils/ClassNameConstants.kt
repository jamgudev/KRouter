package com.jamgu.kroutercompiler.utils

import com.squareup.javapoet.ClassName

/**
 * Created by jamgu on 2021/08/22
 */
object ClassNameConstants {
    const val KROUTER_NAME = "KRouters"

    val ANDROID_CONTEXT = ClassName.get("android.content", "Context")
    val KROUTER_CLASS = ClassName.get("com.jamgu.krouterapi.router", KROUTER_NAME)
    val PARAMTYPES_CLASS = ClassName.get("com.jamgu.krouterapi.router", "ParamTypes")
    val INTERFACES_CLASS = ClassName.get("com.jamgu.krouterapi", "IKRouterMapping")
}