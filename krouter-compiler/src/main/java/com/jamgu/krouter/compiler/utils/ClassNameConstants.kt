package com.jamgu.krouter.compiler.utils

import com.squareup.javapoet.ClassName

/**
 * Created by jamgu on 2021/08/22
 */
internal object ClassNameConstants {

    val ANDROID_CONTEXT = ClassName.get("android.content", "Context")
    val KROUTER_CLASS = ClassName.get("com.jamgu.krouter.core.router", "KRouters")
    val KROUTER_METHOD_CLASS = ClassName.get("com.jamgu.krouter.core.method", "MethodRouters")
    val PARAMTYPES_CLASS = ClassName.get("com.jamgu.krouter.core.router", "ParamTypes")
    val INTERFACES_CLASS = ClassName.get("com.jamgu.krouter.core", "IKRouterMapping")

    val IMETHOD_INVOKER_CLASS = ClassName.get("com.jamgu.krouter.core.method", "IMethodInvoker")
    val MAP_CLASS = ClassName.get("java.util", "Map")
    val IMETHOD_CALLBACK_CLASS = ClassName.get("com.jamgu.krouter.core.method", "IMethodCallback")
    val OBJECT_CLASS = ClassName.get("java.lang", "Object")
    val VOID_CLASS = ClassName.get("java.lang","Void")
}