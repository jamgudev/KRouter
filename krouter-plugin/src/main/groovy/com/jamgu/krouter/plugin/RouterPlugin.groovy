package com.jamgu.krouter.plugin

import com.android.build.gradle.*
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.jamgu.krouter.plugin.utils.Constants
import com.jamgu.krouter.plugin.utils.Logger
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

class RouterPlugin implements Plugin<Project> {

    String DEFAULT_ROUTER_CORE_VERSION = Constants.Ext.DEFAULT_ROUTER_CORE_VERSION
    String DEFAULT_ROUTER_COMPILER_VERSION = Constants.Ext.DEFAULT_ROUTER_COMPILER_VERSION

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)                    // com.android.application
                && !project.plugins.hasPlugin(LibraryPlugin)         // com.android.library
                && !project.plugins.hasPlugin(TestPlugin)            // com.android.test
                && !project.plugins.hasPlugin(DynamicFeaturePlugin)) // com.android.dynamic-feature, added in 3.2
        {
            throw new GradleException("android plugin required.")
        }

        // org.gradle.api.internal.plugins.DefaultExtraPropertiesExtension
        ExtraPropertiesExtension ext = project.rootProject.ext

        boolean loggable = false
        if (ext.has(Constants.Ext.ROUTER_COMPILE_LOGGABLE)) {
            loggable = ext.get(Constants.Ext.ROUTER_COMPILE_LOGGABLE)
        }
        Logger.make(project, loggable)

        // get ext configuration
        if (ext.has(Constants.Ext.ROUTER_CORE_VERSION)) {
            DEFAULT_ROUTER_CORE_VERSION = ext.get(Constants.Ext.ROUTER_CORE_VERSION)
        }
        if (ext.has(Constants.Ext.ROUTER_COMPILER_VERSION)) {
            DEFAULT_ROUTER_COMPILER_VERSION = ext.get(Constants.Ext.ROUTER_COMPILER_VERSION)
        }

        boolean isAutoAddDependency = true
        if (ext.has(Constants.Ext.ROUTER_AUTO_ADD_DEPENDENCY)) {
            isAutoAddDependency = ext.get(Constants.Ext.ROUTER_AUTO_ADD_DEPENDENCY)
        }

        // add dependencies
        addDependence(project, isAutoAddDependency)

        if (project.plugins.hasPlugin(AppPlugin)) {
            def android = project.extensions.getByType(AppExtension)
            def transform = new RouterTransform(project)
            android.registerTransform(transform)
        }
    }

    private void addDependence(Project project, Boolean enable) {
        if (!enable) {
            return
        }

        // https://github.com/JetBrains/kotlin/tree/master/libraries/tools/kotlin-gradle-plugin/src/main/resources/META-INF/gradle-plugins
        def isKotlinProject = project.plugins.hasPlugin('kotlin-android') || project.plugins.hasPlugin('org.jetbrains.kotlin.android')
        if (isKotlinProject) {
            if (!project.plugins.hasPlugin('kotlin-kapt') && !project.plugins.hasPlugin('org.jetbrains.kotlin.kapt')) {
                project.plugins.apply('kotlin-kapt')
            }
        }

        String aptConf = 'annotationProcessor'
        if (isKotlinProject) {
            aptConf = 'kapt'
        }

        Logger.notify("add krouter-core dependence: [v${DEFAULT_ROUTER_CORE_VERSION}] to module: [${project.getName()}]")
        project.dependencies.add('implementation',
                "io.github.jamgudev:krouter-core:${DEFAULT_ROUTER_CORE_VERSION}")

        Logger.notify("add krouter-compiler dependence: [v${DEFAULT_ROUTER_COMPILER_VERSION}] to module: [${project.getName()}]")
        project.dependencies.add(aptConf,
                "io.github.jamgudev:krouter-compiler:${DEFAULT_ROUTER_COMPILER_VERSION}")

        // apt options
        BaseExtension android = project.extensions.findByName("android")
        if (android) {
            Map<String, String> options = [
                    (Constants.Opt.MODULE_NAME): project.name,
                    (Constants.Opt.LOGGABLE): Logger.isLogEnable.toString(),
            ]
            android.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments(options)
            android.productFlavors.all { ProductFlavor flavor ->
                flavor.javaCompileOptions.annotationProcessorOptions.arguments(options)
            }
        }

        // https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/src/main/kotlin/org/jetbrains/kotlin/gradle/plugin/KaptExtension.kt
        def kapt = project.extensions.findByName("kapt")
        if (kapt) {
            kapt.arguments({
                arg(Constants.Opt.MODULE_NAME, project.name)
                arg(Constants.Opt.LOGGABLE, Logger.isLogEnable.toString())
            })
        }
    }
}