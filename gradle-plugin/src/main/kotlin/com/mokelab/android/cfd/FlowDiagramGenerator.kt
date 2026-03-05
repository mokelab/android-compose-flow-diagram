package com.mokelab.android.cfd

import java.io.File

interface FlowDiagramGenerator {
    fun generate(
        kspMarkdownFiles: Iterable<File>,
        screenshotSource: File?,
        outputDir: File,
    )
}
