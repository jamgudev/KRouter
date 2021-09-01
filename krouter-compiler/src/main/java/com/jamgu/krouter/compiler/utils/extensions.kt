package com.jamgu.krouter.compiler.utils

import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * Created by jamgu on 2021/08/22
 */
internal fun isSubtypeOfType(typeMirror: TypeMirror?, otherType: String?): Boolean {
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