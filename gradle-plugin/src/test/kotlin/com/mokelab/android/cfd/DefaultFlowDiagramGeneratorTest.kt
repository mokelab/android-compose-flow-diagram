package com.mokelab.android.cfd

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.nio.file.Files

class DefaultFlowDiagramGeneratorTest : StringSpec({

    val generator = DefaultFlowDiagramGenerator()

    fun tempDir() = Files.createTempDirectory("cfd-test").toFile()

    "outputDir and screenshotsDir are created" {
        val outDir = tempDir()
        try {
            outDir.deleteRecursively()
            generator.generate(emptyList(), null, outDir)
            outDir.exists().shouldBeTrue()
            outDir.resolve("screenshots").exists().shouldBeTrue()
        } finally {
            outDir.deleteRecursively()
        }
    }

    "no error when screenshotSource is null" {
        val outDir = tempDir()
        try {
            generator.generate(emptyList(), null, outDir)
        } finally {
            outDir.deleteRecursively()
        }
    }

    "no error when screenshotSource is a non-existent directory" {
        val outDir = tempDir()
        val nonExistentSource = outDir.resolve("no-such-dir")
        try {
            generator.generate(emptyList(), nonExistentSource, outDir)
        } finally {
            outDir.deleteRecursively()
        }
    }

    "PNG files are copied to screenshotsDir" {
        val sourceDir = tempDir()
        val outDir = tempDir()
        try {
            sourceDir.resolve("ScreenA.png").writeText("png-data")

            generator.generate(emptyList(), sourceDir, outDir)

            outDir.resolve("screenshots/ScreenA.png").exists().shouldBeTrue()
        } finally {
            sourceDir.deleteRecursively()
            outDir.deleteRecursively()
        }
    }

    "hash part of file name is removed" {
        val sourceDir = tempDir()
        val outDir = tempDir()
        try {
            sourceDir.resolve("LoginScreen_abc12345_preview.png").writeText("png-data")

            generator.generate(emptyList(), sourceDir, outDir)

            outDir.resolve("screenshots/LoginScreen_preview.png").exists().shouldBeTrue()
        } finally {
            sourceDir.deleteRecursively()
            outDir.deleteRecursively()
        }
    }

    "subdirectory path is added to file name with dot separator" {
        val sourceDir = tempDir()
        val outDir = tempDir()
        try {
            val subDir = sourceDir.resolve("feature/login").also { it.mkdirs() }
            subDir.resolve("LoginScreen.png").writeText("png-data")

            generator.generate(emptyList(), sourceDir, outDir)

            outDir.resolve("screenshots/feature.login.LoginScreen.png").exists().shouldBeTrue()
        } finally {
            sourceDir.deleteRecursively()
            outDir.deleteRecursively()
        }
    }

    "non-PNG files are not copied to screenshotsDir" {
        val sourceDir = tempDir()
        val outDir = tempDir()
        try {
            sourceDir.resolve("notes.txt").writeText("text-data")
            sourceDir.resolve("icon.webp").writeText("webp-data")

            generator.generate(emptyList(), sourceDir, outDir)

            val screenshots = outDir.resolve("screenshots").listFiles() ?: emptyArray()
            screenshots.map { it.name }.shouldContainExactlyInAnyOrder(emptyList())
        } finally {
            sourceDir.deleteRecursively()
            outDir.deleteRecursively()
        }
    }

    "md files are copied to outputDir" {
        val outDir = tempDir()
        val mdDir = tempDir()
        try {
            val mdFile = mdDir.resolve("LoginScreen.md").also { it.writeText("# Login") }

            generator.generate(listOf(mdFile), null, outDir)

            outDir.resolve("LoginScreen.md").exists().shouldBeTrue()
            outDir.resolve("LoginScreen.md").readText() shouldBe "# Login"
        } finally {
            outDir.deleteRecursively()
            mdDir.deleteRecursively()
        }
    }

    "existing files in outputDir are deleted before generate" {
        val outDir = tempDir()
        try {
            outDir.resolve("stale.md").writeText("stale content")

            generator.generate(emptyList(), null, outDir)

            outDir.resolve("stale.md").exists() shouldBe false
        } finally {
            outDir.deleteRecursively()
        }
    }
})
