package com.jamgu.kroutercompiler.router

import com.jamgu.krouterannotation.KRouter
import com.jamgu.kroutercompiler.utils.error
import com.squareup.javapoet.ClassName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * Created by jamgu on 2021/08/22
 */
class RouterProcessor: AbstractProcessor() {

    private var messager: Messager? = null
    private var filer: Filer? = null

    private val entities: ArrayList<RouterEntity> by lazy { ArrayList() }

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)
        messager = processingEnvironment?.messager
        filer = processingEnvironment?.filer
    }
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KRouter::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (annotations.isNullOrEmpty()) {
            return false
        }

        // find all the elements that was annotated by the specific annotation
        val elements = roundEnvironment?.getElementsAnnotatedWith(KRouter::class.java)

        elements?.forEach{ element ->
            val kRouter = element.getAnnotation(KRouter::class.java)
            val routerEntity = RouterEntity()

            kRouter?.value?.forEach { value ->
                if (value.startsWith("/") || value.endsWith("/")) {
                    error(messager,
                        "${kRouter.javaClass.simpleName}.value can not start/end with \"/\", " +
                                "at ${kRouter.javaClass.canonicalName}@Router(\"$value\").")
                    return true
                }
            }
            routerEntity.stringParams = kRouter.stringParams
            routerEntity.intParams = kRouter.intParams
            routerEntity.longParams = kRouter.longParams
            routerEntity.doubleParams = kRouter.doubleParams
            routerEntity.shortParams = kRouter.shortParams
            routerEntity.floatParams = kRouter.floatParams
            routerEntity.byteParams = kRouter.byteParams
            routerEntity.charParams = kRouter.charParams
            routerEntity.booleanParams = kRouter.booleanParams
            routerEntity.value = kRouter.value

            when(element.kind) {
                ElementKind.CLASS -> {
                    routerEntity.className = ClassName.get(element as TypeElement)
                    routerEntity.classNameTypeMirror = element.asType()
                }
                else -> {
                    error(messager, "${kRouter.javaClass.simpleName} only support CLASS type.")
                    return true
                }
            }

            entities.add(routerEntity)
        }

        // write to java file
        RouterMappingCodeWriter(messager, entities, filer).write()

        return true
    }
}




