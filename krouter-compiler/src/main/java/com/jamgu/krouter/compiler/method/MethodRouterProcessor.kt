package com.jamgu.krouter.compiler.method

import com.jamgu.krouter.compiler.BaseProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class MethodRouterProcessor: BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return HashSet()
    }

    override fun process(elements: MutableSet<out TypeElement>?, roundElement: RoundEnvironment?): Boolean {
        return true
    }
}