package com.jamgu.krouterannotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class KRouter(
    val value: Array<String>,
    val intParams: Array<String> = [],
    val stringParams: Array<String> = [],
    val booleanParams: Array<String> = [],
    val shortParams: Array<String> = [],
    val longParams: Array<String> = [],
    val floatParams: Array<String> = [],
    val doubleParams: Array<String> = [],
    val charParams: Array<String> = [],
    val byteParams: Array<String> = []
)