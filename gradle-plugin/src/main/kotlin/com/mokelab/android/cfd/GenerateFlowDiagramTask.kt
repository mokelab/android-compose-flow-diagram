package com.mokelab.android.cfd

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateFlowDiagramTask : DefaultTask() {

    @get:InputFiles
    abstract val kspGeneratedFiles: ConfigurableFileCollection

    @get:InputDirectory
    @get:Optional
    abstract val screenshotSourceDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outDir = outputDir.get().asFile
        val screenshotsDir = outDir.resolve("screenshots")

        outDir.deleteRecursively()
        outDir.mkdirs()
        screenshotsDir.mkdirs()

        // 1. スクリーンショットの同期
        val source = screenshotSourceDir.orNull?.asFile
        if (source?.exists() == true) {
            source.walkTopDown().filter { it.extension == "png" }.forEach { file ->
                val fileName = file.name
                val cleanName = fileName.replace(Regex("_[a-z0-9]{8}_"), "_")
                val relativePath =
                    file.relativeTo(source).parentFile?.path?.replace(File.separator, ".")
                val finalFileName =
                    if (relativePath != null) "$relativePath.$cleanName" else cleanName
                file.copyTo(File(screenshotsDir, finalFileName), overwrite = true)
            }
        }

        // 2. Markdownの集約
        kspGeneratedFiles.asFileTree.matching { include("**/*.md") }.forEach { file ->
            file.copyTo(File(outDir, file.name), overwrite = true)
        }
    }
}
