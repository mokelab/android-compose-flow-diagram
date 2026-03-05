plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
    id("com.mokelab.android.cfd") version "1.0.0"
}

android {
    namespace = "com.mokelab.android.cfd.docs.preview"
    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt()) {
            minorApiLevel = libs.versions.compileSdkMinor.get().toInt()
        }
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(projects.sample.feature.login)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    screenshotTestImplementation(projects.sample.feature.login)
    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// docs-preview/build.gradle.kts

/*
tasks.register("generateComposeFlowDiagram") {
    group = "documentation"
    description =
        "Aggregates KSP-generated Markdown and synced screenshots into a single directory."

    // 1. 入力：スクリーンショット (Google公式の出力先)
    val screenshotSourceDir = file("src/screenshotTestDebug/reference")

    // 2. 入力：KSPで生成されたMarkdown (debugビルドの場合)
    // ※ codeGenerator.createNewFile(..., packageName = "", ...) とした場合のパスです
    val kspOutputDir = layout.buildDirectory.dir("generated/ksp/debugScreenshotTest/resources")

    // 3. 出力：ドキュメント集約先
    val outputDirProvider = layout.buildDirectory.dir("compose-flow")
    val outputScreenshotsDir = outputDirProvider.map { it.dir("screenshots") }

    // Gradleに依存関係と入出力を教える
    inputs.dir(screenshotSourceDir)
    inputs.dir(kspOutputDir)
    outputs.dir(outputDirProvider)

    // KSPタスクが終わってから動くように依存関係を設定
    dependsOn("kspDebugScreenshotTestKotlin")

    doLast {
        val outputDir = outputDirProvider.get().asFile
        val screenshotsDir = outputScreenshotsDir.get().asFile

        // フォルダのクリーンアップ
        outputDir.deleteRecursively()
        outputDir.mkdirs()
        screenshotsDir.mkdirs()

        // --- A. スクリーンショットの同期 (ハッシュ除去 + フラット化) ---
        if (screenshotSourceDir.exists()) {
            screenshotSourceDir.walkTopDown().filter { it.extension == "png" }.forEach { file ->
                val fileName = file.name
                val cleanName = fileName.replace(Regex("_[a-z0-9]{8}_"), "_")
                val relativePath = file.relativeTo(screenshotSourceDir).parentFile?.path?.replace(
                    File.separator,
                    "."
                )
                val finalFileName =
                    if (relativePath != null) "$relativePath.$cleanName" else cleanName
                file.copyTo(File(screenshotsDir, finalFileName), overwrite = true)
            }
        }

        // --- B. Markdownファイルのコピー ---
        val kspDir = kspOutputDir.get().asFile
        if (kspDir.exists()) {
            kspDir.walkTopDown().filter { it.extension == "md" }.forEach { file ->
                // build/compose-flow/ 直下にコピー
                file.copyTo(File(outputDir, file.name), overwrite = true)
            }
        }

        logger.lifecycle("✅ Compose Flow Diagram generated at: ${outputDir.absolutePath}")
    }
}
 */
