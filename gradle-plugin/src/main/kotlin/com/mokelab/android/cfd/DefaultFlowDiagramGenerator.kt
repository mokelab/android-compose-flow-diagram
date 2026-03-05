package com.mokelab.android.cfd

import java.io.File

class DefaultFlowDiagramGenerator : FlowDiagramGenerator {

    override fun generate(
        kspMarkdownFiles: Iterable<File>,
        screenshotSource: File?,
        outputDir: File,
    ) {
        val screenshotsDir = outputDir.resolve("screenshots")

        outputDir.deleteRecursively()
        outputDir.mkdirs()
        screenshotsDir.mkdirs()

        // 1. スクリーンショットの同期
        if (screenshotSource?.exists() == true) {
            val hashRegex = Regex("_[a-z0-9]{8}_")
            screenshotSource.walkTopDown().filter { it.extension == "png" }.forEach { file ->
                val fileName = file.name
                val cleanName = fileName.replace(hashRegex, "_")
                val relativePath =
                    file.relativeTo(screenshotSource).parentFile?.path?.replace(File.separator, ".")
                val finalFileName =
                    if (relativePath != null) "$relativePath.$cleanName" else cleanName
                file.copyTo(File(screenshotsDir, finalFileName), overwrite = true)
            }
        }

        // 2. Markdownの集約
        kspMarkdownFiles.forEach { file ->
            file.copyTo(File(outputDir, file.name), overwrite = true)
        }
    }
}
