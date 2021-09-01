package com.jamgu.krouter.compiler.method

import com.squareup.javapoet.ClassName
import javax.lang.model.element.Name
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * Created by jamgu on 2021/08/31
 */
internal class MethodRouterEntity {
    var value: String? = null

    var className: ClassName? = null
    var methodName: Name? = null
    var paramElements: MutableList<out VariableElement>? = null
    var returnType: TypeMirror? = null

}