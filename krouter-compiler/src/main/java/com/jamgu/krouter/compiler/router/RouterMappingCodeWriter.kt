package com.jamgu.krouter.compiler.router

import com.jamgu.krouter.compiler.utils.ClassNameConstants
import com.jamgu.krouter.compiler.utils.ClassNameConstants.ANDROID_CONTEXT
import com.jamgu.krouter.compiler.utils.ClassNameConstants.PARAMTYPES_CLASS
import com.jamgu.krouter.compiler.utils.Constants.GENERATED_CLASS_NAME_PREFIX
import com.jamgu.krouter.compiler.utils.Constants.PREFIX_OF_LOGGER
import com.jamgu.krouter.compiler.utils.Logger
import com.jamgu.krouter.compiler.utils.isSubtypeOfType
import com.jamgu.krouter.compiler.utils.kpoet.`public final class`
import com.jamgu.krouter.compiler.utils.kpoet.`public static`
import com.jamgu.krouter.compiler.utils.kpoet.implements
import com.jamgu.krouter.compiler.utils.kpoet.javadoc
import com.jamgu.krouter.compiler.utils.kpoet.param
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import java.util.Locale
import javax.annotation.processing.Filer

/**
 * Created by jamgu on 2021/08/22
 */
internal class RouterMappingCodeWriter(
    private val mLogger: Logger?,
    private val routerEntities: ArrayList<RouterEntity>,
    private val moduleName: String?,
    private val filer: Filer?
) {

    fun write() {
        val classFileName = "${GENERATED_CLASS_NAME_PREFIX}_$moduleName"
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
                    mLogger?.error("$PREFIX_OF_LOGGER @KRouter annotation is not support to map class " +
                            "${entity.className.toString()}."
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
                    "$extraTypeVarName.set${typeName.capitalize(Locale.getDefault())}" +
                            "Extra(new String[]{$arrayContent})"
                )
            }
        }
    }
}