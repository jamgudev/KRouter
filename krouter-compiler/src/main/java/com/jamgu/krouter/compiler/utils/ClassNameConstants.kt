package com.jamgu.krouter.compiler.utils

import com.jamgu.krouter.compiler.utils.Constants.KROUTER_INTERFACE_NAME
import com.jamgu.krouter.compiler.utils.Constants.KROUTER_MAP_ENTER_CLASS_NAME
import com.jamgu.krouter.compiler.utils.Constants.KROUTER_PARAMTYPES_NAME
import com.squareup.javapoet.ClassName

/**
 * Created by jamgu on 2021/08/22
 */
object ClassNameConstants {

    val ANDROID_CONTEXT = ClassName.get("android.content", "Context")
    val KROUTER_CLASS = ClassName.get("com.jamgu.krouter.core.router", KROUTER_MAP_ENTER_CLASS_NAME)
    val PARAMTYPES_CLASS = ClassName.get("com.jamgu.krouter.core.router", KROUTER_PARAMTYPES_NAME)
    val INTERFACES_CLASS = ClassName.get("com.jamgu.krouter.core", KROUTER_INTERFACE_NAME)
}