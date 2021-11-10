package com.jamgu.krouter.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.google.common.io.Files
import com.jamgu.krouter.plugin.utils.Constants
import com.jamgu.krouter.plugin.utils.Logger
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.concurrent.Callable

class RouterTransform extends Transform {
    static File registerTargetFile = null

    Project project

    WaitableExecutor waitableExecutor

    RouterTransform(Project project) {
        this.project = project
        this.waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool()
    }

    @Override
    String getName() {
        return Constants.PLUGIN_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        long begin = System.currentTimeMillis()
        Logger.info("transform begin:")
        boolean isIncremental = transformInvocation.incremental
        if (!isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        Scanner.records = Scanner.getRecords(project.name)

        transformInvocation.inputs.each { TransformInput input ->
            if (!input.jarInputs.empty) {
                Logger.info("transform jar start: isIncremental = $isIncremental")
                input.jarInputs.each { JarInput jarInput ->
                    File destFile = getJarDestFile(transformInvocation, jarInput)
                    if (isIncremental) {
                        Status status = jarInput.getStatus()
                        switch (status) {
                            case Status.NOTCHANGED:
                                break
                            case Status.ADDED:
                            case Status.CHANGED:
                                execute {
                                    transformJar(jarInput, destFile)
                                }
                                break
                            case Status.REMOVED:
                                execute {
                                    if (destFile.exists()) {
                                        FileUtils.forceDelete(destFile)
                                    }
                                }
                                break
                        }
                    } else {
                        execute {
                            transformJar(jarInput, destFile)
                        }
                    }
                }
            }

            if (!input.directoryInputs.empty) {
                Logger.info("transform directory start: isIncremental = $isIncremental")
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    if (isIncremental) {
                        // 增量编译的情况下，我们获取的对象是一个Map，
                        // 而非增量编译的情况下，我们使用的是整个文件夹路径
                        String srcDirPath = directoryInput.getFile().getAbsolutePath()
                        String destDirPath = dest.getAbsolutePath()
                        Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()
                        for (Map.Entry<File, Status> changedFile : fileStatusMap.entrySet()) {
                            Status status = changedFile.getValue()
                            File inputFile = changedFile.getKey()
                            String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath)
                            File destFile = new File(destFilePath)
                            switch (status) {
                                case Status.NOTCHANGED:
                                    break
                                case Status.ADDED:
                                case Status.CHANGED:
                                    execute {
                                        try {
                                            FileUtils.touch(destFile)
                                        } catch (IOException ignored) {
                                            Files.createParentDirs(destFile)
                                        }
                                        transformSingleFile(inputFile, destFile)
                                    }
                                    break
                                case Status.REMOVED:
                                    execute {
                                        if (destFile.exists()) {
                                            FileUtils.deleteQuietly(destFile)
                                        }
                                    }
                                    break
                            }
                        }
                    } else {
                        execute {
                            transformDir(directoryInput, dest)
                        }
                    }

                }
            }
        }
        waitableExecutor.waitForAllTasks()

        // 找到了RouterMappingInitiator.class 向其注入代码
        if (registerTargetFile) {
            Logger.info("found register target file, location: $registerTargetFile," +
                    " start to insert register code to this file")
            Knife.handle()
        } else {
            Logger.warning("register target file not found.")
        }
        Logger.notify("transform finished, cost time: ${(System.currentTimeMillis() - begin)}ms.")
    }

    static File getJarDestFile(TransformInvocation transformInvocation, JarInput jarInput) {
        String destName = jarInput.name
        if (destName.endsWith(".jar")) { // local jar
            // rename to avoid the same name, such as classes.jar
            String hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
            destName = "${destName.substring(0, destName.length() - 4)}_${hexName}"
        }
        File destFile = transformInvocation.outputProvider.getContentLocation(
                destName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        return destFile
    }

    static void transformSingleFile(File inputFile, File destFile) {
        if (inputFile.isFile() && Scanner.shouldScanClass(inputFile)) {
            Scanner.scanClass(inputFile)
        }
        FileUtils.copyFile(inputFile, destFile)
    }

    static void transformJar(JarInput jarInput, File destFile) {
        // com.android.support:appcompat-v7:27.1.1 (/path/to/xxx.jar)
        if (Scanner.shouldScanJar(jarInput)) {
            Scanner.scanJar(jarInput.file, destFile)
        }

        FileUtils.copyFile(jarInput.file, destFile)
    }


    static void transformDir(DirectoryInput directoryInput, File dest) {
        directoryInput.file.eachFileRecurse { File file ->
            if (file.isFile() && Scanner.shouldScanClass(file)) {
                Scanner.scanClass(file)
            }
        }

        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    void execute(Closure closure) {
        waitableExecutor.execute(new Callable<Void>() {
            @Override
            Void call() throws Exception {
                closure()
                return null
            }
        })
    }
}