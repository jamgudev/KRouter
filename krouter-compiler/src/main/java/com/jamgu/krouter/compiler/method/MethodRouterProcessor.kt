package com.jamgu.krouter.compiler.method

import com.jamgu.krouter.annotation.MethodRouter
import com.jamgu.krouter.compiler.BaseProcessor
import com.jamgu.krouter.compiler.utils.isSubtypeOfType
import com.squareup.javapoet.ClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

internal class MethodRouterProcessor: BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(MethodRouter::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundElement: RoundEnvironment?): Boolean {
        if (annotations.isNullOrEmpty()) {
            return false
        }

        val start = System.currentTimeMillis()

        val elements = roundElement?.getElementsAnnotatedWith(MethodRouter::class.java)

        val methodRouters = ArrayList<MethodRouterEntity>()
        elements?.forEach{
            val annotation = it.getAnnotation(MethodRouter::class.java)
            val entity = MethodRouterEntity()

            val value = annotation.value
            if (value.startsWith("/") || value.endsWith("/")) {
                mLogger?.error("MethodRouter.value can not start/end with \"/\", " +
                        "at ${annotation.javaClass.canonicalName}@MethodRouter(\"$value\").")
                return true
            }

            entity.value = value
            when(it.kind) {
                ElementKind.METHOD -> {
                    entity.className = ClassName.get(it.enclosingElement as TypeElement)
                    entity.methodName = it.simpleName
                }
                else -> {
                    mLogger?.error("${annotation.javaClass.simpleName} only support Method type.")
                    return true
                }
            }
            entity.paramElements = (it as? ExecutableElement)?.parameters
            entity.returnType = (it as? ExecutableElement)?.returnType
            if (checkParams(entity, entity.paramElements)) {
                methodRouters.add(entity)
            }
        }

        MethodRouterMappingCodeWriter(mLogger, methodRouters, moduleName, mFiler).write()


        mLogger?.info("[$moduleName] MethodRouterProcessor finished, cost time: ${System.currentTimeMillis() - start}ms.")
        return true
    }

    private fun checkParams(entity: MethodRouterEntity, paramElements: List<VariableElement>?): Boolean {
        val element = paramElements ?: return false

        var mapCount = 0
        var callbackCount = 0
        element.forEach{
            val isCallback = isSubtypeOfType(it.asType(), "com.jamgu.krouter.core.method.IMethodCallback")
            val isMap = isSubtypeOfType(it.asType(), "java.util.Map")
            if (!isCallback && !isMap) {
                mLogger?.error("methodRouter@(${entity.value}): type of parameters error, " +
                        "only support sub type of (IMethodCallback, Map).")
                return false
            }
            if (isMap && ++mapCount > 1) {
                mLogger?.error("the maximum number of parameters of this type@java.util.Map is 1.")
                return false
            }
            if (isCallback && ++callbackCount > 1) {
                mLogger?.error("the maximum number of parameters of this type@IMethodCallback is 1.")
                return false
            }
        }
        return true
    }
}