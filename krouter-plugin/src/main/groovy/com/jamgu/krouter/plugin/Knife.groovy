package com.jamgu.krouter.plugin

import com.jamgu.krouter.plugin.utils.Constants
import com.jamgu.krouter.plugin.utils.Logger
import org.apache.commons.io.IOUtils
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class Knife {
    static void handle() {
        File targetFile = RouterTransform.registerTargetFile
        assert targetFile != null && targetFile.exists()

        if (targetFile.name.endsWith(".jar")) {
            def optJar = new File(targetFile.getParent(), targetFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
            def jarFile = new JarFile(targetFile)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
            Enumeration enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.name
                ZipEntry zipEntry = new ZipEntry(entryName) // new entry
                jarOutputStream.putNextEntry(zipEntry)
                jarFile.getInputStream(jarEntry).withCloseable { is ->
                    if (entryName == Constants.REGISTER_CLASS_NAME) { // find RouterMappingInitiator.class
                        Logger.i('Insert init code to class >> ' + entryName)
                        def bytes = modifyClass(is)
                        jarOutputStream.write(bytes)
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(is))
                    }
                    jarOutputStream.closeEntry()
                }
            }
            jarOutputStream.close()
            jarFile.close()

            targetFile.delete()
            optJar.renameTo(targetFile)
        } else if (targetFile.name.endsWith(".class")) { // 一般不会走到这里，因为RouterMappingInitiator位于jar包中
            modifyClass(new FileInputStream(targetFile))
        }
    }

    private static byte[] modifyClass(InputStream inputStream) {
        inputStream.withCloseable { is ->
            ClassReader cr = new ClassReader(is)
            ClassWriter cw = new ClassWriter(cr, 0)
            ClassVisitor cv = new AptClassVisitor(cw)
            cr.accept(cv, 0)
            return cw.toByteArray()
        }
    }

    /**
     * Delegate static code block
     */
    private static class AptClassVisitor extends ClassVisitor {
        AptClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            // generate code into this method
            if (name == Constants.REGISTER_CLASS_METHOD_NAME) {
                mv = new InitMethodVisitor(mv)
            }
            return mv
        }
    }

    private static class InitMethodVisitor extends MethodVisitor {
        InitMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

        @Override
        void visitInsn(int opcode) {
            // generate code before return
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                Scanner.records.each { record ->
                    record.aptClasses.each { className ->
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        //name = name.replaceAll("/", ".")
                        Logger.i("found apt class $className, insert register " +
                                "code to RouterMappingInitiator.$Constants.REGISTER_CLASS_METHOD_NAME()")
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, Constants.REGISTER_METHOD_NAME, "(Landroid/content/Context;)V", false)
                    }
                }
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }
}