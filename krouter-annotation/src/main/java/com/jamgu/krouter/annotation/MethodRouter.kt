package com.jamgu.krouter.annotation

/**
 * Created by jamgu on 2021/08/30
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class MethodRouter(
    val value: String,
)