package com.jamgu.krouter.compiler.method

import com.jamgu.krouter.compiler.utils.ClassNameConstants
import com.jamgu.krouter.compiler.utils.ClassNameConstants.MAP_CLASS
import com.jamgu.krouter.compiler.utils.ClassNameConstants.IMETHOD_CALLBACK_CLASS
import com.jamgu.krouter.compiler.utils.ClassNameConstants.IMETHOD_INVOKER_CLASS
import com.jamgu.krouter.compiler.utils.ClassNameConstants.KROUTER_METHOD_CLASS
import com.jamgu.krouter.compiler.utils.ClassNameConstants.OBJECT_CLASS
import com.jamgu.krouter.compiler.utils.ClassNameConstants.VOID_CLASS
import com.jamgu.krouter.compiler.utils.Constants
import com.jamgu.krouter.compiler.utils.Logger
import com.jamgu.krouter.compiler.utils.isSubtypeOfType
import com.jamgu.krouter.compiler.utils.kpoet.`public final class`
import com.jamgu.krouter.compiler.utils.kpoet.`public static`
import com.jamgu.krouter.compiler.utils.kpoet.implements
import com.jamgu.krouter.compiler.utils.kpoet.javadoc
import com.jamgu.krouter.compiler.utils.kpoet.param
import com.jamgu.krouter.compiler.utils.kpoet.typeName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.annotation.processing.Filer
import javax.lang.model.type.TypeKind

/**
 * Created by jamgu on 2021/08/31
 */
internal class MethodRouterMappingCodeWriter(
    private val mLogger: Logger?,
    private val methodEntities: ArrayList<MethodRouterEntity>,
    private val moduleName: String?,
    private val filer: Filer?
) {

    fun write() {
        val classFileName = "${Constants.GENERATED_METHOD_ROUTER_CLASS_NAME_PREFIX}_$moduleName"
        val typeSpec = `public final class`(classFileName) {
            javadoc("This is generated code, please do not modify. \n")
            implements(ClassNameConstants.INTERFACES_CLASS)


            `public static`(TypeName.VOID, "map", param(ClassNameConstants.ANDROID_CONTEXT, "context")) {
                methodEntities.forEachIndexed { idx, entity ->
                    // mapping
                    addMethodMappingStatement(entity, this)
                    addCode("\n")

                }
                this
            }
        }

        JavaFile.builder("com.jamgu.krouter", typeSpec)
                .build()
                .writeTo(filer)
    }

    private fun addMethodMappingStatement(entity: MethodRouterEntity, builder: MethodSpec.Builder) {

        val paramString = entity.paramElements?.joinToString {
            when {
                isSubtypeOfType(it.asType(), "java.util.Map") -> "map"
                isSubtypeOfType(it.asType(), "com.jamgu.krouter.core.method.IAsyncMethodCallback") -> "callback"
                else -> {
                    mLogger?.error("this parameter type@${it.asType()} is not supported.")
                    ""
                }
            }
        }

        val returnType = when {
            entity.returnType?.kind == TypeKind.VOID -> VOID_CLASS
            entity.returnType?.typeName?.isPrimitive == true -> entity.returnType?.typeName?.box()
            else -> entity.returnType?.typeName
        }

        builder.addCode("\$T.mapMethod(\$S, new \$T() {\n" +
                "   public @SuppressWarnings(\"ALL\") \$T invoke(\$T map, \$T callback) {\n",
            KROUTER_METHOD_CLASS, entity.value, IMETHOD_INVOKER_CLASS, returnType,
            MAP_CLASS, IMETHOD_CALLBACK_CLASS)

        when(entity.returnType?.kind) {
            TypeKind.VOID -> {
                builder.addStatement("      \$T.\$N($paramString)", entity.className, entity.methodName)
                builder.addStatement("      return null")
            }
            else -> {
                builder.addStatement("      return \$T.\$N($paramString)", entity.className, entity.methodName)
            }
        }
        builder.addCode("   }\n" +
                "});")
    }


}