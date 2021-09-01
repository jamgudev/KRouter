package com.jamgu.krouter.compiler.utils

/**
 * Created by jamgu on 2021/08/24
 */
internal object Constants {

    const val PROJECT_NAME = "KRouter"
    const val KROUTER_METHOD_MAP_ENTER_CLASS_NAME = "MethodRouters"     // KRouters.map..

    const val GENERATED_CLASS_NAME_PREFIX = "KRouterMapping"
    const val GENERATED_METHOD_ROUTER_CLASS_NAME_PREFIX = "MethodRouterMapping"

    // log
    const val PREFIX_OF_LOGGER = "$PROJECT_NAME::compiler >>> "
    // Options of processor
    const val KEY_MODULE_NAME = "KRouter_Module_Name"
    const val KEY_COMPILE_LOGGABLE = "KRouter_Compile_Loggable"

    const val NO_MODULE_NAME_TIPS = " No module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [$KEY_MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n"


}