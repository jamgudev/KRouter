package com.jamgu.kroutercompiler.utils

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.swing.plaf.TextUI
import javax.tools.Diagnostic

/**
 * Created by jamgu on 2021/08/22
 */
fun AbstractProcessor.error(messager: Messager?, msg: String) {
    messager?.printMessage(Diagnostic.Kind.ERROR, msg)
}

fun AbstractProcessor.warn(messager: Messager?, msg: String) {
    messager?.printMessage(Diagnostic.Kind.WARNING, msg)
}

fun AbstractProcessor.note(messager: Messager?, msg: String) {
    messager?.printMessage(Diagnostic.Kind.NOTE, msg)
}

fun isSubtypeOfType(typeMirror: TypeMirror?, otherType: String?): Boolean {
    if (typeMirror == null || otherType?.isEmpty() == true) {
        return false
    }

    if (otherType == typeMirror.toString()) {
        return true
    }
    if (typeMirror.kind != TypeKind.DECLARED) {
        return false
    }
    val declaredType = typeMirror as DeclaredType
    val element = declaredType.asElement()


    if (otherType == element.toString()) {
        return true
    }
    if (element !is TypeElement) {
        return false
    }
    val superType = element.superclass
    //System.out.println(declaredType + ":" + superType);
    if (isSubtypeOfType(superType, otherType)) {
        return true
    }
    for (interfaceType in element.interfaces) {
        if (isSubtypeOfType(interfaceType, otherType)) {
            return true
        }
    }
    return false
}