package com.jamgu.krouter.compiler

import com.jamgu.krouter.compiler.router.RouterProcessor
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * Created by jamgu on 2021/08/22
 */
class MergeRouterProcessor : BaseProcessor() {

    private val processors = arrayOf(RouterProcessor())

    override fun init(processingEnv: ProcessingEnvironment?) {
        processors.forEach {
            it.init(processingEnv)
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        processors.forEach {
            it.process(annotations, roundEnvironment)
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return processors.fold(HashSet()) { set, process ->
            set.addAll(process.supportedAnnotationTypes)
            set
        }
    }

}