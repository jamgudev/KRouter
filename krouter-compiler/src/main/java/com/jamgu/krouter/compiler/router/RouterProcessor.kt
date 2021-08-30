package com.jamgu.krouter.compiler.router

import com.jamgu.krouter.annotation.KRouter
import com.jamgu.krouter.compiler.BaseProcessor
import com.jamgu.krouter.compiler.utils.Constants.PREFIX_OF_LOGGER
import com.squareup.javapoet.ClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * Created by jamgu on 2021/08/22
 */
class RouterProcessor: BaseProcessor() {

    private val entities: ArrayList<RouterEntity> by lazy { ArrayList() }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KRouter::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (annotations.isNullOrEmpty()) {
            return false
        }

        val start = System.currentTimeMillis()

        // find all the elements that was annotated by the specific annotation
        val elements = roundEnvironment?.getElementsAnnotatedWith(KRouter::class.java)

        elements?.forEach{ element ->
            val kRouter = element.getAnnotation(KRouter::class.java)
            val routerEntity = RouterEntity()

            kRouter?.value?.forEach { value ->
                if (value.startsWith("/") || value.endsWith("/")) {
                    mLogger?.error("KRouter.value can not start/end with \"/\", " +
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
                    mLogger?.error("${kRouter.javaClass.simpleName} only support CLASS type.")
                    return true
                }
            }

            entities.add(routerEntity)
        }

        // write to java file
        RouterMappingCodeWriter(mLogger, entities, moduleName, mFiler).write()

        mLogger?.notify("[$moduleName] router process finished, cost time: ${System.currentTimeMillis() - start}ms.")

        return true
    }
}




