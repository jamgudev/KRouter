package com.jamgu.kroutercompiler.router

import com.grosner.kpoet.`public final class`
import com.grosner.kpoet.`public static`
import com.grosner.kpoet.implements
import com.grosner.kpoet.javadoc
import com.grosner.kpoet.param
import com.jamgu.kroutercompiler.utils.ClassNameConstants
import com.jamgu.kroutercompiler.utils.ClassNameConstants.ANDROID_CONTEXT
import com.jamgu.kroutercompiler.utils.ClassNameConstants.KROUTER_NAME
import com.jamgu.kroutercompiler.utils.ClassNameConstants.PARAMTYPES_CLASS
import com.jamgu.kroutercompiler.utils.isSubtypeOfType
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Created by jamgu on 2021/08/22
 */
internal class RouterMappingCodeWriter(
    private val messager: Messager?,
    private val routerEntities: ArrayList<RouterEntity>,
    private val filer: Filer?
) {

    companion object {
        const val GENERATED_CLASS_NAME_PREFIX = "KRouterMapping"
    }

    fun write() {

        val classFileName = GENERATED_CLASS_NAME_PREFIX
        val typeSpec = `public final class`(classFileName) {
            javadoc("This is generated code, please do not modify. \n")
            implements(ClassNameConstants.INTERFACES_CLASS)

            `public static`(TypeName.VOID, "map", param(ANDROID_CONTEXT, "context")) {

                routerEntities.forEachIndexed { idx, entity ->

                    var paramTypeName = "null"
                    if (entity.hasExtraData()) {
                        paramTypeName = "p$idx"

                        val typeName = PARAMTYPES_CLASS
                        // init
                        addStatement("\$T $paramTypeName = new \$T()", typeName, typeName)

                        // set paramTypes
                        addSetParamDataStatements(entity, this, paramTypeName)
                    }

                    // mapping
                    addUriMappingStatement(entity, this, paramTypeName)

                }
                this
            }
        }

        JavaFile.builder("com.jamgu.krouter", typeSpec)
                .build()
                .writeTo(filer)
    }

    private fun addUriMappingStatement(
        entity: RouterEntity,
        builder: MethodSpec.Builder, paramTypeName: String
    ) {
        entity.value?.forEach { uri ->
            when {
                isSubtypeOfType(entity.classNameTypeMirror, "android.app.Activity") -> {
                    builder.addStatement(
                        "\$T.mapActivity(\$S, \$T.class, $paramTypeName)",
                        ClassNameConstants.KROUTER_CLASS, uri, entity.className
                    )
                }
                else -> {
                    messager?.printMessage(
                        Diagnostic.Kind.ERROR,
                        "@$KROUTER_NAME annotation is not support to map class ${entity.className.toString()}."
                    )
                }
            }
        }
    }

    private fun addSetParamDataStatements(
        routerEntity: RouterEntity,
        mb: MethodSpec.Builder, extraTypeVarName: String
    ) {
        if (routerEntity.hasNonStringParams()) {
            listOf(
                Pair("boolean", routerEntity.booleanParams),
                Pair("byte", routerEntity.byteParams),
                Pair("char", routerEntity.charParams),
                Pair("double", routerEntity.doubleParams),
                Pair("float", routerEntity.floatParams),
                Pair("int", routerEntity.intParams),
                Pair("long", routerEntity.longParams),
                Pair("short", routerEntity.shortParams)
            ).forEach { pair ->
                addSetParamDataStatement(mb, pair.first, pair.second, extraTypeVarName)
            }
        }
    }

    private fun addSetParamDataStatement(
        mb: MethodSpec.Builder,
        typeName: String, args: Array<String>?,
        extraTypeVarName: String
    ) {
        if (args?.isNotEmpty() == true) {
            val arrayContent = args.joinToString { if (it.isNotBlank()) "\"$it\"" else "" }

            if (arrayContent.isNotBlank()) {
                mb.addStatement(
                    "$extraTypeVarName.set${typeName.capitalize()}" +
                            "Extra(new String[]{$arrayContent})"
                )
            }
        }
    }
}