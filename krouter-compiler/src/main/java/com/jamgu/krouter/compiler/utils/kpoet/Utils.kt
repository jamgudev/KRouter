package com.jamgu.krouter.compiler.utils.kpoet

/**
 * Wraps an object in quotes to mirror JavaPoet's $S type.
 */
val Any?.S
    get() = "\"$this\""

/**
 * Wraps an object in literal value to String to mirror JavaPoet's $L type
 */
val Any?.L
    get() = "$this"