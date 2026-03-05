package com.mokelab.android.cfd

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

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
        DefaultFlowDiagramGenerator().generate(
            kspMarkdownFiles = kspGeneratedFiles.asFileTree.matching { include("**/*.md") },
            screenshotSource = screenshotSourceDir.orNull?.asFile,
            outputDir = outputDir.get().asFile,
        )
    }
}
