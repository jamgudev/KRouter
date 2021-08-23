package com.jamgu.krouterplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.jamgu.krouterplugin.utils.Logger
import com.jamgu.krouterplugin.utils.ScanSetting
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * find our KRouterInit.kt file and insert some code into its init() method during compiling
 */
class RouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        //only application module needs this plugin to generate register code
        if (isApp) {
            Logger.make(project)

            Logger.i('Project enable router-register plugin')

            def android = project.extensions.getByType(AppExtension)
            def transformImpl = new RegisterTransform(project)

            ArrayList<ScanSetting> list = new ArrayList<>()
            list.add(new ScanSetting("IKRouterMapping"))
            RegisterTransform.registerList = list
            android.registerTransform(transformImpl)
        }
    }

}
