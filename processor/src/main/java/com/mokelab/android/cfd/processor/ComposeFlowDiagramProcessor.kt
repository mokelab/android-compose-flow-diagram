package com.mokelab.android.cfd.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

class ComposeFlowDiagramProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private val generator = MermaidContentGenerator()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.mokelab.android.cfd.ScreenNode")
            .filterIsInstance<KSFunctionDeclaration>()

        val unableToProcess = symbols.filterNot { it.validate() }.toList()

        // 1. ファイル（KSFile）ごとにグループ化
        val fileGroups = symbols.filter { it.validate() }.groupBy { it.containingFile }

        // 2. 各ファイルグループに対して .md を生成
        fileGroups.forEach { (ksFile, functions) ->
            if (ksFile != null) {
                generateMermaidFilePerKotlinFile(ksFile, functions)
            }
        }

        return unableToProcess
    }

    private fun generateMermaidFilePerKotlinFile(
        ksFile: KSFile,
        functions: List<KSFunctionDeclaration>
    ) {
        // 元のKotlinファイル名を取得（例: LoginScreen.kt -> LoginScreen）
        val originalFileName = ksFile.fileName.removeSuffix(".kt")
        val nodes = functions.mapNotNull { function ->
            val annotation = function.annotations.find { it.shortName.asString() == "ScreenNode" }
                ?: return@mapNotNull null
            val displayName =
                annotation.arguments.find { it.name?.asString() == "displayName" }?.value as? String
                    ?: ""
            val routesTo =
                (annotation.arguments.find { it.name?.asString() == "routesTo" }?.value as? List<*>)?.filterIsInstance<String>()
                    ?: emptyList()
            ScreenNodeInfo(
                functionName = function.simpleName.asString(),
                packageName = function.packageName.asString(),
                displayName = displayName,
                routesTo = routesTo
            )
        }

        // 3. Kotlinファイル名に基づいた .md ファイルを作成
        // 出力先: build/generated/ksp/debug/resources/ (packageNameを指定しない場合)
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false, ksFile),
            packageName = "",
            fileName = "${originalFileName}Flow",
            extensionName = "md"
        )
        file.write(generator.generate(originalFileName, nodes).toByteArray())
        file.close()
    }
}