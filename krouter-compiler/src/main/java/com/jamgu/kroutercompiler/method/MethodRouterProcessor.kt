package com.jamgu.kroutercompiler.method

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class MethodRouterProcessor: AbstractProcessor() {
    override fun process(elements: MutableSet<out TypeElement>?, roundElement: RoundEnvironment?): Boolean {
        return true
    }
}