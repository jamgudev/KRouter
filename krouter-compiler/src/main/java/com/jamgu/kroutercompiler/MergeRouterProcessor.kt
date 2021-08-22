package com.jamgu.kroutercompiler

import com.jamgu.kroutercompiler.router.RouterProcessor
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * Created by jamgu on 2021/08/22
 */
class MergeRouterProcessor : AbstractProcessor() {

    private val processors = arrayOf(RouterProcessor())

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        processors.forEach {
            it.init(p0)
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

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }
}