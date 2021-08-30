package com.jamgu.krouter.plugin.utils

import org.gradle.api.Project

class Logger {
    static org.gradle.api.logging.Logger logger

    static boolean isLogEnable = false

    static void make(Project project, Boolean loggable) {
        logger = project.getLogger()
        isLogEnable = loggable
    }

    static void notify(String info) {
        if (null != info && null != logger) {
            System.out.println("KRouter::plugin info >>> " + info)
        }
    }

    static void info(String info) {
        if (null != info && null != logger && isLogEnable) {
            System.out.println("KRouter::plugin info >>> " + info)
        }
    }

    static void error(String error) {
        if (null != error && null != logger) {
            System.out.println("KRouter::plugin error >>> " + error)
        }
    }

    static void warning(String warning) {
        if (null != warning && null != logger && isLogEnable) {
            System.out.println("KRouter::plugin warn >>> " + warning)
        }
    }
}
