package com.jamgu.krouterplugin.utils

import com.jamgu.krouterplugin.RegisterTransform
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

class ScanUtil {

    /**
     * find our jar file that has KRouterInit.kt
     *
     * @param jarFile All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    static void scanJar(File jarFile, File destFile) {
        if (jarFile) {
//            Logger.i("ScanUtil scan jar file "+jarFile)
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    // mark this jar file contains KRouterInit.class
                    // After the scan is complete, we will generate register code into this file
                    RegisterTransform.fileContainsInitClass = destFile
                } else if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                }
            }
            file.close()
        }
    }

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null && entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    /**
     * find all the classes that implements IKRouterMapping
     * @param class file
     */
    static void scanClass(File file) {
        scanClass(new FileInputStream(file))
    }

    static void scanClass(InputStream inputStream) {
        try {
            ClassReader cr = new ClassReader(inputStream)
            ClassWriter cw = new ClassWriter(cr, 0)
            ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            inputStream.close()
        } catch (Exception e) {
            Logger.e(e.getMessage())
        }
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            RegisterTransform.registerList.each { ext ->
                if (ext.interfaceName && interfaces != null) {
                    interfaces.each { itName ->
                        if (itName == ext.interfaceName) {
                            ext.classList.add(name)
                        }
                    }
                }
            }
        }
    }

}