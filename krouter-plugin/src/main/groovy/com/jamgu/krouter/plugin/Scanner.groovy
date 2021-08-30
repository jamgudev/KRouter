package com.jamgu.krouter.plugin

import com.android.build.api.transform.JarInput
import com.google.common.collect.ImmutableList
import com.jamgu.krouter.plugin.utils.Constants
import com.jamgu.krouter.plugin.utils.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

class Scanner {
    // 当前app module的records
    static List<Record> records
    // 所有app module的records（当项目存在多个app module时）
    static final HashMap<String, List<Record>> recordsMap = [:]

    private static final Set<String> excludeJar = ["com.android.support", "android.arch.", "androidx."]

    static List<Record> getRecords(String name) {
        def records = recordsMap[name]
        if (records == null) {
            recordsMap[name] = ImmutableList.of(
                    new Record(Constants.Apt.ROUTER_INTERFACE_PACKAGE_NAME + Constants.Apt.ROUTER_INTERFACE_NAME)
            )
        }
        return recordsMap[name]
    }

    static boolean shouldScanJar(JarInput jarInput) {
        excludeJar.each {
            Logger.info("scan jar file name: " + jarInput.name)
            if (jarInput.name.contains(it)) {
                Logger.info("exclude jar file name: " + jarInput.name)
                return false
            }
        }
        return true
    }

    static boolean shouldScanClass(File classFile) {
        return classFile.absolutePath.replaceAll("\\\\", "/").contains(Constants.Apt.GENERATED_CLASS_PACKAGE_NAME)
    }

    /**
     * 扫描jar包
     */
    static void scanJar(File src, File dest) {
        if (src && src.exists()) {
            def jar = new JarFile(src)
            Enumeration enumeration = jar.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (entryName == Constants.REGISTER_CLASS_NAME) {
                    // mark
                    RouterTransform.registerTargetFile = dest
                } else if (entryName.startsWith(Constants.Apt.GENERATED_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = jar.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                }
            }
            jar.close()
        }
    }

    static void scanClass(File classFile) {
        scanClass(new FileInputStream(classFile))
    }

    /**
     * 扫描class
     */
    static void scanClass(InputStream is) {
        try {
            is.withCloseable {
                ClassReader cr = new ClassReader(is)
                ScanClassVisitor cv = new ScanClassVisitor()
                cr.accept(cv, 0)
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // ClassReader cr = new ClassReader(inputStream) may cause
            // ArrayIndexOutOfBoundsException when scanning Java Module
            // while plugin can still properly,
            // unknown reason, ignore this exception for now
            // do nothing
        } catch (Exception e) {
            Logger.error(e.toString() + ": " + e.getMessage())
        }
    }

    static class ScanClassVisitor extends ClassVisitor {
        ScanClassVisitor() {
            super(Opcodes.ASM5)
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            if (interfaces != null) {
                records.each { record ->
                    interfaces.each { interfaceName ->
                        if (interfaceName == record.templateName) {
                            record.aptClasses.add(name)
                            Logger.info("found class $name implements interface $interfaceName, now add this class to @Record:$record.templateName's aptClasses, " +
                                    "current aptClasses size = ${record.aptClasses.size()}")
                        }
                    }
                }
            }
        }
    }
}