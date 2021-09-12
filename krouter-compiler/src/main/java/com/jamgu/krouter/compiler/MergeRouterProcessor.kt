package com.jamgu.krouter.compiler

import com.jamgu.krouter.compiler.method.MethodRouterProcessor
import com.jamgu.krouter.compiler.router.RouterProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * Created by jamgu on 2021/08/22
 */
internal class MergeRouterProcessor : BaseProcessor() {

    private val processors = arrayOf(RouterProcessor(), MethodRouterProcessor())

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        processors.forEach {
            it.init(processingEnv)
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (annotations.isNullOrEmpty()) {
            return false
        }
        val start = System.currentTimeMillis()
        processors.forEach {
            it.process(annotations, roundEnvironment)
        }

        mLogger?.notify("($moduleName) All processes finished, cost time: ${System.currentTimeMillis() - start}ms.")

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return processors.fold(HashSet()) { set, process ->
            set.addAll(process.supportedAnnotationTypes)
            set
        }
    }

}