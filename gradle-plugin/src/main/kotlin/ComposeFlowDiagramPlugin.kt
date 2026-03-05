import com.mokelab.android.cfd.GenerateFlowDiagramTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register

class ComposeFlowDiagramPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. 必要なプラグイン (KSP) を自動適用
        project.pluginManager.apply("com.google.devtools.ksp")

        // 2. 依存関係を自動追加
        project.dependencies {
            add("implementation", "com.mokelab.android:compose-flow-diagram-annotations:1.0.0")
            add("ksp", "com.mokelab.android:compose-flow-diagram-processor:1.0.0")
        }

        // 3. タスクの登録
        val generateTask =
            project.tasks.register<GenerateFlowDiagramTask>("generateComposeFlowDiagram") {
                outputDir.set(project.layout.buildDirectory.dir("compose-flow"))
                screenshotSourceDir.set(project.file("src/screenshotTestDebug/reference"))
            }

        // 4. 各バリアント（debug等）のKSPタスクと紐付け
        project.afterEvaluate {
            val kspTask = project.tasks.matching {
                it.name == "kspDebugScreenshotTestKotlin" || it.name == "kspDebugKotlin"
            }
            generateTask.configure {
                kspGeneratedFiles.from(kspTask.map { it.outputs.files })
            }

            // スクリーンショット検証後に自動実行
            project.tasks.matching { it.name == "validateDebugScreenshotTest" }.configureEach {
                finalizedBy(generateTask)
            }
        }
    }
}